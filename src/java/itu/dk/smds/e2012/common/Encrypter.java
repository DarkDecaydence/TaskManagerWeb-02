package itu.dk.smds.e2012.common;

import java.io.*;
import java.security.*;
import javax.crypto.*;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 *
 * @author Alexander
 */
public class Encrypter {

    private static KeyGenerator keyGen;
    private SecretKey desKey;
    private Cipher desCipher;
    
    public static Encrypter getInstance() {
        return new Encrypter();
    }
    
    private Encrypter() {
        try {
            keyGen = KeyGenerator.getInstance("DES");
            desKey = keyGen.generateKey();
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            desCipher.init(Cipher.ENCRYPT_MODE, desKey);
        } catch (NoSuchAlgorithmException nsae) {
            System.out.println("No such algorithm: " + nsae.getMessage());
        } catch (NoSuchPaddingException nspe) {
            System.out.println("No such padding: " + nspe.getMessage());
        } catch (InvalidKeyException ike) {
            System.out.println("Invalid key: " + ike.getMessage());
        }
        
    }

    public String encryptClearText(String input) throws IllegalBlockSizeException, BadPaddingException {
        byte[] clearText = input.getBytes();
        byte[] cipherBytes = desCipher.doFinal(clearText);
        String cipherText = Base64.encode(cipherBytes);

        return cipherText;
    }

    public String decryptEncryption(String input) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        byte[] byteText = Base64.decode(input);
        byte[] clearBytes = desCipher.doFinal(byteText);
        String clearText = new String(clearBytes, "UTF-8");

        return clearText;
    }
}
