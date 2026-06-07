package view;

import javax.swing.*;
import controller.Controller;
import model.JogoClueInicio;
import model.ObservadoIF;
import model.ObservadorIF;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class JanelaPrincipal extends JFrame implements ObservadorIF {

    private PainelTabuleiro painelTabuleiro;
    private JComboBox<Integer> boxDado1;
    private JComboBox<Integer> boxDado2;
    private JLabel labelImagemDado1;
    private JLabel labelImagemDado2;
    private JLabel labelJogador;
    private JPanel painelDadosImagens;
    private JButton botaoPassagem; // NOVO: Botão guardado globalmente para controle de estado

    public JanelaPrincipal() {
        setTitle("Clue - Detetive | Terceira Iteração");
        setSize(1350, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Controller.getInstance().registrarObservador(this);

        painelTabuleiro = new PainelTabuleiro();
        add(new JScrollPane(painelTabuleiro), BorderLayout.CENTER);

        JPanel painelLateral = new JPanel();
        painelLateral.setLayout(new BoxLayout(painelLateral, BoxLayout.Y_AXIS));
        painelLateral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelLateral.setPreferredSize(new Dimension(250, 900));

        labelJogador = new JLabel();
        labelJogador.setFont(new Font("Arial", Font.BOLD, 14));
        labelJogador.setForeground(Color.RED);
        painelLateral.add(labelJogador);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // NOVO: Botão de Passagem Secreta (inicialmente desligado)
        botaoPassagem = new JButton("Usar Passagem Secreta");
        botaoPassagem.setEnabled(false);
        botaoPassagem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controller.getInstance().usarPassagemSecreta();
            }
        });
        painelLateral.add(botaoPassagem);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        // Novas Janelas Requisitadas na Iteração 3
        JButton botaoCartas = new JButton("Mostrar Cartas");
        botaoCartas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new JanelaCartas(JanelaPrincipal.this);
            }
        });
        painelLateral.add(botaoCartas);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton botaoNotas = new JButton("Bloco de Notas");
        botaoNotas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new JanelaBlocoNotas(JanelaPrincipal.this);
            }
        });
        painelLateral.add(botaoNotas);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        painelLateral.add(new JLabel("Dado 1:"));
        Integer[] faces = {1, 2, 3, 4, 5, 6};
        boxDado1 = new JComboBox<>(faces);
        painelLateral.add(boxDado1);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        painelLateral.add(new JLabel("Dado 2:"));
        boxDado2 = new JComboBox<>(faces);
        painelLateral.add(boxDado2);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        labelImagemDado1 = new JLabel();
        labelImagemDado2 = new JLabel();
        painelDadosImagens = new JPanel(new FlowLayout());
        painelDadosImagens.setOpaque(true);
        painelDadosImagens.add(labelImagemDado1);
        painelDadosImagens.add(labelImagemDado2);
        painelLateral.add(painelDadosImagens);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton botaoDados = new JButton("Lançar Dados");
        botaoDados.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int d1 = (Integer) boxDado1.getSelectedItem();
                int d2 = (Integer) boxDado2.getSelectedItem();
                Controller.getInstance().processarRolagemDados(true, d1, d2);
            }
        });
        painelLateral.add(botaoDados);
        add(painelLateral, BorderLayout.EAST);

        JogoClueInicio partida = Controller.getInstance().getPartida();
        atualizarTurnoVisual(partida.getJogadorDaVez());
        atualizarImagensDosDados(partida.getValorDado1(), partida.getValorDado2());
    }

    @Override
    public void notify(ObservadoIF o) {
        if (o instanceof JogoClueInicio) {
            JogoClueInicio jogo = (JogoClueInicio) o;
            atualizarTurnoVisual(jogo.getJogadorDaVez());
            atualizarImagensDosDados(jogo.getValorDado1(), jogo.getValorDado2());

            // NOVO: A cada notificação do Observer, checa se o jogador da vez tem passagem
            boolean temPassagem = Controller.getInstance().verificarPassagemSecreta();
            botaoPassagem.setEnabled(temPassagem);
        }
    }

    public void atualizarTurnoVisual(String jogador) {
        labelJogador.setText("Vez de: " + jogador);
        painelDadosImagens.setBackground(obterCorDoJogador(jogador));
        painelDadosImagens.repaint();
    }

    private Color obterCorDoJogador(String nomeJogador) {
        switch (nomeJogador) {
            case "Srta. Rose": return Color.RED;
            case "Coronel Mostarda": return Color.YELLOW;
            case "Professor Plum": return new Color(128, 0, 128);
            case "Sr. Marinho": return Color.GREEN;
            case "Dona Violeta": return Color.BLUE;
            case "Dona Branca": return Color.WHITE;
            default: return Color.LIGHT_GRAY;
        }
    }

    private void atualizarImagensDosDados(int valorD1, int valorD2) {
        try {
            Image img1 = ImageIO.read(new File("resources/Tabuleiros/dado" + valorD1 + ".jpg"));
            Image img2 = ImageIO.read(new File("resources/Tabuleiros/dado" + valorD2 + ".jpg"));
            labelImagemDado1.setIcon(new ImageIcon(img1.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
            labelImagemDado2.setIcon(new ImageIcon(img2.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            System.out.println("Erro ao carregar imagens dos dados: verifiqueresources/Tabuleiros/");
        }
    }
}