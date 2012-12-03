package itu.dk.smds.e2012.common;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.jgroups.*;

/**
 *
 * @author Alexander
 */
public class TokenService extends ReceiverAdapter {
    private static Encrypter tokenServiceServerEncrypter;
    private static HashMap<String,Encrypter> tokenServiceClientEncrypters;
    
    public static Encrypter getNewEncrypter(String username) {
        Encrypter newEnc = Encrypter.getInstance();
        tokenServiceClientEncrypters.put(username, newEnc);
        return newEnc;
    }
    
    public static Encrypter connectToServer() {
        Encrypter newEnc = Encrypter.getInstance();
        tokenServiceServerEncrypter = newEnc;
        return newEnc;
    }

    public static String getNewToken(String host, String passwd) {
        String token;
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
            String unsafeToken = user + host + ", " + timestamp;
            String serverCryptedToken = tokenServiceServerEncrypter.encryptClearText(unsafeToken);
            token = tokenServiceClientEncrypters.get(user).encryptClearText(serverCryptedToken);
        } catch(Exception e) {
            // System.out.println(e);
            token = null;
        }
        
        return token;
    }
}
