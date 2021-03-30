import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;  
class stage1{  
    public static void main(String args[])throws Exception{  
        Socket s=new Socket("127.0.0.1",50000);   
        DataInputStream din=new DataInputStream(s.getInputStream());  
        DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
        String str;
        byte[] byteArray;

        //send HELO
        str = "HELO";
        dout.write(str.getBytes());
        dout.flush();
        System.out.println("Client has send " + str + " to sever"); 

        //recieved OK
        byteArray = new byte[din.available()];
        din.read(byteArray);
        str = new String(byteArray,StandardCharsets.UTF_8);
        System.out.println(str);

        //send AUTH
        str = "AUTHDac";
        dout.write(str.getBytes());
        dout.flush();
        System.out.println("Client has send " + str + " to sever"); 

        //recieved welcome message "OK"
        byteArray = new byte[din.available()];
        din.read(byteArray);
        str = new String(byteArray,StandardCharsets.UTF_8);
        System.out.println(str);

        //read from sever
        str = "REDY";
        dout.write(str.getBytes());
        dout.flush();
        System.out.println("Client has send " + str + " to sever"); 

        

        dout.close();  
        s.close();  
    }
}  