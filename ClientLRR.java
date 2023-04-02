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
            System.out.println(replier.concat(reply));

            System.out.println("GETS All");
            dataOut.write(("GETS All\n").getBytes());
            dataOut.flush();
            reply = dataIn.readLine();
            String getsAllData = reply;
            System.out.println(replier.concat(reply));
            Pattern getsPattern = Pattern.compile("DATA (\\d+) (\\d+)");
            Matcher dataMatcher = getsPattern.matcher(getsAllData);
            int nRecs = 1;
            int recLen = 124;
            if (dataMatcher.find()) {
                nRecs = Integer.parseInt(dataMatcher.group(1));
                recLen = Integer.parseInt(dataMatcher.group(2));
            }
            System.out.println("OK");
            dataOut.write(("OK\n").getBytes());
            dataOut.flush();
            for (int i = 0; i < nRecs; i++) {
                reply = dataIn.readLine();
                System.out.println(replier.concat(reply));
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

    static String readReply() {
        return null;
    }

    static boolean isOk(String s) {
        return s.compareTo(serverOK) == 0;
    }

}
