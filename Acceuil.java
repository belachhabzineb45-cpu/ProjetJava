import javax.swing.*;
import java.awt.*;

public class Accueil extends JFrame {
    private final String nomUtilisateur;
    private final String motDePasseMaitre;

    public Accueil(String nom, String mdp) {
        this.nomUtilisateur = nom;
        this.motDePasseMaitre = mdp;
        Theme.setupFrame(this, "Accueil - Gestionnaire de mots de passe", 1050, 650);
        setResizable(true);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(24, 24));
        main.setBackground(Theme.BG);
        main.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));

        JLabel title = new JLabel("Bonjour, " + nomUtilisateur , SwingConstants.CENTER);
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.PRIMARY_DARK);
        main.add(title, BorderLayout.NORTH);

        // Grille 1x3 : seulement 3 cartes (Générateur sécurisé supprimé)
        JPanel grid = new JPanel(new GridLayout(1, 3, 24, 24));
        grid.setOpaque(false);
        grid.add(cardButton(" Ajouter un compte", "Enregistrer, rechercher, supprimer et copier vos mots de passe.", () -> openHistorique()));
        grid.add(cardButton(" Déchiffrer un mdp", "Retrouver rapidement un compte par nom de site.", () -> openDechiffrement()));
        grid.add(cardButton(" Bonnes pratiques", "Utilisez un mot de passe maître fort et ne le partagez jamais.", () -> showTips()));
        main.add(grid, BorderLayout.CENTER);

        JLabel footer = new JLabel("Gestionnaire de mots de passe", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        footer.setForeground(Theme.MUTED);
        main.add(footer, BorderLayout.SOUTH);
        add(main);
    }

    private JPanel cardButton(String title, String desc, Runnable action) {
        JPanel card = Theme.card(26);
        card.setLayout(new BorderLayout(10, 12));
        JLabel t = new JLabel(title);
        t.setFont(Theme.SUBTITLE);
        t.setForeground(Theme.TEXT);
        JTextArea d = new JTextArea(desc);
        d.setFont(Theme.NORMAL);
        d.setForeground(Theme.MUTED);
        d.setOpaque(false);
        d.setEditable(false);
        d.setLineWrap(true);
        d.setWrapStyleWord(true);
        JButton b = Theme.primaryButton("Ouvrir");
        b.addActionListener(e -> action.run());
        card.add(t, BorderLayout.NORTH);
        card.add(d, BorderLayout.CENTER);
        card.add(b, BorderLayout.SOUTH);
        return card;
    }

    private void openHistorique() {
        new Historique(nomUtilisateur, motDePasseMaitre);
        dispose();
    }

    private void openDechiffrement() {
        new Dechiffrement(nomUtilisateur, motDePasseMaitre);
        dispose();
    }

    private void showTips() {
        JOptionPane.showMessageDialog(this,
                "Conseils :\n- Utilisez un mot de passe maître long.\n- Ne réutilisez pas le même mot de passe.\n- Activez HTTPS sur vos comptes.\n- Sauvegardez vos fichiers .dat dans un endroit sûr.",
                "Bonnes pratiques", JOptionPane.INFORMATION_MESSAGE);
    }
}
