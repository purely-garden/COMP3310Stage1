import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import custom_exception.HandshakeException;
import custom_exception.NoEmploymentException;

public class ClientLRR {
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
            ArrayList<String> largestServers = (ArrayList<String>) serverList.findLargest();
            System.out.println(largestServers);
            String schd = "SCHD";
            String SPACE = " ";

            while (curJob.isNONE()) {
                int LRRCount = 0;
                StringBuilder strBuild = new StringBuilder(schd).append(SPACE);
                strBuild.append(curJob.id).append(SPACE);
                strBuild.append(largestServers.get(LRRCount)).append(SPACE);
                strBuild.append(LRRCount);
                System.out.println(strBuild.toString());

                dataOut.write(strBuild.toString().getBytes());
                dataOut.flush();
                reply = dataIn.readLine();
                System.out.println(replier.concat(reply));
                curJob = doREDY(dataIn, dataOut, reply);
            }

            System.out.println(sendier + "QUIT");
            dataOut.write(("QUIT\n").getBytes());
            dataOut.flush();
            reply = dataIn.readLine();
            System.out.println(reply.concat(reply));

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static boolean isOk(String s) {
        return s.compareTo(serverOK) == 0;
    }

    static Job doREDY(BufferedReader dataIn, DataOutputStream dataOut, String reply) throws IOException {
        System.out.println(sendier+"REDY");
        dataOut.write(("REDY\n").getBytes());
        dataOut.flush();
        reply = dataIn.readLine();
        System.out.println(replier.concat(reply));
        return new Job(reply);
    }

    static boolean doREDY(BufferedReader dataIn, DataOutputStream dataOut, String reply, int[] jobInfo, String jobMsg)
            throws IOException {
        System.out.println(sendier+"REDY");
        dataOut.write(("REDY\n").getBytes());
        dataOut.flush();
        reply = dataIn.readLine();
        System.out.println(replier.concat(reply));

        Pattern redyPattern = Pattern.compile("[a-z0-9]+", Pattern.CASE_INSENSITIVE);
        Matcher redyMatcher = redyPattern.matcher(reply);
        redyMatcher.find();
        jobMsg = redyMatcher.group();
        System.out.println("jobmsg " + jobMsg);
        if (jobMsg.compareTo("NONE") == 0) {
            return false;
        }
        jobInfo = new int[6];
        for (int k = 0; k < 6; k++) {
            if (redyMatcher.find()) {
                jobInfo[k] = Integer.parseInt(redyMatcher.group(0));
                System.out.print("JobInfo");
                System.out.print(" [" + k + "] " + jobInfo[k]);
            }
        }
        System.out.println();
        return true;
    }
}
