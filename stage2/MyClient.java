
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.*;
import java.io.*;

public class MyClient {
    private static final String SPACE = " ";
 
    public String readMsg(int array, BufferedInputStream bis) {
        try {
            byte[] b = new byte[array];
            bis.read(b);
            return new String(b);
        } catch (Exception e) {
            //System.out.println(e);
        }
        return "error";
    }
    
    private static List<Server> parseServer(String[] fileAddress){
        List<Server> dsServerList = new ArrayList<>();
        int count=0;
        while(fileAddress.length!=count){
            String[] serverDetail = fileAddress[count].split(SPACE);
            dsServerList.add(new Server(
                serverDetail[0],
                Integer.parseInt(serverDetail[1]),
                Integer.parseInt(serverDetail[4]),
                Integer.parseInt(serverDetail[5]), 
                Integer.parseInt(serverDetail[6]),
                Integer.parseInt(serverDetail[7])
            ));
            count++;
        }
        return dsServerList;
    }

    private static List<String> parseJOBNMessage(String ServerReply){
        List<String> info = new ArrayList<>();
        String[] splitinfo = ServerReply.split(SPACE);
        if (splitinfo[0].equals("JCPL")) {
            info.add(splitinfo[0]);
        } else if (splitinfo[0].equals("JOBN")){
            info.add(splitinfo[0]);
            info.add(splitinfo[2]);
            info.add(splitinfo[4]);
            info.add(splitinfo[5]);
            info.add(splitinfo[6]);
        } else info.add(splitinfo[0]);
        return info;
    }
    private static String createSCHDString(int jobID, String serverType, int serverID){
        return "SCHD" + SPACE + jobID + SPACE + serverType + SPACE + serverID;
    }
    private static String handleGetsCapable(String core, String memory, String disk){
    	return "GETS Capable" + SPACE + core + SPACE+ memory +SPACE + disk;
    }
    public static void main(String[] args) throws Exception{
        try {
            Socket s = new Socket("127.0.0.1", 50000);
            DataInputStream din =  new DataInputStream(s.getInputStream());
            DataOutputStream dout =  new DataOutputStream(s.getOutputStream());
            BufferedInputStream bin = new BufferedInputStream(din);
            BufferedOutputStream bout = new BufferedOutputStream(dout);

            MyClient mc = new MyClient();

            bout.write("HELO".getBytes());
            bout.flush();
            mc.readMsg(2, bin);

            bout.write("AUTH dac".getBytes());
            bout.flush();
            mc.readMsg(2, bin);

            int jobID=-1;
            boolean isMoreJob = true;
            while (isMoreJob){
                bout.write("REDY".getBytes());
                bout.flush();

                String job = mc.readMsg(64, bin);
                List<String> info = parseJOBNMessage(job);  

                if (info.get(0).equals("JOBN")){
                    jobID++;
                    bout.write(handleGetsCapable(info.get(2), info.get(3), info.get(4)).getBytes());
                    bout.flush();
                    mc.readMsg(64, bin);
                    //get all jobs after gets capable
                    bout.write("OK".getBytes());
                    bout.flush();
                    String[] JOBNSplit = mc.readMsg(10000, bin).split("\n");
    
                    bout.write("OK".getBytes());
                    bout.flush();
                    mc.readMsg(2, bin);
                    
                    //------main Processor----------
                    //parseServer and store it in Server Class.
                    List<Server> serverList = parseServer(JOBNSplit);
                    //sort in accesnding order
                    if (args[0].equals("ff")){
                        Collections.sort(serverList, new Comparator<Server>() {
                            @Override
                            public int compare(Server a, Server b) {
                                return a.getServerCore() - b.getServerCore();
                            }
                        });
                        boolean is_signed = false;
                        //find the first and best available server
                        for (Server sver:serverList) {
                            if (sver.getIsRunning()==0 && sver.getServerCore()>=Integer.parseInt(info.get(2))) {
                                bout.write(createSCHDString(jobID, sver.getServerType(), sver.getTypeCount()).getBytes());
                                bout.flush();
                                is_signed=true;
                                break;
                            }
                        }
                        //if all others busy and un available then assign to the best one.
                        if (is_signed==false){
                            for (Server sver:serverList) {
                                if (sver.getServerCore()>=Integer.parseInt(info.get(2))) {
                                    bout.write(createSCHDString(jobID, sver.getServerType(), sver.getTypeCount()).getBytes());
                                    bout.flush();
                                    is_signed=true;
                                    break;
                                }
                            }
                        }
                        //if others fail to assign, then assigned to the first server find.
                        if (is_signed==false) {
                            bout.write(createSCHDString(jobID, serverList.get(0).getServerType(), serverList.get(0).getTypeCount()).getBytes());
                            bout.flush();
                        }
                    }
                    //sort in desending order 
                    else if (args[0].equals("wf")) {
                        Collections.sort(serverList, new Comparator<Server>() {
                            @Override
                            public int compare(Server a, Server b) {
                                return b.getServerCore() - a.getServerCore();
                            }
                        });
                        boolean is_signed = false;
                        //find the first and best available server
                        for (Server sver:serverList) {
                            if (sver.getIsRunning()==0 && sver.getServerCore()>=Integer.parseInt(info.get(2))) {
                                bout.write(createSCHDString(jobID, sver.getServerType(), sver.getTypeCount()).getBytes());
                                bout.flush();
                                is_signed=true;
                                break;
                            }
                        }
                        //if all others busy and un available then assign to the best one.
                        if (is_signed==false){
                            for (Server sver:serverList) {
                                if (sver.getServerCore()>=Integer.parseInt(info.get(2))) {
                                    bout.write(createSCHDString(jobID, sver.getServerType(), sver.getTypeCount()).getBytes());
                                    bout.flush();
                                    is_signed=true;
                                    break;
                                }
                            }
                        }
                        //if others fail to assign, then assigned to the first server find.
                        if (is_signed==false) {
                            bout.write(createSCHDString(jobID, serverList.get(0).getServerType(), serverList.get(0).getTypeCount()).getBytes());
                            bout.flush();
                        }
                    }
                    mc.readMsg(2, bin);
                    bout.write("OK".getBytes());
                    bout.flush();
                    mc.readMsg(2, bin);
                }
                else if (info.get(0).equals("JCPL")) {
                    continue;
                }
                else {
                    isMoreJob = false;
                }
            }
            bout.write("QUIT".getBytes());
            bout.flush();
        } catch(IOException ex){
            Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
            //parseDSSystem_xml_file(ADDRESS);
        }
    }
}
