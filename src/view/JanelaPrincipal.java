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

// NOVO: Importação para trabalhar com a lista de personagens vindos da tela inicial
import java.util.List;

// ALTERAÇÃO 1: A classe agora implementa a interface Observador
public class JanelaPrincipal extends JFrame implements Observador {

    private PainelTabuleiro painelTabuleiro;

    // NOVO: Transformamos o painelLateral em um atributo da classe para podermos mudar a cor dele de qualquer método
    private JPanel painelLateral;

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
    // NOVO: Flag para rastrear se o jogador fez uma sugestão neste turno
    private boolean jaDeuPalpiteNesteTurno = false;

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
        setTitle("Clue - Detetive | Finalização");
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

        // NOVO: Usa a variável de classe instanciada, configurando o painel inteiro
        painelLateral = new JPanel();
        painelLateral.setLayout(new BoxLayout(painelLateral, BoxLayout.Y_AXIS));
        painelLateral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelLateral.setPreferredSize(new Dimension(250, 900));
        // NOVO: Garante que o painel pinta o seu fundo
        painelLateral.setOpaque(true);

        // Título que indica o jogador da vez
        labelJogador = new JLabel();
        labelJogador.setFont(new Font("Arial", Font.BOLD, 18)); // NOVO: Aumentei um pouco a fonte para destaque
        labelJogador.setForeground(Color.BLACK); // NOVO: Mudei para preto pois o fundo inteiro agora será colorido
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
        // NOVO: Agora que o painelLateral inteiro será colorido, o painelDadosImagens deve ser transparente
        painelDadosImagens.setOpaque(false);
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

        // ==========================================
        // NOVOS BOTÕES DA 4ª ITERAÇÃO: SALVAR E CARREGAR
        // ==========================================
        botaoSalvar = new JButton("Salvar Partida");
        botaoSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // Regra do professor: Apenas o filetype pode ser pré-definido
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

