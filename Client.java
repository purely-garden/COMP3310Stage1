
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import custom_exception.HandshakeException;
import custom_exception.NoEmploymentException;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Client {
    static String replier = "C RCVD ";
    static String sendier = "C SENT ";
    static String serverOK = "OK";

    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 50000);
                BufferedReader dataIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());) {
            String reply;
            String username = System.getProperty("user.name");

            System.out.println(sendier + "HELO");
            dataOut.write(("HELO\n").getBytes());
            dataOut.flush();
            reply = dataIn.readLine();
            System.out.println(replier.concat(reply));
            if (!isOk(reply)) {
                throw new HandshakeException("HELO -> server reply is not OK");
            }

            System.out.println(sendier + "AUTH " + username);
            dataOut.write(("AUTH " + username + "\n").getBytes());
            dataOut.flush();
            reply = dataIn.readLine();
            System.out.println(replier.concat(reply));
            if (!isOk(reply)) {
                throw new HandshakeException("AUTH -> server reply is not OK");
            }

            Job curJob = doREDY(dataIn, dataOut, reply);
            if (curJob.isNONE()) {
                throw new NoEmploymentException("Get an Employment", new Throwable("REDY:NONE"));
            }

            System.out.println(sendier + "GETS All");
            dataOut.write(("GETS All\n").getBytes());
            dataOut.flush();
            reply = dataIn.readLine();
            System.out.println(replier.concat(reply));
            Pattern getsPattern = Pattern.compile("DATA (\\d+) (\\d+)");
            Matcher dataMatcher = getsPattern.matcher(reply);
            int nRecs = 1;
            int recLen = 124;
            if (dataMatcher.find()) {
                nRecs = Integer.parseInt(dataMatcher.group(1));
                recLen = Integer.parseInt(dataMatcher.group(2));
            }
            System.out.println(sendier + "OK");
            dataOut.write(("OK\n").getBytes());
            dataOut.flush();
            String[] getsServerRecords = new String[nRecs];
            for (int i = 0; i < nRecs; i++) {
                reply = dataIn.readLine();
                getsServerRecords[i] = reply;
                System.out.println(replier.concat(getsServerRecords[i]));
            }
            System.out.println(sendier + "OK");
            dataOut.write(("OK\n").getBytes());
            dataOut.flush();
            reply = dataIn.readLine();
            System.out.println(replier.concat(reply));

            Servers serverList = new Servers(getsServerRecords);
            String largestServerType = serverList.findLargest();
            int largestServerCount = serverList.getLargestServerCount();
            String schd = "SCHD";
            String SPACE = " ";
            int LRRCount = 0;
            while (!curJob.isNONE()) {
                if (curJob.getCode().compareTo("JOBN") == 0) {
                    StringBuilder strBuild = new StringBuilder(schd).append(SPACE);
                    strBuild.append(curJob.id).append(SPACE);
                    strBuild.append(largestServerType).append(SPACE);
                    strBuild.append(LRRCount);
                    System.out.println(sendier + strBuild.toString());
                    dataOut.write(strBuild.append("\n").toString().getBytes());
                    dataOut.flush();
                    reply = dataIn.readLine();
                    System.out.println(replier.concat(reply));
                    curJob = doREDY(dataIn, dataOut, reply);
                    LRRCount = (LRRCount + 1) % largestServerCount;
                } else if (!curJob.isNONE() && curJob.getCode().compareTo("JCPL") == 0) {
                    System.out.println(sendier + "REDY");
                    dataOut.write(("REDY\n").getBytes());
                    dataOut.flush();
                    reply = dataIn.readLine();
                    System.out.println(replier.concat(reply));
                    if (reply.compareTo("NONE") == 0) {
                        break;
                    }
                    curJob = doREDY(dataIn, dataOut, reply);
                }
            }

            dataOut.write(("QUIT\n").getBytes());
            dataOut.flush();
            System.out.println(sendier + "QUIT");
            reply = dataIn.readLine();
            System.out.println(replier.concat(reply));

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static boolean isOk(String s) {
        return s.compareTo(serverOK) == 0;
    }

    static Job doREDY(BufferedReader dataIn, DataOutputStream dataOut, String reply) throws IOException {
        System.out.println(sendier + "REDY");
        dataOut.write(("REDY\n").getBytes());
        dataOut.flush();
        reply = dataIn.readLine();
        System.out.println(replier.concat(reply));
        return new Job(reply);
    }

}
