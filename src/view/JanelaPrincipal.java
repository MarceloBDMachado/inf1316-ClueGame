package view;

import javax.swing.*;
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

    public JanelaPrincipal() {
        // Configurações obrigatórias exigidas pelo enunciado
        setTitle("Clue - Detetive | Segunda Iteração");
        setSize(1350, 900); // Confortavelmente abaixo do limite máximo de 1400x1050
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Cria a API do jogo primeiro (O Model)
        model.JogoClueInicio partida = new model.JogoClueInicio();

        // Passa o Model para dentro da View (O Tabuleiro)
        painelTabuleiro = new PainelTabuleiro(partida);
        add(new JScrollPane(painelTabuleiro), BorderLayout.CENTER);

        // Painel de Controle Lateral (Leste)
        JPanel painelLateral = new JPanel();
        painelLateral.setLayout(new BoxLayout(painelLateral, BoxLayout.Y_AXIS));
        painelLateral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelLateral.setPreferredSize(new Dimension(250, 900));

        // Título Lateral indicando o jogador da vez
        JLabel labelJogador = new JLabel("Jogador da vez: Miss Scarlet");
        labelJogador.setFont(new Font("Arial", Font.BOLD, 14));
        labelJogador.setForeground(Color.RED); // Indicação visual clara solicitada no PDF
        painelLateral.add(labelJogador);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Seletores de dados (Dados Viciados de Teste)
        painelLateral.add(new JLabel("Dado de Teste 1:"));
        Integer[] faces = {1, 2, 3, 4, 5, 6};
        boxDado1 = new JComboBox<>(faces);
        painelLateral.add(boxDado1);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        painelLateral.add(new JLabel("Dado de Teste 2:"));
        boxDado2 = new JComboBox<>(faces);
        painelLateral.add(boxDado2);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Espaço para renderizar os ícones dos dados selecionados
        labelImagemDado1 = new JLabel();
        labelImagemDado2 = new JLabel();
        JPanel painelDadosImagens = new JPanel(new FlowLayout());
        painelDadosImagens.add(labelImagemDado1);
        painelDadosImagens.add(labelImagemDado2);
        painelLateral.add(painelDadosImagens);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botão para simular / confirmar os passos obtidos
        JButton btnConfirmarDados = new JButton("Confirmar Dados");

        // Listener clássico por classe anônima - Proibido Expressões Lambda aqui!
        btnConfirmarDados.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int d1 = (Integer) boxDado1.getSelectedItem();
                int d2 = (Integer) boxDado2.getSelectedItem();
                int totalPassos = d1 + d2;

                System.out.println("Dados confirmados! Total de passos para andar: " + totalPassos);
                painelTabuleiro.setPassosDisponiveis(totalPassos);

                // Atualiza visualmente as imagens dos dados selecionados no painel lateral
                atualizarImagensDosDados(d1, d2);
            }
        });

        painelLateral.add(btnConfirmarDados);

        add(painelLateral, BorderLayout.EAST);
    }

    // Função interna para carregar os JPEGs/PNGs dos dados selecionados na tela
    private void atualizarImagensDosDados(int valorD1, int valorD2) {
        try {
            Image img1 = ImageIO.read(new File("resources/Tabuleiros/dado" + valorD1 + ".jpg"));
            Image img2 = ImageIO.read(new File("resources/Tabuleiros/dado" + valorD2 + ".jpg"));

            // Redimensiona levemente para caber no painel lateral
            labelImagemDado1.setIcon(new ImageIcon(img1.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
            labelImagemDado2.setIcon(new ImageIcon(img2.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            System.out.println("Aviso: Falha ao desenhar miniaturas das faces dos dados.");
        }
    }

    public static void main(String[] args) {
        // Inicialização thread-safe do Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JanelaPrincipal().setVisible(true);
            }
        });
    }
}