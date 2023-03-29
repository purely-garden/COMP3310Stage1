import java.io.*;
import java.net.*;

public class MyServer {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(6666);
            Socket s = ss.accept();// establishes connection
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String str = (String) dis.readUTF();

            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            System.out.println("Client Message: " + str);
            if (str.equals("HELO")) {
                System.out.println("Reply: GDAY");
                dout.writeUTF("GDAY");
                dout.flush();
            }

            str = (String) dis.readUTF();
            System.out.println("Client Message: " + str);
            System.out.println("Reply: " + str);
            dout.writeUTF(str);
            dout.flush();

            ss.close();
            System.out.println("Connection Closed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}