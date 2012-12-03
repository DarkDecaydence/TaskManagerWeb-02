package itu.dk.smds.e2012.common;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.UnsupportedEncodingException;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 *
 * @author Alexander
 */
public class Encrypter {

    private PBEParameterSpec pbeParameterSpec;
    private PBEKeySpec pbeKeySpec;
    private SecretKeyFactory keyFac;
    private SecretKey pbeKey;
    private Cipher pbeCipher;
    
    //Salt
    byte[] salt = {
        (byte) 0x21, (byte) 0xa2, (byte) 0xf4, (byte) 0x6b,
        (byte) 0x87, (byte) 0xcd, (byte) 0x4e, (byte) 0x2c };
    
    //Iteration Count
    int count = 33;
    
    public static Encrypter getInstance(String password) {
        return new Encrypter(password);
    }
    
    private Encrypter(String password) {
        try {
            //PBE parameter set
            pbeParameterSpec = new PBEParameterSpec(salt, count);

            pbeKeySpec = new PBEKeySpec(password.toCharArray());
            keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            pbeKey = keyFac.generateSecret(pbeKeySpec);
            
            pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            
            pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParameterSpec);
        } catch (Exception e) {
            System.out.println("You bloody broke it...");
        }
    }

    public String encryptClearText(String input) throws IllegalBlockSizeException, BadPaddingException {
        byte[] clearText = input.getBytes();
        byte[] cipherBytes = pbeCipher.doFinal(clearText);
        String cipherText = Base64.encode(cipherBytes);
        
        return cipherText;
    }

    public String decryptEncryption(String input) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        byte[] byteText = Base64.decode(input);
        byte[] clearBytes = pbeCipher.doFinal(byteText);
        String clearText = new String(clearBytes, "UTF-8");

        return clearText;
    }
}
