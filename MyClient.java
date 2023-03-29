import java.io.*;
import java.net.*;

public class MyClient {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 50000);
            BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String str;

            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.write(("HELO\n").getBytes());
            dout.flush();
            str = (String) dis.readLine();
            System.out.println("Server Reply: " + str);

            dout.write(("AUTH arbi\n").getBytes());
            dout.flush();
            str = (String) dis.readLine();
            System.out.println("Server Reply: " + str);

            dout.write(("REDY\n").getBytes());
            dout.flush();
            str = (String) dis.readLine();
            System.out.println("Server Reply: " + str);

            dout.write(("QUIT\n").getBytes());
            dout.flush();
            str = (String) dis.readLine();
            System.out.println("Server Reply: " + str);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
