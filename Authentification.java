import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Authentification extends JFrame {
    private JTextField txtNom;
    private JPasswordField txtMotDePasse;
    private JLabel lblStatus;
    private static final String FICHIER_AUTH = "auth.dat";

    public Authentification() {
        Theme.setupFrame(this, "Gestionnaire de mots de passe - Connexion", 980, 650);
        setResizable(true);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(Theme.BG);
        main.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));

        JPanel card = Theme.card(38);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(600, 460));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel title = new JLabel(" Gestionnaire de mots de passe", SwingConstants.CENTER);
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.PRIMARY_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        JLabel subtitle = new JLabel("Connexion sécurisée avec mot de passe maître", SwingConstants.CENTER);
        subtitle.setFont(Theme.NORMAL);
        subtitle.setForeground(Theme.MUTED);
        gbc.gridy = 1;
        card.add(subtitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2; gbc.gridx = 0;
        card.add(Theme.label("Nom d'utilisateur"), gbc);
        gbc.gridx = 1;
        txtNom = new JTextField(22); Theme.styleField(txtNom);
        card.add(txtNom, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        card.add(Theme.label("Mot de passe maître"), gbc);
        gbc.gridx = 1;
        txtMotDePasse = new JPasswordField(22); Theme.styleField(txtMotDePasse);
        card.add(txtMotDePasse, gbc);

        JButton btnConnexion = Theme.primaryButton("Se connecter");
        JButton btnCreer = Theme.secondaryButton("Créer un compte");
        JPanel buttons = new JPanel(new GridLayout(1, 2, 14, 0));
        buttons.setOpaque(false);
        buttons.add(btnConnexion);
        buttons.add(btnCreer);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        card.add(buttons, gbc);

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(Theme.NORMAL);
        gbc.gridy = 5;
        card.add(lblStatus, gbc);

        JLabel note = new JLabel("Astuce : utilisez un mot de passe maître long et unique.", SwingConstants.CENTER);
        note.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        note.setForeground(Theme.MUTED);
        gbc.gridy = 6;
        card.add(note, gbc);

        main.add(card);
        add(main);

        btnConnexion.addActionListener(e -> connecter());
        btnCreer.addActionListener(e -> inscrire());
        txtMotDePasse.addActionListener(e -> connecter());
    }

    private void inscrire() {
        String nom = txtNom.getText().trim();
        String mdp = new String(txtMotDePasse.getPassword());
        if (!validateInputs(nom, mdp)) return;
        File f = new File(FICHIER_AUTH);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHIER_AUTH))) {
            byte[] salt = CryptoService.randomBytes(16);
            String hash = CryptoService.hashMasterPassword(mdp, salt);
            oos.writeObject(new AuthRecord(nom, CryptoService.b64(salt), hash));
            showStatus("Compte créé avec succès !", Theme.SUCCESS);
            JOptionPane.showMessageDialog(this, "Bienvenue " + nom + " !");
            new Accueil(nom, mdp);
            dispose();
        } catch (Exception ex) {
            showStatus("Erreur lors de la création du compte.", Theme.DANGER);
        }
    }

    private void connecter() {
        String nom = txtNom.getText().trim();
        String mdp = new String(txtMotDePasse.getPassword());
        if (!validateInputs(nom, mdp)) return;
        File f = new File(FICHIER_AUTH);
        if (!f.exists()) {
            showStatus("Aucun compte trouvé. Cliquez sur Créer un compte.", Theme.WARNING);
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            AuthRecord record;
            if (obj instanceof AuthRecord) {
                record = (AuthRecord) obj;
            } else {
                showStatus("Ancien fichier auth.dat détecté. Créez un nouveau compte sécurisé.", Theme.WARNING);
                return;
            }
            String hash = CryptoService.hashMasterPassword(mdp, CryptoService.fromB64(record.saltB64));
            if (record.username.equals(nom) && CryptoService.constantTimeEquals(hash, record.hashB64)) {
                new Accueil(nom, mdp);
                dispose();
            } else {
                showStatus("Nom ou mot de passe incorrect.", Theme.DANGER);
            }
        } catch (Exception ex) {
            showStatus("Impossible de lire le fichier d'authentification.", Theme.DANGER);
        }
    }

    private boolean validateInputs(String nom, String mdp) {
        if (nom.isEmpty() || mdp.isEmpty()) {
            showStatus("Veuillez remplir tous les champs.", Theme.DANGER);
            return false;
        }
        if (mdp.length() < 8) {
            showStatus("Le mot de passe maître doit contenir au moins 8 caractères.", Theme.DANGER);
            return false;
        }
        return true;
    }

    private void showStatus(String msg, Color c) {
        lblStatus.setText(msg);
        lblStatus.setForeground(c);
    }
}
