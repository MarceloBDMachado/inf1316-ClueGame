package view;

import javax.swing.*;
import controller.ControllerClue;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class JanelaBlocoNotas extends JDialog {

    public JanelaBlocoNotas(JFrame pai) {
        super(pai, "Bloco de Notas de Palpites", true);
        setSize(650, 500);
        setLocationRelativeTo(pai);
        setLayout(new GridLayout(1, 3, 10, 10));

        // Acessa o Controller Singleton
        ControllerClue controller = ControllerClue.getInstancia();

        // Pega as cartas da mão do jogador atual como texto (Array de Strings)
        // Substitua pelo nome exato do método que está no seu Controller
        List<String[]> cartasNaMao = controller.obterDadosCartasDoJogadorAtual();

        List<String> nomesCartasMao = new ArrayList<>();

        // Verificação de segurança (evita NullPointerException se a mão estiver vazia)
        if (cartasNaMao != null) {
            for (String[] c : cartasNaMao) {
                nomesCartasMao.add(c[0]); // Pega apenas o nome textual da carta
            }
        }

        // Gera as colunas puxando os nomes direto da Fachada no Model
        JPanel colSuspeitos = criarColunaDeNotas("Suspeitos", controller.getModel().getNomesSuspeitos(), nomesCartasMao);
        JPanel colArmas = criarColunaDeNotas("Armas", controller.getModel().getNomesArmas(), nomesCartasMao);
        JPanel colComodos = criarColunaDeNotas("Cômodos", controller.getModel().getNomesComodos(), nomesCartasMao);

        add(colSuspeitos);
        add(colArmas);
        add(colComodos);

        // Adiciona um letreiro indicando o dono deste bloco
        setTitle("Bloco de Notas — Detetive: " + controller.getModel().getJogadorDaVez());

        setVisible(true);
    }

    private JPanel criarColunaDeNotas(String titulo, List<String> itens, List<String> nomesCartasMao) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createTitledBorder(titulo));

        // Segurança adicional caso os itens venham nulos do Model
        if (itens != null) {
            for (String item : itens) {
                JCheckBox box = new JCheckBox(item);

                // MECÂNICA DE AUTO-CHECK: Se a carta está na mão do jogador, já nasce marcada em azul!
                if (nomesCartasMao.contains(item)) {
                    box.setSelected(true);
                    box.setForeground(Color.BLUE); // Destaque para diferenciar das anotações normais
                }

                painel.add(box);
            }
        }
        return painel;
    }
}