package view;

import javax.swing.*;

import model.JogoClueInicio;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class JanelaPrincipal extends JFrame {

    private PainelTabuleiro painelTabuleiro;
    private JComboBox<Integer> boxDado1;
    private JComboBox<Integer> boxDado2;
    private JLabel labelImagemDado1;
    private JLabel labelImagemDado2;
    private JLabel labelJogador;
    private JPanel painelDadosImagens;
    private JogoClueInicio partida;

    // Dentro de JanelaPrincipal.java
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JanelaPrincipal().setVisible(true);
            }
        });
    }

    public JanelaPrincipal() {
        // Configurações obrigatórias exigidas pelo enunciado
        setTitle("Clue - Detetive | Segunda Iteração");
        setSize(1350, 900); // Confortavelmente abaixo do limite máximo de 1400x1050
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // CORREÇÃO: Removido o tipo 'model.JogoClueInicio' para inicializar o campo da classe
        this.partida = new model.JogoClueInicio();

        // Passa o Model para dentro da View (O Tabuleiro)
        painelTabuleiro = new PainelTabuleiro(partida);
        add(new JScrollPane(painelTabuleiro), BorderLayout.CENTER);

        // Painel de Controle Lateral (Leste)
        JPanel painelLateral = new JPanel();
        painelLateral.setLayout(new BoxLayout(painelLateral, BoxLayout.Y_AXIS));
        painelLateral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelLateral.setPreferredSize(new Dimension(250, 900));

        // Título Lateral indicando o jogador da vez
        labelJogador = new JLabel();
        labelJogador.setFont(new Font("Arial", Font.BOLD, 14));
        labelJogador.setForeground(Color.RED); // Indicação visual clara solicitada no PDF
        painelLateral.add(labelJogador);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Seletores de dados (Dados Viciados de Teste)
        painelLateral.add(new JLabel("Dado 1:"));
        Integer[] faces = {1, 2, 3, 4, 5, 6};
        boxDado1 = new JComboBox<>(faces);
        painelLateral.add(boxDado1);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        painelLateral.add(new JLabel("Dado 2:"));
        boxDado2 = new JComboBox<>(faces);
        painelLateral.add(boxDado2);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Painel que ficará com a cor do jogador da vez e exibirá os dados
        labelImagemDado1 = new JLabel();
        labelImagemDado2 = new JLabel();
        painelDadosImagens = new JPanel(new FlowLayout());
        painelDadosImagens.setOpaque(true);
        painelDadosImagens.add(labelImagemDado1);
        painelDadosImagens.add(labelImagemDado2);
        painelLateral.add(painelDadosImagens);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton botaoDados = new JButton("Lançar Dados");{

            botaoDados.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int d1 = (Integer) boxDado1.getSelectedItem();
                    int d2 = (Integer) boxDado2.getSelectedItem();
                    int totalPassos = d1 + d2;

                    painelTabuleiro.setPassosDisponiveis(totalPassos);
                    atualizarImagensDosDados(d1, d2);
                }
            });
            painelLateral.add(botaoDados);
            add(painelLateral, BorderLayout.EAST);

            atualizarTurnoVisual();
        }
    }

    public void atualizarTurnoVisual() {
        String jogador = partida.getJogadorDaVez();
        labelJogador.setText("Vez de: " + jogador);

        // A própria View determina a cor correspondente ao nome do jogador
        painelDadosImagens.setBackground(obterCorDoJogador(jogador));
        painelDadosImagens.repaint();
    }


    // Metodo auxiliar da View para mapear os nomes às cores do Swing
    private Color obterCorDoJogador(String nomeJogador) {
        switch (nomeJogador) {
            case "Srta. Rose":
                return Color.RED;
            case "Coronel Mostarda":
                return Color.YELLOW;
            case "Professor Plum":
                return new Color(128, 0, 128); // Roxo clássico do Plum
            case "Sr. Marinho":
                return Color.GREEN;
            case "Dona Violeta":
                return Color.BLUE;
            case "Dona Branca":
                return Color.WHITE;
            default:
                return Color.LIGHT_GRAY;
        }
    }

    private void atualizarImagensDosDados(int valorD1, int valorD2) {
        try {
            // Mapeamento exato com a pasta Tabuleiros
            Image img1 = ImageIO.read(new File("resources/Tabuleiros/dado" + valorD1 + ".jpg"));
            Image img2 = ImageIO.read(new File("resources/Tabuleiros/dado" + valorD2 + ".jpg"));
            labelImagemDado1.setIcon(new ImageIcon(img1.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
            labelImagemDado2.setIcon(new ImageIcon(img2.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        }       catch (IOException e) {
            System.out.println("Erro ao carregar imagens dos dados: verifique o caminho resources/Tabuleiros/");
        }
    }
}