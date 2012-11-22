package itu.dk.smds.e2012.common;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.jgroups.*;

/**
 *
 * @author Alexander
 */
public class TokenService extends ReceiverAdapter {
    private Encrypter tokenServiceServerEncrypter;
    private HashMap<String,Encrypter> tokenServiceClientEncrypters;
    private static JChannel channel;
    
    public TokenService() {
        try {
            channel = new JChannel();
            channel.setReceiver(this);
            channel.connect("ServerCluster1");
            eventLoop();
            channel.close();
        } catch (Exception e) {
        }
        
    }
    
    private void eventLoop()
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        while (true) {
            
        }
    }
    
    public void connectServerEncrypter(Encrypter encrypter) {
        this.tokenServiceServerEncrypter = encrypter;
    }
    
    public void connectClientEncrypter(String username, Encrypter encrypter) {
        this.tokenServiceClientEncrypters.put(username, encrypter);
    }
    
    public String authenticateToken(TaskManagerTCPClient tcpC) throws Exception {
        String token = getNewToken();
        if (token != null)
        {
            return token;
        } else {
            throw new Exception();
        }
    }
    
    public String getNewToken() {
        String host = JOptionPane.showInputDialog("Enter username@hostname",
                        System.getProperty("user.name")
                        + "@localhost");
        
        String passwd = JOptionPane.showInputDialog("Enter password:");
        
        return getNewToken(host, passwd);
    }

    public String getNewToken(String host, String password) {
        String token;
        try {
            JSch jsch = new JSch();
            
            String user = host.substring(0, host.indexOf('@'));
            host = host.substring(host.indexOf('@')+1);
            System.out.println("Host: " + host +  " User: " + user);
            
            Session session = jsch.getSession(user, host, 22);
            
            session.setPassword(password);
            
            session.connect(30000);   // making a connection with timeout.
            
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String timestamp = df.format(date);
            String unsafeToken = user + host + ", " + timestamp;
            token = tokenServiceServerEncrypter.encryptClearText(unsafeToken);
        } catch(Exception e) {
            // System.out.println(e);
            token = null;
        }
        
        return token;
    }
    
    public static void main(String[] args) {
        new TokenService();
    }
}