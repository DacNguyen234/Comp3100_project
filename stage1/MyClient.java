import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;

public class MyClient {
    public static String HELO = "HELO";
    public static String AUTH = "AUTH dac";
    public static String REDY = "REDY";
    public static String QUIT = "QUIT";

    public MyClient(){   }

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

    public static void main (String args[]) throws Exception {
        try {
            Socket s = new Socket("localhost", 50000);
            DataInputStream din =  new DataInputStream(s.getInputStream());
            DataOutputStream dout =  new DataOutputStream(s.getOutputStream());
            BufferedInputStream bin = new BufferedInputStream(din);
            BufferedOutputStream bout = new BufferedOutputStream(dout);
            System.out.println("Connected with the server");

            MyClient csj = new MyClient();

            Boolean jobsLeft = true;

            String largestServer = null;

            bout.write(HELO.getBytes());
            System.out.println("Sent HELO to the server");
            bout.flush();

            String serverReply = csj.readMsg(new byte[2], bin);
            System.out.println("Received in response to HELO: " + serverReply);

            bout.write(AUTH.getBytes());
            bout.flush();

            serverReply = csj.readMsg(new byte[2], bin);
            System.out.println("Received in response to AUTH: " + serverReply);

            bout.write(REDY.getBytes());
            bout.flush();

            while(jobsLeft) {
            
                serverReply = csj.readMsg(new byte[64], bin);
                System.out.println("Received in response to REDY: " + serverReply);

                if(serverReply.substring(0,4).equals("NONE") || serverReply.substring(0,4).equals(QUIT)) {
                    jobsLeft = false;
                    bout.write(QUIT.getBytes());
                    bout.flush();
                    break;
                }

                if(!(serverReply.substring(0,4).equals("JOBN"))) {
                    bout.write(REDY.getBytes());
                    bout.flush();
                    continue;
                }

                else if(serverReply.substring(0, 4).equals("JOBN")) {
                    String[] JOBNSplit = serverReply.split("\\s+");
                    int JobID = Integer.parseInt(JOBNSplit[2]);

                    bout.write("GETS All".getBytes());
                    bout.flush();

                    serverReply = csj.readMsg(new byte[32], bin);
                    System.out.println("Received in response to GETS All: " + serverReply);

                    bout.write("OK".getBytes());
                    bout.flush();

                    String[] message_space = serverReply.split(" ");

                    String str = new String();
                    for(int i = 0; i < message_space[2].length(); i++) {
                        char c = message_space[2].charAt(i);
                        if(c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' ||c == '6' || c == '7' || c == '8' || c == '9')
                            str += c;
                        else
                            break;
                    }
                    
                    serverReply = csj.readMsg(new byte[Integer.parseInt(message_space[1])*Integer.parseInt(str)], bin);
                    
                    //System.out.println(serverReply);

                    String[] arrOfStr = serverReply.split("\n");
                    if(largestServer == null){
                        String biggestServer = arrOfStr[0];

                        for(int i = 0; i < arrOfStr.length; i++) {
                            String[] ServerSplitWord = arrOfStr[i].split("\\s+");
                            String[] BigSplitWord = biggestServer.split("\\s+");
                            int currentCore = Integer.parseInt(ServerSplitWord[4]);
                            int bestCore = Integer.parseInt(BigSplitWord[4]);
                            if(currentCore > bestCore) {
                                biggestServer = arrOfStr[i];
                            }
                        }
                        String[] bigSplit = biggestServer.split("\\s+");
                        largestServer = bigSplit[0];
                    }
                    
                    bout.write("OK".getBytes());
                    bout.flush();

                    serverReply = csj.readMsg(new  byte[1], bin);
                    System.out.println("Received in response to OK is a dot: " + serverReply);

                    String SCHD = "SCHD" + " " + JobID + " " + largestServer + " " + "0";
                    bout.write(SCHD.getBytes());
                    bout.flush();
                    System.out.println("The biggest server is: " + largestServer);
                    serverReply = csj.readMsg(new byte[2], bin);
                    System.out.println("Received in response to SCHD: " + serverReply);
                    
                    bout.write(REDY.getBytes());
                    bout.flush();

                }
            }
            serverReply = csj.readMsg(new byte[32], bin);
            System.out.println("Received in response to QUIT: " + serverReply);

            if(serverReply.equals(QUIT)) {
                bout.close();
                dout.close();
                bin.close();
                din.close();
                s.close();
            }
        }catch(Exception e) {
            System.out.println(e);
        }
    }
}
