import java.io.Serializable;
import java.time.LocalDateTime;

public class compte implements Serializable {
    private static final long serialVersionUID = 2L;
    private String site;
    private String identifiant;
    private String motDePasseChiffre;
    private String note;
    private LocalDateTime dateCreation;

    public compte(String site, String identifiant, String motDePasseChiffre) {
        this(site, identifiant, motDePasseChiffre, "");
    }

    public compte(String site, String identifiant, String motDePasseChiffre, String note) {
        this.site = site;
        this.identifiant = identifiant;
        this.motDePasseChiffre = motDePasseChiffre;
        this.note = note;
        this.dateCreation = LocalDateTime.now();
    }

    public String getSite() { return site; }
    public String getIdentifiant() { return identifiant; }
    public String getMotDePasseChiffre() { return motDePasseChiffre; }
    public String getNote() { return note == null ? "" : note; }
    public LocalDateTime getDateCreation() { return dateCreation; }

    public void setSite(String site) { this.site = site; }
    public void setIdentifiant(String identifiant) { this.identifiant = identifiant; }
    public void setMotDePasseChiffre(String motDePasseChiffre) { this.motDePasseChiffre = motDePasseChiffre; }
    public void setNote(String note) { this.note = note; }
}
