import javax.swing.SwingUtilities;
import view.JanelaInicio;

public class Main {
    public static void main(String[] args) {
        // Inicialização em conformidade com as boas práticas de threads do Swing (EDT
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JanelaInicio().setVisible(true);
            }
        });
    }
}