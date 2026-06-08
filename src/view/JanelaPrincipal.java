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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JanelaInicio().setVisible(true); // AGORA ABRE A JANELA INICIAL!
            }
        });
    }

    public JanelaPrincipal() {
        // Configurações básicas da janela principal
        setTitle("Clue - Detetive | Terceira Iteração");
        setSize(1350, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Recupera as instâncias necessárias através do Controller para manter o encapsulamento
        this.controller = ControllerClue.getInstancia();
        this.partida = controller.getModel();

        // Registra esta janela como um observador para ser notificada de mudanças de estado
        this.controller.registrarObservador(this);

        // Passamos o Model e a própria Janela para o Tabuleiro e o posicionamos no centro
        painelTabuleiro = new PainelTabuleiro(partida, this);
        add(new JScrollPane(painelTabuleiro), BorderLayout.CENTER);

        // Cria e configura o painel lateral de controle
        JPanel painelLateral = new JPanel();
        painelLateral.setLayout(new BoxLayout(painelLateral, BoxLayout.Y_AXIS));
        painelLateral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelLateral.setPreferredSize(new Dimension(250, 900));

        // Título que indica o jogador da vez
        labelJogador = new JLabel();
        labelJogador.setFont(new Font("Arial", Font.BOLD, 14));
        labelJogador.setForeground(Color.RED);
        painelLateral.add(labelJogador);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Seletores de dados para os testes manuais exigidos pelo professor
        painelLateral.add(new JLabel("Dado 1 (Teste):"));
        Integer[] faces = {1, 2, 3, 4, 5, 6};
        boxDado1 = new JComboBox<>(faces);
        painelLateral.add(boxDado1);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        painelLateral.add(new JLabel("Dado 2 (Teste):"));
        boxDado2 = new JComboBox<>(faces);
        painelLateral.add(boxDado2);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Painel colorido que reflete a cor do jogador atual e mostra os dados sorteados
        labelImagemDado1 = new JLabel();
        labelImagemDado2 = new JLabel();
        painelDadosImagens = new JPanel(new FlowLayout());
        painelDadosImagens.setOpaque(true);
        painelDadosImagens.add(labelImagemDado1);
        painelDadosImagens.add(labelImagemDado2);
        painelLateral.add(painelDadosImagens);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botão para o jogador consultar as cartas que tem na mão
        JButton botaoCartas = new JButton("Mostrar Cartas");
        botaoCartas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new JanelaCartas(JanelaPrincipal.this);
            }
        });
        painelLateral.add(botaoCartas);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        // Botão para o bloco de notas interativo
        JButton botaoNotas = new JButton("Bloco de Notas");
        botaoNotas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new JanelaBlocoNotas(JanelaPrincipal.this);
            }
        });
        painelLateral.add(botaoNotas);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botão de jogada normal com aleatoriedade dos dados
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

        // Botão de definição manual dos dados para facilitar correções e testes
        JButton botaoDadosTeste = new JButton("Definir Dados (Teste)");
        botaoDadosTeste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int d1 = (Integer) boxDado1.getSelectedItem();
                int d2 = (Integer) boxDado2.getSelectedItem();
                int totalPassos = d1 + d2;

                painelTabuleiro.setPassosDisponiveis(totalPassos);
                atualizarImagensDosDados(d1, d2);

                controller.rolarDados(d1, d2);
            }
        });
        painelLateral.add(botaoDadosTeste);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botão que permite teletransporte entre os cômodos nos cantos do tabuleiro
        JButton botaoPassagem = new JButton("Usar Passagem Secreta");
        botaoPassagem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String jogadorAtual = partida.getJogadorDaVez();
                boolean viajou = controller.usarPassagemSecreta(jogadorAtual);

                if (viajou) {
                    painelTabuleiro.setPassosDisponiveis(0);
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
        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        // Botão para que o detetive possa sugerir uma teoria de crime a partir de um cômodo
        JButton botaoPalpite = new JButton("Dar Palpite (Sugestão)");
        botaoPalpite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JComboBox<String> comboSuspeitos = new JComboBox<>(partida.getNomesSuspeitos().toArray(new String[0]));
                JComboBox<String> comboArmas = new JComboBox<>(partida.getNomesArmas().toArray(new String[0]));
                JComboBox<String> comboComodos = new JComboBox<>(partida.getNomesComodos().toArray(new String[0]));

                Object[] mensagem = {
                        "Qual suspeito você sugere?", comboSuspeitos,
                        "Com qual arma?", comboArmas,
                        "Em qual cômodo?", comboComodos
                };

                int opcao = JOptionPane.showConfirmDialog(JanelaPrincipal.this, mensagem, "Fazer um Palpite", JOptionPane.OK_CANCEL_OPTION);

                if (opcao == JOptionPane.OK_OPTION) {
                    String suspeito = (String) comboSuspeitos.getSelectedItem();
                    String arma = (String) comboArmas.getSelectedItem();
                    String comodo = (String) comboComodos.getSelectedItem();

                    String[] refutacao = controller.fazerPalpite(suspeito, arma, comodo);

                    if (refutacao != null) {
                        JOptionPane.showMessageDialog(JanelaPrincipal.this,
                                "O detetive " + refutacao[2] + " desmentiu a sua teoria e mostrou a carta:\n\n" + refutacao[0] + " (" + refutacao[1] + ")",
                                "Palpite Refutado!", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(JanelaPrincipal.this,
                                "Ninguém tem as cartas da sua teoria.\nIsso pode ser a solução do crime!",
                                "Ninguém Refutou!", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        painelLateral.add(botaoPalpite);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botão crítico do jogo: permite arriscar o resultado final contra o envelope
        JButton botaoAcusacao = new JButton("Fazer Acusação Final");
        botaoAcusacao.setBackground(Color.RED);
        botaoAcusacao.setForeground(Color.WHITE);
        botaoAcusacao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> comboSuspeitos = new JComboBox<>(partida.getNomesSuspeitos().toArray(new String[0]));
                JComboBox<String> comboArmas = new JComboBox<>(partida.getNomesArmas().toArray(new String[0]));
                JComboBox<String> comboComodos = new JComboBox<>(partida.getNomesComodos().toArray(new String[0]));

                Object[] mensagem = {
                        "Quem é o assassino?", comboSuspeitos,
                        "Com qual arma?", comboArmas,
                        "Em qual cômodo?", comboComodos
                };

                int opcao = JOptionPane.showConfirmDialog(JanelaPrincipal.this, mensagem, "Acusação Confidencial", JOptionPane.OK_CANCEL_OPTION);

                if (opcao == JOptionPane.OK_OPTION) {
                    String suspeito = (String) comboSuspeitos.getSelectedItem();
                    String arma = (String) comboArmas.getSelectedItem();
                    String comodo = (String) comboComodos.getSelectedItem();

                    boolean venceu = controller.fazerAcusacao(suspeito, arma, comodo);

                    if (venceu) {
                        JOptionPane.showMessageDialog(JanelaPrincipal.this,
                                "PARABÉNS! Descobriu o assassino e VENCEU o jogo!\nO crime foi cometido por " + suspeito + " com o/a " + arma + " no/na " + comodo + ".",
                                "Fim de Jogo - Vitória", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    } else {
                        JOptionPane.showMessageDialog(JanelaPrincipal.this,
                                "ACUSAÇÃO ERRADA! A sua teoria estava incorreta.\nVocê está eliminado da investigação e não pode mais jogar.",
                                "Eliminado!", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        painelLateral.add(botaoAcusacao);

        // Após adicionar todos os componentes no painel lateral, fixamos ele na porção direita do BorderLayout
        add(painelLateral, BorderLayout.EAST);

        // Garante a distribuição correta de cartas e status inicial ao criar a janela
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
