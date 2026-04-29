import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.util.ArrayList;

public class Dechiffrement extends JFrame {
    private final String nomUtilisateur;
    private final String motDePasseMaitre;

    private JTextField txtSite;
    private JTextArea txtResultat;

    private compte dernierCompte;
    private String dernierMdp;

    public Dechiffrement(String nom, String mdp) {
        this.nomUtilisateur = nom;
        this.motDePasseMaitre = mdp;

        Theme.setupFrame(this, "Déchiffrement - " + nom, 980, 620);
        setResizable(true);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(18, 18));
        main.setBackground(Theme.BG);
        main.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));

        JLabel title = new JLabel("Rechercher et déchiffrer un mot de passe", SwingConstants.CENTER);
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.PRIMARY_DARK);
        main.add(title, BorderLayout.NORTH);

        JPanel card = Theme.card(28);
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        card.add(Theme.label("Nom du site"), gbc);

        gbc.gridx = 1;
        txtSite = new JTextField(28);
        Theme.styleField(txtSite);
        card.add(txtSite, gbc);

        JButton btnChercher = Theme.primaryButton("Déchiffrer");
        JButton btnCopier = Theme.secondaryButton("Copier le mot de passe");
        JButton btnRetour = Theme.secondaryButton("Retour accueil");

        btnChercher.setForeground(Color.BLACK);
        btnCopier.setForeground(Color.BLACK);
        btnRetour.setForeground(Color.BLACK);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 12, 0));
        buttons.setOpaque(false);
        buttons.add(btnChercher);
        buttons.add(btnCopier);
        buttons.add(btnRetour);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        card.add(buttons, gbc);

        txtResultat = new JTextArea(10, 60);
        txtResultat.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        txtResultat.setForeground(Theme.TEXT);
        txtResultat.setEditable(false);
        txtResultat.setLineWrap(true);
        txtResultat.setWrapStyleWord(true);
        txtResultat.setText("Entrez le nom du site puis cliquez sur Déchiffrer.");

        gbc.gridy = 2;
        card.add(new JScrollPane(txtResultat), gbc);

        main.add(card, BorderLayout.CENTER);
        add(main);

        btnChercher.addActionListener(e -> chercher());
        btnCopier.addActionListener(e -> copier());
        btnRetour.addActionListener(e -> {
            new Accueil(nomUtilisateur, motDePasseMaitre);
            dispose();
        });

        txtSite.addActionListener(e -> chercher());
    }

    private void chercher() {
        String site = txtSite.getText().trim();

        if (site.isEmpty()) {
            txtResultat.setText("Veuillez saisir le nom du site.");
            return;
        }

        JPasswordField champCode = new JPasswordField();

        int choix = JOptionPane.showConfirmDialog(
                this,
                champCode,
                "Entrez le code de sécurité",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choix != JOptionPane.OK_OPTION) {
            return;
        }

        String codeSaisi = new String(champCode.getPassword()).trim();

        if (codeSaisi.isEmpty()) {
            txtResultat.setText("Veuillez entrer le code de sécurité.");
            return;
        }

        if (!codeSaisi.equals(motDePasseMaitre)) {
            txtResultat.setText("Erreur : code de sécurité incorrect.");
            return;
        }

        ArrayList<compte> comptes = chargerComptes();

        dernierCompte = null;
        dernierMdp = null;

        for (compte c : comptes) {
            if (c.getSite().equalsIgnoreCase(site)
                    || c.getSite().toLowerCase().contains(site.toLowerCase())) {
                dernierCompte = c;
                break;
            }
        }

        if (dernierCompte == null) {
            txtResultat.setText("Aucun compte trouvé pour : " + site);
            return;
        }

        dernierMdp = CryptoService.dechiffrer(
                dernierCompte.getMotDePasseChiffre(),
                motDePasseMaitre
        );

        if (dernierMdp == null) {
            txtResultat.setText("Déchiffrement impossible. Les données sont peut-être corrompues.");
            return;
        }

        txtResultat.setText("Compte trouvé\n\n"
                + "Site : " + dernierCompte.getSite()
                + "\nIdentifiant : " + dernierCompte.getIdentifiant()
                + "\nMot de passe : " + dernierMdp
                + "\nNote : " + dernierCompte.getNote());
    }

    private void copier() {
        if (dernierMdp == null) {
            JOptionPane.showMessageDialog(this, "Déchiffrez d'abord un compte.");
            return;
        }

        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(dernierMdp), null);

        JOptionPane.showMessageDialog(this, "Mot de passe copié.");
    }

    @SuppressWarnings("unchecked")
    private ArrayList<compte> chargerComptes() {
        File fichier = new File("comptes_" + nomUtilisateur + ".dat");

        if (fichier.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
                return (ArrayList<compte>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new ArrayList<>();
    }
}
