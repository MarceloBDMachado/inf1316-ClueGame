package view;

import javax.swing.*;

import controller.ControllerClue;
import observer.Observador;
import model.JogoClueInicio;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

// NOVO: Importação para trabalhar com a lista de personagens vindos da tela inicial
import java.util.List;

public class JanelaPrincipal extends JFrame implements Observador {

    private PainelTabuleiro painelTabuleiro;
    private JComboBox<Integer> boxDado1;
    private JComboBox<Integer> boxDado2;
    private JLabel labelImagemDado1;
    private JLabel labelImagemDado2;
    private JLabel labelJogador;
    private JPanel painelDadosImagens;
    private JogoClueInicio partida;
    private JButton botaoPalpite;
    private JButton botaoSalvar;
    private JButton botaoCarregar;
    private JButton botaoRolarSorte;
    private JButton botaoDadosTeste;
    private JButton botaoPassagem;
    private boolean jaMoveuNesteTurno = false;

    private ControllerClue controller;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JanelaInicio().setVisible(true);
            }
        });
    }

    public JanelaPrincipal() {
        setTitle("Clue - Detetive | Finalização");
        setSize(1350, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        this.controller = ControllerClue.getInstancia();
        this.partida = controller.getModel();
        this.controller.registrarObservador(this);

        painelTabuleiro = new PainelTabuleiro(partida, this);
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

        painelLateral.add(new JLabel("Dado 1 (Teste):"));
        Integer[] faces = {1, 2, 3, 4, 5, 6};
        boxDado1 = new JComboBox<>(faces);
        painelLateral.add(boxDado1);

        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        painelLateral.add(new JLabel("Dado 2 (Teste):"));
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

        JButton botaoCartas = new JButton("Mostrar Cartas");
        botaoCartas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new JanelaCartas(JanelaPrincipal.this);
            }
        });
        painelLateral.add(botaoCartas);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        botaoSalvar = new JButton("Salvar Partida");
        botaoSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivo de Texto (*.txt)", "txt"));
                if (fileChooser.showSaveDialog(JanelaPrincipal.this) == JFileChooser.APPROVE_OPTION) {
                    File arquivo = fileChooser.getSelectedFile();
                    if (!arquivo.getName().endsWith(".txt")) {
                        arquivo = new File(arquivo.getAbsolutePath() + ".txt");
                    }
                    controller.salvarPartida(arquivo);
                    JOptionPane.showMessageDialog(JanelaPrincipal.this, "Jogo salvo com sucesso!");
                }
            }
        });
        painelLateral.add(botaoSalvar);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        botaoCarregar = new JButton("Carregar Partida");
        botaoCarregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivo de Texto (*.txt)", "txt"));
                if (fileChooser.showOpenDialog(JanelaPrincipal.this) == JFileChooser.APPROVE_OPTION) {
                    controller.carregarPartida(fileChooser.getSelectedFile());
                    JOptionPane.showMessageDialog(JanelaPrincipal.this, "Jogo carregado com sucesso!");
                }
            }
        });
        painelLateral.add(botaoCarregar);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton botaoNotas = new JButton("Bloco de Notas");
        botaoNotas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new JanelaBlocoNotas(JanelaPrincipal.this);
            }
        });
        painelLateral.add(botaoNotas);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        botaoRolarSorte = new JButton("Rolar Dados (Sorte)");
        botaoRolarSorte.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jaMoveuNesteTurno = true;
                botaoRolarSorte.setEnabled(false);
                botaoDadosTeste.setEnabled(false);
                botaoPassagem.setEnabled(false);
                int[] dados = controller.rolarDadosAleatorios();
                int d1 = dados[0];
                int d2 = dados[1];
                int totalPassos = d1 + d2;

                painelTabuleiro.setPassosDisponiveis(totalPassos);
                atualizarImagensDosDados(d1, d2);

                botaoSalvar.setEnabled(false);
            }
        });
        painelLateral.add(botaoRolarSorte);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        botaoDadosTeste = new JButton("Definir Dados (Teste)");
        botaoDadosTeste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jaMoveuNesteTurno = true;
                botaoRolarSorte.setEnabled(false);
                botaoDadosTeste.setEnabled(false);
                botaoPassagem.setEnabled(false);
                int d1 = (Integer) boxDado1.getSelectedItem();
                int d2 = (Integer) boxDado2.getSelectedItem();
                int totalPassos = d1 + d2;

                painelTabuleiro.setPassosDisponiveis(totalPassos);
                atualizarImagensDosDados(d1, d2);

                controller.rolarDados(d1, d2);

                botaoSalvar.setEnabled(false);
            }
        });
        painelLateral.add(botaoDadosTeste);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        botaoPassagem = new JButton("Usar Passagem Secreta");
        botaoPassagem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String jogadorAtual = partida.getJogadorDaVez();
                boolean viajou = controller.usarPassagemSecreta(jogadorAtual);

                if (viajou) {
                    jaMoveuNesteTurno = true;
                    botaoRolarSorte.setEnabled(false);
                    botaoDadosTeste.setEnabled(false);
                    botaoPassagem.setEnabled(false);

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

        botaoPalpite = new JButton("Dar Palpite (Sugestão)");
        botaoPalpite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String comodoObrigatorio = controller.getComodoAtualJogador();

                JComboBox<String> comboSuspeitos = new JComboBox<>(partida.getNomesSuspeitos().toArray(new String[0]));
                JComboBox<String> comboArmas = new JComboBox<>(partida.getNomesArmas().toArray(new String[0]));

                Object[] mensagem = {
                        "Qual suspeito você sugere?", comboSuspeitos,
                        "Com qual arma?", comboArmas,
                        "O cômodo é o atual:", new JLabel(comodoObrigatorio)
                };

                int opcao = JOptionPane.showConfirmDialog(JanelaPrincipal.this, mensagem, "Fazer um Palpite", JOptionPane.OK_CANCEL_OPTION);

                if (opcao == JOptionPane.OK_OPTION) {
                    String suspeito = (String) comboSuspeitos.getSelectedItem();
                    String arma = (String) comboArmas.getSelectedItem();
                    String comodo = comodoObrigatorio;

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

        JButton botaoAcusacao = new JButton("Fazer Acusação Final");
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));
        JButton botaoPassarVez = new JButton("Passar a Vez");
        botaoPassarVez.setBackground(Color.DARK_GRAY);
        botaoPassarVez.setForeground(Color.WHITE);
        botaoPassarVez.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                painelTabuleiro.setPassosDisponiveis(0);
                jaMoveuNesteTurno = false;

                botaoRolarSorte.setEnabled(true);
                botaoDadosTeste.setEnabled(true);
                botaoPassagem.setEnabled(true);

                controller.encerrarTurno();
            }
        });
        painelLateral.add(botaoPassarVez);
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
                        int resposta = JOptionPane.showConfirmDialog(JanelaPrincipal.this,
                                "PARABÉNS! Descobriu o assassino e VENCEU o jogo!\nO crime foi cometido por " + suspeito + " com o/a " + arma + " no/na " + comodo + ".\n\nDeseja jogar novamente?",
                                "Fim de Partida - Acusação Correta", JOptionPane.YES_NO_OPTION);

                        if (resposta == JOptionPane.YES_OPTION) {
                            ControllerClue.resetarJogo();
                            // NOVO: Cumprindo a regra do PDF para reabrir a janela de seleção diretamente
                            JanelaInicio menu = new JanelaInicio();
                            menu.iniciarFluxoNovoJogo();
                            dispose();
                        } else {
                            System.exit(0);
                        }
                    } else {
                        JOptionPane.showMessageDialog(JanelaPrincipal.this,
                                "ACUSAÇÃO ERRADA! A sua teoria estava incorreta.\nVocê está eliminado da investigação e não pode mais jogar.",
                                "Eliminado!", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        painelLateral.add(botaoAcusacao);

        add(painelLateral, BorderLayout.EAST);
    }

    public void iniciarPartidaComJogadores(int n, List<String> personagensSelecionados) {
        controller.iniciarPartida(n, personagensSelecionados);
        atualizarTurnoVisual();
    }


    @Override
    public void atualizar() {
        atualizarTurnoVisual();
        painelTabuleiro.repaint();
    }

    public void atualizarTurnoVisual() {
        String jogador = partida.getJogadorDaVez();
        labelJogador.setText("Vez de: " + jogador);

        painelDadosImagens.setBackground(obterCorDoJogador(jogador));
        painelDadosImagens.repaint();

        if (botaoSalvar != null) {
            botaoSalvar.setEnabled(!jaMoveuNesteTurno);
        }

        String comodoAtual = controller.getComodoAtualJogador();
        if (botaoPalpite != null) {
            botaoPalpite.setEnabled(comodoAtual != null);
        }

        if (!jaMoveuNesteTurno) {
            if (botaoRolarSorte != null) botaoRolarSorte.setEnabled(true);
            if (botaoDadosTeste != null) botaoDadosTeste.setEnabled(true);
            if (botaoPassagem != null) botaoPassagem.setEnabled(true);
        }
    }

    private Color obterCorDoJogador(String nomeJogador) {
        switch (nomeJogador) {
            case "Srta. Rose":
                return Color.RED;
            case "Coronel Mostarda":
                return Color.YELLOW;
            case "Professor Plum":
                return new Color(128, 0, 128);
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
            Image img1 = ImageIO.read(new File("resources/Tabuleiros/dado" + valorD1 + ".jpg"));
            Image img2 = ImageIO.read(new File("resources/Tabuleiros/dado" + valorD2 + ".jpg"));
            labelImagemDado1.setIcon(new ImageIcon(img1.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
            labelImagemDado2.setIcon(new ImageIcon(img2.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            System.out.println("Erro ao carregar imagens dos dados: verifique o caminho resources/Tabuleiros/");
        }
    }

}