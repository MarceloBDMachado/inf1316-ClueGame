package view;

import javax.swing.*;
import controller.Controller;
import model.Carta;
import model.JogoClueInicio;
import java.awt.*;
import java.util.List;

public class JanelaCartas extends JDialog {

    public JanelaCartas(JFrame pai) {
        super(pai, "Suas Cartas", true);
        setSize(500, 300);
        setLocationRelativeTo(pai);
        setLayout(new BorderLayout());

        JogoClueInicio jogo = Controller.getInstance().getPartida();
        int idJogador = jogo.getIndiceJogadorDaVez();
        List<Carta> cartas = jogo.getCartasDoJogador(idJogador);

        JPanel painelCentral = new JPanel(new FlowLayout());
        painelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (cartas.isEmpty()) {
            painelCentral.add(new JLabel("Nenhuma carta na mão deste jogador."));
        } else {
            for (Carta c : cartas) {
                JPanel painelCartaVisual = new JPanel();
                painelCartaVisual.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                painelCartaVisual.setPreferredSize(new Dimension(100, 150));
                painelCartaVisual.setLayout(new BorderLayout());

                JLabel lblTexto = new JLabel("<html><center>" + c.getNome() + "<br><b>" + c.getTipo() + "</b></center></html>", SwingConstants.CENTER);
                painelCartaVisual.add(lblTexto, BorderLayout.CENTER);
                painelCentral.add(painelCartaVisual);
            }
        }

        add(new JLabel("Cartas de: " + jogo.getJogadorDaVez(), SwingConstants.CENTER), BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
        setVisible(true);
    }
}