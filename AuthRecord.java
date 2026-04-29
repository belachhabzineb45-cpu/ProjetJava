import java.io.Serializable;

public class AuthRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    public String username;
    public String saltB64;
    public String hashB64;

    public AuthRecord(String username, String saltB64, String hashB64) {
        this.username = username;
        this.saltB64 = saltB64;
        this.hashB64 = hashB64;
    }
}
