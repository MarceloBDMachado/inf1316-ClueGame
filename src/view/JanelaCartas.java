package view;

import javax.swing.*;
import controller.ControllerClue;
import java.awt.*;
import java.util.List;

public class JanelaCartas extends JDialog {

    public JanelaCartas(JFrame pai) {
        // Define a janela como modal (trava a tela de trás até ser fechada)
        super(pai, "Suas Cartas", true);
        setSize(500, 350);
        setLocationRelativeTo(pai);
        setLayout(new BorderLayout());

        // Acessa os dados exclusivamente através do nosso ControllerClue Singleton
        ControllerClue controller = ControllerClue.getInstancia();
        List<String[]> cartasData = controller.getCartasJogadorDaVez();
        String nomeJogador = controller.getModel().getJogadorDaVez();

        JPanel painelCentral = new JPanel(new FlowLayout());
        painelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (cartasData.isEmpty()) {
            painelCentral.add(new JLabel("Nenhuma carta distribuída para este jogador."));
        } else {
            // Varre a lista de strings enviada de forma segura pelo Controller
            for (String[] dadosCarta : cartasData) {
                String nomeCarta = dadosCarta[0];
                String tipoCarta = dadosCarta[1];

                JPanel painelCartaVisual = new JPanel();
                painelCartaVisual.setBackground(Color.WHITE);
                painelCartaVisual.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                painelCartaVisual.setPreferredSize(new Dimension(120, 180));
                painelCartaVisual.setLayout(new BorderLayout());

                // Monta o texto centralizado da carta estilizado em HTML básico
                JLabel lblTexto = new JLabel("<html><center><br><font size='4'><b>" + nomeCarta + "</b></font><br><br><font color='gray'>" + tipoCarta + "</font></center></html>", SwingConstants.CENTER);
                painelCartaVisual.add(lblTexto, BorderLayout.CENTER);

                painelCentral.add(painelCartaVisual);
            }
        }

        // Cabeçalho indicando o dono da mão
        JLabel lblTitulo = new JLabel("Cartas na mão de: " + nomeJogador, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        add(lblTitulo, BorderLayout.NORTH);
        add(new JScrollPane(painelCentral), BorderLayout.CENTER);

        // Exibe a janela modal na tela
        setVisible(true);
    }
}