                // REGRA DA 4ª ITERAÇÃO: Desabilita salvar após rolar dados
                botaoSalvar.setEnabled(false);
            }
        });
        painelLateral.add(botaoRolarSorte);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 10)));

        // Botão de definição manual dos dados
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

                // REGRA DA 4ª ITERAÇÃO: Desabilita salvar após forçar os dados
                botaoSalvar.setEnabled(false);
            }
        });
        painelLateral.add(botaoDadosTeste);
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botão de passagem secreta
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

        // Botão para que o detetive possa sugerir uma teoria de crime a partir de um cômodo
        botaoPalpite = new JButton("Dar Palpite (Sugestão)");
        botaoPalpite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Pega o cômodo onde o jogador está no momento
                String comodoObrigatorio = controller.getComodoAtualJogador();

                JComboBox<String> comboSuspeitos = new JComboBox<>(partida.getNomesSuspeitos().toArray(new String[0]));
                JComboBox<String> comboArmas = new JComboBox<>(partida.getNomesArmas().toArray(new String[0]));

                // O JOptionPane agora tem um JLabel (texto fixo) para o cômodo em vez de combo box
                Object[] mensagem = {
                        "Qual suspeito você sugere?", comboSuspeitos,
                        "Com qual arma?", comboArmas,
                        "O cômodo é o atual:", new JLabel(comodoObrigatorio)
                };

                int opcao = JOptionPane.showConfirmDialog(JanelaPrincipal.this, mensagem, "Fazer um Palpite", JOptionPane.OK_CANCEL_OPTION);

                if (opcao == JOptionPane.OK_OPTION) {
                    String suspeito = (String) comboSuspeitos.getSelectedItem();
                    String arma = (String) comboArmas.getSelectedItem();
                    String comodo = comodoObrigatorio; // Usa o cômodo travado

                    String[] refutacao = controller.fazerPalpite(suspeito, arma, comodo);

                    if (refutacao != null && refutacao.length > 0 && refutacao[0].equals("ERRO_CONSECUTIVO")) {
                        JOptionPane.showMessageDialog(JanelaPrincipal.this,
                                "Ação Inválida! Você não pode fazer palpites consecutivos no mesmo cômodo sem sair dele primeiro.",
                                "Regra do Clue", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // NOVO: Registra o palpite, trava as opções de movimento e força o jogador a passar a vez
                    jaDeuPalpiteNesteTurno = true;
                    jaMoveuNesteTurno = true;
                    botaoRolarSorte.setEnabled(false);
                    botaoDadosTeste.setEnabled(false);
                    botaoPassagem.setEnabled(false);
                    botaoSalvar.setEnabled(false);
                    painelTabuleiro.setPassosDisponiveis(0);

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

        // Botão de acusação
        JButton botaoAcusacao = new JButton("Fazer Acusação Final");
        painelLateral.add(Box.createRigidArea(new Dimension(0, 20))); // Espaçamento

        JButton botaoPassarVez = new JButton("Passar a Vez");
        botaoPassarVez.setBackground(Color.DARK_GRAY);
        botaoPassarVez.setForeground(Color.WHITE);
        botaoPassarVez.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // NOVO: Validação das regras para permitir passar a vez (Andou tudo, Deu Palpite ou Está Preso)
                boolean terminouMovimento = jaMoveuNesteTurno && painelTabuleiro.getPassosDisponiveis() == 0;
                boolean preso = controller.isJogadorPreso(partida.getJogadorDaVez());

                if (terminouMovimento || jaDeuPalpiteNesteTurno || preso) {
                    painelTabuleiro.setPassosDisponiveis(0);
                    jaMoveuNesteTurno = false;
                    jaDeuPalpiteNesteTurno = false; // NOVO: Reseta a trava do palpite para o próximo jogador

                    // Manda reativar os botões de rolagem para o próximo da fila
                    botaoRolarSorte.setEnabled(true);
                    botaoDadosTeste.setEnabled(true);
                    botaoPassagem.setEnabled(true);

                    controller.encerrarTurno();
                } else {
                    // NOVO: Mensagens de erro explicativas dependendo de onde o jogador travou
                    if (jaMoveuNesteTurno && painelTabuleiro.getPassosDisponiveis() > 0) {
                        JOptionPane.showMessageDialog(JanelaPrincipal.this,
                                "Você rolou os dados e ainda tem passos disponíveis. Clique no tabuleiro para se mover!",
                                "Movimento Pendente", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(JanelaPrincipal.this,
                                "Ação Inválida! Para passar a vez, você deve se mover, dar um palpite ou estar com as saídas bloqueadas.",
                                "Bloqueio de Turno", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        painelLateral.add(botaoPassarVez);

        // NOVO: Nova cor chamativa (Laranja Escuro) aplicada no botão de acusação para se destacar do fundo inteiro colorido
        botaoAcusacao.setBackground(new Color(255, 0, 0)); // Laranja forte
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
                        // REGRA DA 4ª ITERAÇÃO: Término de Partida e Loop Principal
                        int resposta = JOptionPane.showConfirmDialog(JanelaPrincipal.this,
                                "PARABÉNS! Descobriu o assassino e VENCEU o jogo!\nO crime foi cometido por " + suspeito + " com o/a " + arma + " no/na " + comodo + ".\n\nDeseja jogar novamente?",
                                "Fim de Partida - Acusação Correta", JOptionPane.YES_NO_OPTION);

                        if (resposta == JOptionPane.YES_OPTION) {
                            ControllerClue.resetarJogo(); // Zera o Singleton
                            // Cumprindo a regra do PDF para reabrir a janela de seleção diretamente
                            JanelaInicio menu = new JanelaInicio();
                            menu.iniciarFluxoNovoJogo();
                            dispose(); // Fecha o tabuleiro atual
                        } else {
                            System.exit(0); // Encerra a aplicação
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

        // Após adicionar todos os componentes no painel lateral, fixamos ele na porção direita do BorderLayout
        add(painelLateral, BorderLayout.EAST);
    }

    // A assinatura mudou para receber os personagens selecionados e enviar ao controller
    public void iniciarPartidaComJogadores(int n, List<String> personagensSelecionados) {
        controller.iniciarPartida(n, personagensSelecionados);
        atualizarTurnoVisual();
    }


    @Override
    public void atualizar() {
        atualizarTurnoVisual();
        painelTabuleiro.repaint(); // Manda o tabuleiro se redesenhar sozinho
    }

    public void atualizarTurnoVisual() {
        String jogador = partida.getJogadorDaVez();
        labelJogador.setText("Vez de: " + jogador);

        // NOVO: Agora a cor de fundo é aplicada no painelLateral INTEIRO!
        painelLateral.setBackground(obterCorDoJogador(jogador));

        // NOVO: Redesenha o painel inteiro
        painelLateral.repaint();

        // 1. Reabilita o botão de salvar no início de um novo turno
        if (botaoSalvar != null) {
            botaoSalvar.setEnabled(!jaMoveuNesteTurno);
        }

        // 2. Trava o botão de Palpite se o jogador estiver no corredor
        String comodoAtual = controller.getComodoAtualJogador();
        if (botaoPalpite != null) {
            botaoPalpite.setEnabled(comodoAtual != null); // Só ativa se estiver num quarto!
        }

        // 3. Garante o reset visual dos botões de movimento no início de cada turno
        if (!jaMoveuNesteTurno) {
            if (botaoRolarSorte != null) botaoRolarSorte.setEnabled(true);
            if (botaoDadosTeste != null) botaoDadosTeste.setEnabled(true);
            if (botaoPassagem != null) botaoPassagem.setEnabled(true);
        }
    }

    // NOVO: Atualizado os valores RGB para tons mais "pastéis/claros".
    // Assim, o texto preto e os botões continuam legíveis na barra lateral inteira.
    private Color obterCorDoJogador(String nomeJogador) {
        switch (nomeJogador) {
            case "Srta. Rose":
                return new Color(255, 104, 104); // Rosa Claro / Vermelho Pastel
            case "Coronel Mostarda":
                return new Color(255, 255, 153); // Amarelo Pastel
            case "Professor Plum":
                return new Color(214, 122, 244); // Roxo Pastel / Thistle
            case "Sr. Marinho":
                return new Color(152, 251, 152); // Verde Claro Pastel
            case "Dona Violeta":
                return new Color(173, 216, 230); // Azul Claro Pastel
            case "Dona Branca":
                return new Color(245, 245, 245); // Branco Gelo / Off-white
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
