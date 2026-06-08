package view;

import javax.swing.*;
import controller.ControllerClue; // Importando nosso Controller Singleton
import java.awt.*;

public class JanelaBlocoNotas extends JDialog {

    public JanelaBlocoNotas(JFrame pai) {
        super(pai, "Bloco de Notas de Palpites", true);
        setSize(600, 450);
        setLocationRelativeTo(pai);
        setLayout(new GridLayout(1, 3, 10, 10));

        // Puxamos os nomes dinamicamente através do Controller (Fachada)
        // Isso garante que, se mudarmos o Model, a View reflete automaticamente
        ControllerClue controller = ControllerClue.getInstancia();

        // Supondo que você crie métodos no seu JogoClueInicio para retornar essas listas:
        JPanel colSuspeitos = criarColunaDeNotas("Suspeitos", controller.getModel().getNomesSuspeitos());
        // JPanel colArmas = criarColunaDeNotas("Armas", controller.getModel().getNomesArmas());
        // JPanel colComodos = criarColunaDeNotas("Cômodos", controller.getModel().getNomesComodos());

        add(colSuspeitos);
        // add(colArmas);
        // add(colComodos);

        setVisible(true);
    }

    private JPanel criarColunaDeNotas(String titulo, java.util.List<String> itens) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createTitledBorder(titulo));

        for (String item : itens) {
            JCheckBox box = new JCheckBox(item);
            // Dica: Adicione um ItemListener aqui se quiser que o Model salve essa marcação
            painel.add(box);
        }
        return painel;
    }
}