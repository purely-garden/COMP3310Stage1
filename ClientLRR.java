import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
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
                System.out.println("Connection retry.");
                for (int i = 1; i <= 3; i++) {
                    System.out.println("HELO");
                    dataOut.write(("HELO\n").getBytes());
                    dataOut.flush();
                    reply = dataIn.readLine();
                    System.out.println(replier.concat(reply));

                    if (isOk(reply)) {
                        break;
                    } else if (i == 3) {
                        throw new HandshakeException("HELO -> server reply is not OK");
                    }
                }
            }

            System.out.println("AUTH " + username);
            dataOut.write(("AUTH " + username + "\n").getBytes());
            dataOut.flush();
            

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
