package ir.derasat.mydiary;

import android.util.Base64;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static SecretKeySpec generateKey(String password) throws Exception {
        byte[] key = password.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        return new SecretKeySpec(key, "AES");
    }

    public static String encrypt(String data, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }

    public static String decrypt(String encryptedData, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT));
        return new String(decryptedData, "UTF-8");
    }
}