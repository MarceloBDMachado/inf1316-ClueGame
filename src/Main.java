import javax.swing.SwingUtilities;
import view.JanelaPrincipal;

public class Main {
    public static void main(String[] args) {
        // Inicialização em conformidade com as boas práticas de threads do Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            new JanelaPrincipal().setVisible(true);
        });
    }
}