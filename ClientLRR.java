import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import custom_exception.HandshakeException;

public class ClientLRR {
    static String replier = "Reply: ";
    static String serverOK = "OK";

    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 50000);
                BufferedReader dataIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());) {
            String reply;
            String username = "ClientUser1";

            System.out.println("HELO");
            dataOut.write(("HELO\n").getBytes());
            dataOut.flush();
            reply = dataIn.readLine();
            System.out.println(replier.concat(reply));
            if (!isOk(reply)) {
                throw new HandshakeException("HELO -> server reply is not OK");
            }

            System.out.println("AUTH " + username);
            dataOut.write(("AUTH " + username + "\n").getBytes());
            dataOut.flush();
            reply = dataIn.readLine();
            System.out.println(replier.concat(reply));
            if (!isOk(reply)) {
                throw new HandshakeException("AUTH -> server reply is not OK");
            }

            System.out.println("REDY");
            dataOut.write(("REDY\n").getBytes());
            dataOut.flush();
            reply = dataIn.readLine();
            // JOBN 37 0 653 3 700 3800
            // submitTime jobID estRuntime resource-Requirements(core memory disk)
            Pattern redyPattern = Pattern.compile("[[:alnum:]]+");
            Matcher redyMatcher = redyPattern.matcher(reply);
            redyMatcher.find();
            String jobMsg = redyMatcher.group();
            System.out.println("jobmsg " + jobMsg);
            int[] jobInfo = new int[6];
            for(int k = 0; k < 6; k++) {
                if(redyMatcher.find()) {
                    jobInfo[k] = Integer.parseInt(redyMatcher.group(0));
                    System.out.println("JobInfo ["+k+"] " + jobInfo[k]);
                }
            }
            System.out.println(replier.concat(reply));

            System.out.println("GETS All");
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
            System.out.println("OK");
            dataOut.write(("OK\n").getBytes());
            dataOut.flush();
            String[] getsAllStrings = new String[nRecs];
            for (int i = 0; i < nRecs; i++) {
                reply = dataIn.readLine();
                getsAllStrings[i] = reply;
                System.out.println("Record ["+i+"] " + getsAllStrings[i]);
            }
            System.out.println("OK");
            dataOut.write(("OK\n").getBytes());
            dataOut.flush();



            System.out.println("QUIT");
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

}
