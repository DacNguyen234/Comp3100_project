import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;



public class MyClientScheduleJob {
    public static String QUIT = "QUIT";
    public static char[] HI = {'H', 'E','L', 'O'};
    public int coreCount = -1;

    public String readMsg(byte[] b, BufferedInputStream bis) {
        try {
            bis.read(b);
            String str = new String(b, StandardCharsets.UTF_8);
            return str;
        } catch (Exception e) {
            System.out.println(e);
        }
        return "error";
    }
    public static void main (String args[])  {
        try {
            Socket s = new Socket("localhost", 50000);
            DataInputStream din =  new DataInputStream(s.getInputStream());
            DataOutputStream dout =  new DataOutputStream(s.getOutputStream());
            BufferedInputStream bin = new BufferedInputStream(din);
            BufferedOutputStream bout = new BufferedOutputStream(dout);
            System.out.println("Connected with the server");

            MyClientScheduleJob csj = new MyClientScheduleJob();

	    //Send HELO and reponse
            bout.write("HELO".getBytes());
            bout.flush();
            String serverReply = csj.readMsg(new byte[32], bin);
            System.out.println("Received in response to HELO: " + serverReply);

	    //Send AUTH and reponse
            bout.write("AUTH GROUP20".getBytes());
            bout.flush();
            serverReply = csj.readMsg(new byte[32], bin);
            System.out.println("Received in response to AUTH: " + serverReply);

	    //Send REDY and reponse
            bout.write("REDY".getBytes());
            bout.flush();
            serverReply = csj.readMsg(new byte[32], bin);
            System.out.println("Received in response to REDY: " + serverReply);

	    //Send GETSALL and reponse
            bout.write("GETS All".getBytes());
            bout.flush();
            serverReply = csj.readMsg(new byte[32], bin);
            System.out.println("Received in response to GETS ALL: " +serverReply);

            bout.write("OK".getBytes());
            bout.flush();

	    //get size
            String[] message_space = serverReply.split(" ");
            serverReply = csj.readMsg(new byte[Integer.parseInt(message_space[1])*Integer.parseInt(message_space[2])], bin);
	    /*
            String[] arrOfStr = serverReply.split("\n");
            for(String server: arrOfStr) {  //missing
                String[] indiServer = server.split(" ");
            }
            
	    //Send OK and reponse
            bout.write("OK".getBytes());
            bout.flush();
            serverReply = csj.readMsg(new  byte[1], bin);
            System.out.println("Received in response to OK is a dot: " + serverReply);

            bout.write("SCHD 110 joon 0".getBytes());
            bout.flush();

            serverReply = csj.readMsg(new byte[100], bin);
            System.out.println("Received in response to SCHD: " + serverReply);
            bout.write("REDY".getBytes());
            bout.flush();
            serverReply = csj.readMsg(new byte[32], bin);
            System.out.println("Received in response to REDY: " + serverReply);

            bout.write(QUIT.getBytes());
            bout.flush();

            serverReply = csj.readMsg(new byte[32], bin);
            System.out.println("Received in response to QUIT: " + serverReply);

            if(serverReply.equals(QUIT)) {
                bout.close();
                dout.close();
                s.close();
            }
            */
        }catch(Exception e) {
            System.out.println(e);
        }
    }
}
