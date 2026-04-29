import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("OptionPane.messageFont", Theme.NORMAL);
        UIManager.put("OptionPane.buttonFont", Theme.BUTTON);
        UIManager.put("Button.font", Theme.BUTTON);
        UIManager.put("Label.font", Theme.NORMAL);
        SwingUtilities.invokeLater(Authentification::new);
    }
}
