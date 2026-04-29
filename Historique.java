import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.util.ArrayList;
 
public class Historique extends JFrame {
    private ArrayList<compte> comptes = new ArrayList<>();
    private final String nomUtilisateur;
    private final String motDePasseMaitre;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSite, txtIdentifiant, txtRecherche;
    private JTextArea txtNote;
    private JPasswordField txtMotDePasse;
    private JLabel lblStatus, lblForce;
    private JSpinner spLength;
    private JCheckBox chkSymbols;
 
    public Historique(String nom, String mdp) {
        this.nomUtilisateur = nom;
        this.motDePasseMaitre = mdp;
        Theme.setupFrame(this, "Gestion des comptes - " + nom, 1250, 760);
        setResizable(true);
        chargerComptes();
        creerInterface();
        setVisible(true);
    }
 
    private void creerInterface() {
        JPanel main = new JPanel(new BorderLayout(18, 18));
        main.setBackground(Theme.BG);
        main.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));
 
        JLabel title = new JLabel("Coffre-fort des comptes - " + nomUtilisateur, SwingConstants.CENTER);
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.PRIMARY_DARK);
        main.add(title, BorderLayout.NORTH);
 
        JPanel left = Theme.card(18);
        left.setLayout(new GridBagLayout());
        left.setPreferredSize(new Dimension(420, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
 
        int y = 0;
        JLabel formTitle = new JLabel("Ajouter un compte");
        formTitle.setFont(Theme.SUBTITLE);
        formTitle.setForeground(Theme.TEXT);
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2;
        left.add(formTitle, gbc);
        gbc.gridwidth = 1;
 
        addLabelField(left, gbc, y++, "Site", txtSite = new JTextField());
        addLabelField(left, gbc, y++, "Identifiant", txtIdentifiant = new JTextField());
        addLabelField(left, gbc, y++, "Mot de passe", txtMotDePasse = new JPasswordField());
 
        gbc.gridx = 0; gbc.gridy = y; left.add(Theme.label("Force"), gbc);
        gbc.gridx = 1; lblForce = new JLabel("-"); lblForce.setFont(Theme.NORMAL); left.add(lblForce, gbc); y++;
 
        JPanel genPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        genPanel.setOpaque(false);
        spLength = new JSpinner(new SpinnerNumberModel(16, 8, 64, 1));
        chkSymbols = new JCheckBox("Symboles", true);
        chkSymbols.setOpaque(false);
        JButton btnGenerer = Theme.secondaryButton("Générer");
        genPanel.add(spLength); genPanel.add(chkSymbols); genPanel.add(btnGenerer);
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2; left.add(genPanel, gbc); gbc.gridwidth = 1;
 
        gbc.gridx = 0; gbc.gridy = y; left.add(Theme.label("Note"), gbc);
        gbc.gridx = 1; txtNote = new JTextArea(3, 18); txtNote.setFont(Theme.NORMAL); txtNote.setLineWrap(true); txtNote.setWrapStyleWord(true);
        left.add(new JScrollPane(txtNote), gbc); y++;
 
        JButton btnAjouter = Theme.primaryButton("Ajouter le compte");
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2; left.add(btnAjouter, gbc); gbc.gridwidth = 1;
 
        JButton btnRetour = Theme.secondaryButton("Retour accueil");
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2; left.add(btnRetour, gbc); gbc.gridwidth = 1;
        main.add(left, BorderLayout.WEST);
 
        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setOpaque(false);
        JPanel searchPanel = Theme.card(12);
        searchPanel.setLayout(new BorderLayout(10, 0));
        JLabel searchLabel = Theme.label("Recherche rapide :");
        txtRecherche = new JTextField(); Theme.styleField(txtRecherche);
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(txtRecherche, BorderLayout.CENTER);
        center.add(searchPanel, BorderLayout.NORTH);
 
        String[] colonnes = {"N°", "Site", "Identifiant", "Mot de passe chiffré", "Note"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(Theme.NORMAL);
        table.setRowHeight(34);
        table.getTableHeader().setFont(Theme.BUTTON);
        table.getTableHeader().setBackground(Theme.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setDefaultRenderer((tbl, val, sel, foc, row, col) -> {
            JLabel lbl = new JLabel(val == null ? "" : val.toString(), SwingConstants.CENTER);
            lbl.setFont(Theme.BUTTON);
            lbl.setBackground(Theme.PRIMARY);
            lbl.setForeground(Color.WHITE);
            lbl.setOpaque(true);
            lbl.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            return lbl;
        });
        center.add(new JScrollPane(table), BorderLayout.CENTER);
 
        JPanel actions = new JPanel(new GridLayout(1, 4, 12, 0));
        actions.setOpaque(false);
        JButton btnCopierId = Theme.secondaryButton("Copier identifiant");
        JButton btnCopierMdp = Theme.primaryButton("Copier mot de passe");
        JButton btnSupprimer = Theme.secondaryButton("Supprimer");
        JButton btnAfficher = Theme.secondaryButton("Afficher détails");
        actions.add(btnCopierId); actions.add(btnCopierMdp); actions.add(btnAfficher); actions.add(btnSupprimer);
        center.add(actions, BorderLayout.SOUTH);
        main.add(center, BorderLayout.CENTER);
 
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setOpaque(true);
        lblStatus.setBackground(Theme.PRIMARY_DARK);
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(Theme.NORMAL);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.add(lblStatus, BorderLayout.SOUTH);
        add(main);
 
        btnGenerer.addActionListener(e -> {
            txtMotDePasse.setText(PasswordUtil.generate((int) spLength.getValue(), chkSymbols.isSelected()));
            updateStrength();
        });
        btnAjouter.addActionListener(e -> ajouterCompte());
        btnRetour.addActionListener(e -> { new Accueil(nomUtilisateur, motDePasseMaitre); dispose(); });
        btnCopierId.addActionListener(e -> copierIdentifiant());
        btnCopierMdp.addActionListener(e -> copierMdp());
        btnSupprimer.addActionListener(e -> supprimerCompte());
        btnAfficher.addActionListener(e -> afficherDetails());
        txtRecherche.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { rafraichirTableau(); }
            public void removeUpdate(DocumentEvent e) { rafraichirTableau(); }
            public void changedUpdate(DocumentEvent e) { rafraichirTableau(); }
        });
        txtMotDePasse.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateStrength(); }
            public void removeUpdate(DocumentEvent e) { updateStrength(); }
            public void changedUpdate(DocumentEvent e) { updateStrength(); }
        });
        rafraichirTableau();
        updateStrength();
    }
 
    private void addLabelField(JPanel p, GridBagConstraints gbc, int y, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1; p.add(Theme.label(label), gbc);
        gbc.gridx = 1; Theme.styleField(field); p.add(field, gbc);
    }
 
    private void updateStrength() {
        String p = new String(txtMotDePasse.getPassword());
        String txt = PasswordUtil.strengthText(p);
        lblForce.setText(txt);
        lblForce.setForeground(txt.equals("Fort") ? Theme.SUCCESS : txt.equals("Moyen") ? Theme.WARNING : Theme.DANGER);
    }
 
    private void ajouterCompte() {
        String site = txtSite.getText().trim();
        String identifiant = txtIdentifiant.getText().trim();
        String mdp = new String(txtMotDePasse.getPassword());
        String note = txtNote.getText().trim();
        if (site.isEmpty() || identifiant.isEmpty() || mdp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs principaux sont obligatoires."); return;
        }
        String chiffre = CryptoService.chiffrer(mdp, motDePasseMaitre);
        if (chiffre == null) { JOptionPane.showMessageDialog(this, "Erreur de chiffrement."); return; }
        comptes.add(new compte(site, identifiant, chiffre, note));
        sauvegarderComptes();
        txtSite.setText(""); txtIdentifiant.setText(""); txtMotDePasse.setText(""); txtNote.setText("");
        rafraichirTableau();
        lblStatus.setText("Compte ajouté avec succès : " + site);
    }
 
    private compte selectedCompte() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une ligne."); return null; }
        String site = tableModel.getValueAt(row, 1).toString();
        String id = tableModel.getValueAt(row, 2).toString();
        for (compte c : comptes) if (c.getSite().equals(site) && c.getIdentifiant().equals(id)) return c;
        return null;
    }
 
    private void copierIdentifiant() {
        compte c = selectedCompte(); if (c == null) return;
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(c.getIdentifiant()), null);
        lblStatus.setText("Identifiant copié dans le presse-papiers.");
    }
 
    private void copierMdp() {
        compte c = selectedCompte(); if (c == null) return;
        String mdp = CryptoService.dechiffrer(c.getMotDePasseChiffre(), motDePasseMaitre);
        if (mdp == null) { JOptionPane.showMessageDialog(this, "Déchiffrement impossible."); return; }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(mdp), null);
        lblStatus.setText("Mot de passe déchiffré puis copié dans le presse-papiers.");
    }
 
    private void afficherDetails() {
        compte c = selectedCompte(); if (c == null) return;
        String mdp = CryptoService.dechiffrer(c.getMotDePasseChiffre(), motDePasseMaitre);
        JOptionPane.showMessageDialog(this,
                "Site : " + c.getSite() + "\nIdentifiant : " + c.getIdentifiant() + "\nMot de passe : " + (mdp == null ? "Erreur" : mdp) + "\nNote : " + c.getNote(),
                "Détails", JOptionPane.INFORMATION_MESSAGE);
    }
 
    private void supprimerCompte() {
        compte c = selectedCompte(); if (c == null) return;
        int ok = JOptionPane.showConfirmDialog(this, "Supprimer le compte " + c.getSite() + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            comptes.remove(c); sauvegarderComptes(); rafraichirTableau(); lblStatus.setText("Compte supprimé.");
        }
    }
 
    private void rafraichirTableau() {
        if (tableModel == null) return;
        String filtre = txtRecherche == null ? "" : txtRecherche.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        int n = 1;
        for (compte c : comptes) {
            if (!filtre.isEmpty() && !c.getSite().toLowerCase().contains(filtre) && !c.getIdentifiant().toLowerCase().contains(filtre)) continue;
            String masked = c.getMotDePasseChiffre() == null ? "" : c.getMotDePasseChiffre().substring(0, Math.min(28, c.getMotDePasseChiffre().length())) + "...";
            tableModel.addRow(new Object[]{n++, c.getSite(), c.getIdentifiant(), masked, c.getNote()});
        }
        if (lblStatus != null) lblStatus.setText(comptes.size() + " compte(s) enregistrés | données chiffrées localement");
    }
 
    @SuppressWarnings("unchecked")
    private void chargerComptes() {
        File f = new File("comptes_" + nomUtilisateur + ".dat");
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            comptes = (ArrayList<compte>) ois.readObject();
        } catch (Exception e) { comptes = new ArrayList<>(); }
    }
 
    private void sauvegarderComptes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("comptes_" + nomUtilisateur + ".dat"))) {
            oos.writeObject(comptes);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur de sauvegarde."); }
    }
}
