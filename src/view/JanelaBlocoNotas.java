package view;

import javax.swing.*;
import controller.ControllerClue;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class JanelaBlocoNotas extends JDialog {

    public JanelaBlocoNotas(JFrame pai) {
        super(pai, "Bloco de Notas de Palpites", true);
        setSize(650, 500);
        setLocationRelativeTo(pai);
        setLayout(new GridLayout(1, 3, 10, 10));

        ControllerClue controller = ControllerClue.getInstancia();

        // Pega as cartas da mão do jogador atual como texto
        List<String[]> cartasNaMao = controller.obterDadosCartasDoJogadorAtual();

        List<String> nomesCartasMao = new ArrayList<>();

        if (cartasNaMao != null) {
            for (String[] c : cartasNaMao) {
                nomesCartasMao.add(c[0]);
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

        // NOVO: Referência final do controller e do detetive da vez para gerenciar as marcações individuais
        final ControllerClue controller = ControllerClue.getInstancia();
        final String jogadorAtual = controller.getModel().getJogadorDaVez();

        if (itens != null) {
            for (int i = 0; i < itens.size(); i++) {
                final String item = itens.get(i);
                final JCheckBox box = new JCheckBox(item);

                if (nomesCartasMao.contains(item)) {
                    box.setSelected(true);
                    box.setEnabled(false); // Travado e desabilitado pois o jogador já possui essa carta na mão
                    box.setForeground(Color.BLUE); // Destaque para diferenciar das anotações normais
                } else {
                    // NOVO: Recupera do Model se o detetive atual já havia marcado este item antes
                    if (controller.isNotaMarcada(jogadorAtual, item)) {
                        box.setSelected(true);
                    }

                    // NOVO: Substituídas expressões lambda por ActionListener tradicional em conformidade com as regras de E/S Swing
                    box.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            controller.marcarNota(jogadorAtual, item, box.isSelected());
                        }
                    });
                }

                painel.add(box);
            }
        }
        return painel;
    }
}