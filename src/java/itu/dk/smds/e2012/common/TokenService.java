package itu.dk.smds.e2012.common;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import org.jgroups.*;

/**
 *
 * @author Alexander
 */
public class TokenService extends ReceiverAdapter {
    private static Encrypter tokenServiceServerEncrypter;
    private JChannel tokenChannel;
    

    private void start() throws Exception{
        tokenChannel = new JChannel();
        tokenChannel.setReceiver(this);                   
        tokenChannel.connect("ServerCluster");
        eventLoop();
        tokenChannel.close();
    }
    
    private void eventLoop(){    
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
            System.out.print("> "); System.out.flush();
            String line=in.readLine().toLowerCase();
                if(line.startsWith("end") || line.startsWith("close")) {
                    break;
                }
            } catch(Exception e) {
            }
        }
    }
    
    @Override
    public void viewAccepted(View new_view){
        System.out.println("** view: " + new_view);
    }
    
    @Override
    public void receive(Message msg){
        try {
            
            Object[] rec = (Object[]) msg.getObject();
            if(!("SENC".equals((String) rec[0]) || "GetT".equals((String) rec[0]))){
                return;
            }
            if("SENC".equals((String) rec[0])){
                tokenServiceServerEncrypter = Encrypter.getInstance((String)rec[1]);
                System.out.println("Server Password Arrived");
            } else if ("GetT".equals((String) rec[0])) {
                String token = getNewToken((String)rec[1],(String) rec[2]);
                Message tokenMsg = new Message(null, null, new Object[] {"NewT", token});
                tokenChannel.send(tokenMsg);
            }
        } catch (Exception e){
            System.out.println("Error while parsing command, TokenService");
            // Send message back to client using "send"
        }
    } 

    private static String getNewToken(String host, String passwd) {
        String token;
        Encrypter userEnc = Encrypter.getInstance(passwd);
        
        try {
            JSch jsch = new JSch();
            
            String user = host.substring(0, host.indexOf('@'));
            host = host.substring(host.indexOf('@')+1);
            System.out.println("Host: " + host +  " User: " + user);
            
            Session session = jsch.getSession(user, host, 22);
            
            session.setPassword(passwd);
            
            session.connect(30000);   // making a connection with timeout.
            
            Date date = new Date();
            String timestamp = date.toString();
            String unsafeToken = user + "@"+ host + ", " + timestamp;
            String serverCryptedToken = tokenServiceServerEncrypter.encryptClearText(unsafeToken);
            token = userEnc.encryptClearText(serverCryptedToken);
        } catch(Exception e) {
            System.out.println(e);
            token = null;
        }
        
        return token;
    }
    
    public static void main(String[] args){
        try{
            new TokenService().start();
        } catch(Exception e){
            System.out.println("Error occured starting token service");
        }
    }
}