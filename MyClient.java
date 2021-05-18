import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException ;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyClient {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 5000;
    private static final String HELO = "HELO\n";
    private static final String REDY = "REDY\n";
    private static final String OK = "OK\n";
    private static final String NONE = "NONE\n";
    private static final String AUTH = "AUTH DAC\n";
    private static final String CAPABLE = "GETS CAPABLE";
    private static final String DSSystem_xml_file = "ds-system.xml";
    private static final String SPACE = " ";
    private static Socket socket;
    private static DataInputStream  fi;
    private static DataOutputStream fo;

    private static void handshake(DataInputStream fi, DataOutputStream fo){
        try {
            fo.write(HELO.getBytes());
            String reply = fi.readLine();
            System.out.println("Server says" + reply);
            fo.write(AUTH.getBytes());
            reply = fi.readLine();
            System.out.println("Server say"+  reply);
        } catch (IOException ex){
            Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static List<Server> parseDSSystem_xml_file(String fielAddress){
        List<Server> dsServerList = new ArrayList<>();
        return dsServerList;
    }

    private static List<String> parseJOBNMessage(String ServerReply){
        List<String> info = new ArrayList<>();
        String[] splitinfo = ServerReply.split(SPACE);
        info.add(splitinfo[2]);
        info.add(splitinfo[4]);
        info.add(splitinfo[5]);
        info.add(splitinfo[6]);
        return info;
    }
    private static String createSCHDString(String jobID, int serverID, String serverType){
        return "SHCD" + SPACE + jobID + SPACE + serverType + SPACE + serverID;
    }
    private static String handleGetsCapable(String core, String memory, String disk){
    	return CAPABLE + SPACE + core + SPACE+ memory +SPACE + disk;
    }

    public static void main(String[] args){
        try {
            socket = new Socket(ADDRESS, PORT);
            fi = new DataInputStream(socket.getInputStream());
            fo = new DataOutputStream(socket.getOutputStream());
            handshake(fi, fo);
            List<Server> dsServers = parseDSSystem_xml_file(ADDRESS);

            fo.write(REDY.getBytes());
            String reply = fi.readLine();
			System.out.println("server says: "+ reply);

            while (!reply.equals(NONE)){
                List<String> parseInfo = parseJOBNMessage(reply);
                String tmp = handleGetsCapable(parseInfo.get(1),parseInfo.get(2),parseInfo.get(3));
                reply = fi.readLine();
                System.out.println(reply);
                
                
                //get capables xx xx xx
                //will get DATA xx xx
                //send ok
                //will list job
                
                /*
                Server server = dsServers.get(0);
                int serverID = 0;
                String jobID = parseInfo.get(0);
                createSCHDString(parseInfo[0], sever.getServerType());

                fo.write(REDY.getBytes());
                reply = fi.readLine();
                system.out.println("Server says: " + reply)
                */
            }
        } catch(IOException ex){
            Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
            parseDSSystem_xml_file(ADDRESS);
        }
    }
}
