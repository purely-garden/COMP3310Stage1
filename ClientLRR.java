import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientLRR {
    static BufferedReader dataIn;
    static DataOutputStream dataOut;

    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 50000);
            dataIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            dataOut = new DataOutputStream(s.getOutputStream());
            String reply;
            String replier = "Server Reply: ";
            String serverOK = "OK";
            String username = "ClientUser1";

            dataOut.write(("HELO\n").getBytes());
            dataOut.flush();
            reply = (String) dataIn.readLine();
            if (reply.compareTo(serverOK) == 0) {
                dataOut.write(("AUTH " + username + "\n").getBytes());
                dataOut.flush();
            } else {
                throw new Exception("w");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static String readReply() {
        return null;
    }
}
