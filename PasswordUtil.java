import java.security.SecureRandom;

public class PasswordUtil {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{};:,.?";
    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length, boolean symbols) {
        if (length < 8) length = 8;
        String chars = UPPER + LOWER + DIGITS + (symbols ? SYMBOLS : "");
        StringBuilder sb = new StringBuilder();
        sb.append(randomChar(UPPER));
        sb.append(randomChar(LOWER));
        sb.append(randomChar(DIGITS));
        if (symbols) sb.append(randomChar(SYMBOLS));
        while (sb.length() < length) sb.append(randomChar(chars));
        return shuffle(sb.toString());
    }

    private static char randomChar(String s) {
        return s.charAt(random.nextInt(s.length()));
    }

    private static String shuffle(String input) {
        char[] a = input.toCharArray();
        for (int i = a.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
        return new String(a);
    }

    public static int strength(String password) {
        if (password == null) return 0;
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[^A-Za-z0-9].*")) score++;
        return Math.min(score, 6);
    }

    public static String strengthText(String password) {
        int s = strength(password);
        if (s <= 2) return "Faible";
        if (s <= 4) return "Moyen";
        return "Fort";
    }
}
