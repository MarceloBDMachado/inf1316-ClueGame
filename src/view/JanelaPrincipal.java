package view;

import javax.swing.*;

// NOVAS IMPORTAÇÕES PARA OS PADRÕES DE PROJETO
import controller.ControllerClue;
import observer.Observador;
import model.JogoClueInicio;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

// ALTERAÇÃO 1: A classe agora implementa a interface Observador
public class JanelaPrincipal extends JFrame implements Observador {

    private PainelTabuleiro painelTabuleiro;
    private JComboBox<Integer> boxDado1;
    private JComboBox<Integer> boxDado2;
    private JLabel labelImagemDado1;
    private JLabel labelImagemDado2;
    private JLabel labelJogador;
    private JPanel painelDadosImagens;
    private JogoClueInicio partida;

    // ALTERAÇÃO 2: A View agora tem uma referência para o Controller
    private ControllerClue controller;

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
        setTitle("Clue - Detetive | Terceira Iteração");
        setSize(1350, 900); // Confortavelmente abaixo do limite máximo de 1400x1050
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ALTERAÇÃO 3: Em vez de dar 'new' no Model, pegamos a instância pelo Controller
        this.controller = ControllerClue.getInstancia();
        this.partida = controller.getModel();

        // ALTERAÇÃO 4: Registra esta janela como um observador (Padrão Observer)
        this.controller.registrarObservador(this);

        // MUDANÇA AQUI: Passamos o Model (partida) e a própria Janela (this) para o Tabuleiro
        painelTabuleiro = new PainelTabuleiro(partida, this);
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

        // Adiciona checklist para o jogador
        JButton botaoNotas = new JButton("Bloco de Notas");
        botaoNotas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new JanelaBlocoNotas(JanelaPrincipal.this);
            }
        });
        painelLateral.add(botaoNotas);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Seletores de dados (Dados Viciados de Teste)
        painelLateral.add(new JLabel("Dado 1 (Teste):"));
        Integer[] faces = {1, 2, 3, 4, 5, 6};
        boxDado1 = new JComboBox<>(faces);
        painelLateral.add(boxDado1);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        painelLateral.add(new JLabel("Dado 2 (Teste):"));
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

        // ========================================================
        // ADIÇÃO CIRÚRGICA DOS BOTÕES DE AÇÃO (3ª ITERAÇÃO)
        // ========================================================

        // 1. Botão para rolar os dados de forma completamente aleatória (Mecânica de Sorte)
        JButton botaoRolarSorte = new JButton("Rolar Dados (Sorte)");
        botaoRolarSorte.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] dados = controller.rolarDadosAleatorios();
                int d1 = dados[0];
                int d2 = dados[1];
                int totalPassos = d1 + d2;

                painelTabuleiro.setPassosDisponiveis(totalPassos);
                atualizarImagensDosDados(d1, d2);
            }
        });
        painelLateral.add(botaoRolarSorte);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        // 2. Botão de Definir Dados (Mecanismo viciado exigido pelo professor para testar)
        JButton botaoDadosTeste = new JButton("Definir Dados (Teste)");
        botaoDadosTeste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int d1 = (Integer) boxDado1.getSelectedItem();
                int d2 = (Integer) boxDado2.getSelectedItem();
                int totalPassos = d1 + d2;

                painelTabuleiro.setPassosDisponiveis(totalPassos);
                atualizarImagensDosDados(d1, d2);

                // Avisar o controller que os dados manuais foram setados
                controller.rolarDados(d1, d2);
            }
        });
        painelLateral.add(botaoDadosTeste);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. Botão de ativação de Passagem Secreta
        JButton botaoPassagem = new JButton("Usar Passagem Secreta");
        botaoPassagem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String jogadorAtual = partida.getJogadorDaVez();
                boolean viajou = controller.usarPassagemSecreta(jogadorAtual);

                if (viajou) {
                    painelTabuleiro.setPassosDisponiveis(0); // Zera o movimento pois usou a passagem
                    JOptionPane.showMessageDialog(JanelaPrincipal.this,
                            jogadorAtual + " utilizou com sucesso as passagens secretas da mansão!",
                            "Passagem Secreta", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(JanelaPrincipal.this,
                            "Ação Inválida! Você precisa estar nos cômodos dos cantos (Cozinha, Escritório, Sala de Estar ou Jardim de Inverno) para usar passagens.",
                            "Bloqueio de Movimento", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        painelLateral.add(botaoPassagem);

        add(painelLateral, BorderLayout.EAST);

        // ALTERAÇÃO 6: Garante que as cartas são distribuídas na inicialização da janela
        controller.iniciarPartida(6);

        atualizarTurnoVisual();
    }

    // ==========================================
    // ALTERAÇÃO 7: Método obrigatório da interface Observador
    // Quando o Model mudar, ele chama esse método automaticamente.
    // ==========================================
    @Override
    public void atualizar() {
        atualizarTurnoVisual();
        painelTabuleiro.repaint(); // Manda o tabuleiro se redesenhar sozinho
    }
    // ==========================================

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
        } catch (IOException e) {
            System.out.println("Erro ao carregar imagens dos dados: verifique o caminho resources/Tabuleiros/");
        }
    }
}
