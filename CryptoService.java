import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoService {
    private static final String AES_ALGO = "AES/GCM/NoPadding";
    private static final String KDF = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120000;
    private static final int KEY_BITS = 256;
    private static final int SALT_BYTES = 16;
    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;
    private static final SecureRandom random = new SecureRandom();

    public static String chiffrer(String texte, String motDePasseMaitre) {
        try {
            byte[] salt = randomBytes(SALT_BYTES);
            byte[] iv = randomBytes(IV_BYTES);
            SecretKeySpec key = deriveKey(motDePasseMaitre, salt);
            Cipher cipher = Cipher.getInstance(AES_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] encrypted = cipher.doFinal(texte.getBytes(StandardCharsets.UTF_8));
            return "v2:" + b64(salt) + ":" + b64(iv) + ":" + b64(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String dechiffrer(String texteChiffre, String motDePasseMaitre) {
        try {
            if (texteChiffre == null || texteChiffre.trim().isEmpty()) return null;
            if (!texteChiffre.startsWith("v2:")) return dechiffrerAncienECB(texteChiffre, motDePasseMaitre);
            String[] parts = texteChiffre.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] iv = Base64.getDecoder().decode(parts[2]);
            byte[] encrypted = Base64.getDecoder().decode(parts[3]);
            SecretKeySpec key = deriveKey(motDePasseMaitre, salt);
            Cipher cipher = Cipher.getInstance(AES_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    public static String hashMasterPassword(String password, byte[] salt) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(KDF);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, 256);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return b64(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] randomBytes(int len) {
        byte[] out = new byte[len];
        random.nextBytes(out);
        return out;
    }

    public static String b64(byte[] data) { return Base64.getEncoder().encodeToString(data); }
    public static byte[] fromB64(String data) { return Base64.getDecoder().decode(data); }

    private static SecretKeySpec deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory skf = SecretKeyFactory.getInstance(KDF);
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_BITS);
        return new SecretKeySpec(skf.generateSecret(spec).getEncoded(), "AES");
    }

    private static String dechiffrerAncienECB(String texteChiffre, String cle) {
        try {
            String cleComplete = completerCleAncienne(cle);
            SecretKeySpec keySpec = new SecretKeySpec(cleComplete.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(texteChiffre);
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    private static String completerCleAncienne(String cle) {
        if (cle == null) cle = "";
        StringBuilder sb = new StringBuilder(cle.trim());
        while (sb.length() < 16) sb.append("0");
        return sb.substring(0, 16);
    }
}
