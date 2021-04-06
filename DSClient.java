import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;



public class DSClient {

    static final String HELO = "HELO";
    static final String OK = "OK";
    static final String REDY = "REDY";
    static final String GETSAVAIL = "GETS Avail";
    static final String GETSALL = "GETS All";
    static final String QUIT = "QUIT";

    public static void main(String args[]){
        try {
        Socket s = new Socket("localhost", 50000);
        DataInputStream din = new DataInputStream(s.getInputStream());
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        String reply;
        
        dout.write(HELO.getBytes());
        dout.flush();
        byte[] replyBytes = new byte[2];
        din.read(replyBytes);
        reply = new String(replyBytes, StandardCharsets.UTF_8);
        System.out.println("Server replies: " + reply);

        String auth = "AUTH user";
        dout.write(auth.getBytes());
        dout.flush();

        byte[] replyBytes2 = new byte[2];
        din.read(replyBytes2);
        reply = new String(replyBytes2, StandardCharsets.UTF_8);
        System.out.println("Server replies: " + reply);
        dout.write(REDY.getBytes());
        dout.flush();

        Boolean jobsLeft = true;

        String SCHD;

        String largestServer = null;
        String availCommand;

        while(jobsLeft) {
            byte[] groupOfBytes = new byte[50];
            din.read(groupOfBytes);
            reply = new String(groupOfBytes, StandardCharsets.UTF_8);
            System.out.println("Server replies: " + reply);

            if(reply.substring(0,4).equals("NONE") || reply.substring(0,4).equals(QUIT)) {
                jobsLeft = false;
                dout.write(QUIT.getBytes());
                dout.flush();
                break;
            }

            if(!(reply.substring(0,4).equals("JOBN") || reply.substring(0,4).equals("JOBP"))) {
                    dout.write(OK.getBytes());
                    dout.flush();
                    dout.write(REDY.getBytes());
                    dout.flush();
                
                continue;
            }

            else if(reply.substring(0, 4).equals("JOBN")) {
                String[] JOBNSplit = reply.split("\\s+");
                int JobSubmitTime = Integer.parseInt(JOBNSplit[1]);
                int JobID = Integer.parseInt(JOBNSplit[2]);
                String JobRunTime = JOBNSplit[3];
                String JobCores = JOBNSplit[4];
                String JobMemory = JOBNSplit[5];
                String JobDisk = JOBNSplit[6];

                dout.write(GETSALL.getBytes());
                dout.flush();

                byte[] groupOfBytes1 = new byte[20];
                din.read(groupOfBytes1);
                reply = new String(groupOfBytes1, StandardCharsets.UTF_8);
                System.out.println("Server replies: " + reply);

                dout.write(OK.getBytes());
                dout.flush();

                String[] DataSplit = reply.split(" ");
                byte[] groupOfBytes2 = new byte[Integer.parseInt(DataSplit[1]) * 124];
                din.read(groupOfBytes2);
                reply = new String(groupOfBytes2, StandardCharsets.UTF_8);
                System.out.println("Server replies: " + reply);

                if(largestServer == null){
                    String[] ServerSplit = reply.split("\\n+");
                    String biggestServer = ServerSplit[0];

                    for(int i = 0; i < ServerSplit.length; i++) {
                        String[] ServerSplitWord = ServerSplit[i].split("\\s+");
                        String[] BigSplitWord = biggestServer.split("\\s+");
                        int currentCore = Integer.parseInt(ServerSplitWord[4]);
                        int bestCore = Integer.parseInt(BigSplitWord[4]);
                        if(currentCore > bestCore) {
                            biggestServer = ServerSplit[i];
                        }
                    }
                    String[] bigSplit = biggestServer.split("\\s+");
                    largestServer = bigSplit[0] + " " + bigSplit[1];
                }
                dout.write(OK.getBytes());
                dout.flush();

                byte[] groupOfBytes_2 = new byte[1];
                din.read(groupOfBytes_2);
                reply = new String(groupOfBytes_2, StandardCharsets.UTF_8);
                System.out.println("Server replies: " + reply);

            SCHD = "SCHD " + JobID + " " + largestServer + " 0";
            dout.write(SCHD.getBytes());
            dout.flush();
            System.out.println("The biggest server is: " + largestServer);

            byte[] groupOfBytes3 = new byte[32];
            din.read(groupOfBytes3);
            reply = new String(groupOfBytes3, StandardCharsets.UTF_8);
            System.out.println("Server replies: " + reply);
            
            dout.write(REDY.getBytes());
            dout.flush();
            }
        }

        byte[] groupOfBytes4 = new byte[32];
        din.read(groupOfBytes4);
        reply = new String(groupOfBytes4, StandardCharsets.UTF_8);
        System.out.println("Server replies: " + reply);
        if(reply.equals(QUIT)) {
            dout.close();
            s.close();
        }
    }
    catch(Exception e) {
        System.out.println(e);
}
}
}
    

