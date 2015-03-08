package common.lib.misc;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionManager {
	
	public static byte[] encrypt(byte[] keyStart, byte[] clearText) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(keyStart, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clearText);
        return encrypted;
    }

    public static byte[] decrypt(byte[] keyStart, byte[] cipherText) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(keyStart, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(cipherText);
        return decrypted;
    }

}
