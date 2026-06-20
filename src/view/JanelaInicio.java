package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import controller.ControllerClue;

public class JanelaInicio extends JFrame {

    public JanelaInicio() {
        setTitle("Clue - Início");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra a janela no ecrã
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("CLUE - DETETIVE", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 36));
        titulo.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        add(titulo, BorderLayout.NORTH);

        // Painel
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 50));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        // Botão Novo Jogo
        JButton btnNovoJogo = new JButton("Novo Jogo");
        btnNovoJogo.setFont(new Font("Arial", Font.BOLD, 18));
        btnNovoJogo.setPreferredSize(new Dimension(150, 50));
        btnNovoJogo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pergunta o número de jogadores
                String[] opcoes = {"3", "4", "5", "6"};
                String num = (String) JOptionPane.showInputDialog(JanelaInicio.this,
                        "Quantos jogadores vão participar?",
                        "Novo Jogo",
                        JOptionPane.QUESTION_MESSAGE,
                        null, opcoes, opcoes[0]);

                if (num != null) {
                    int numJogadores = Integer.parseInt(num);
                    ControllerClue.resetarJogo();

                    // Inicia o jogo com a quantidade escolhida
                    JanelaPrincipal jogo = new JanelaPrincipal();
                    jogo.iniciarPartidaComJogadores(numJogadores);
                    jogo.setVisible(true);

                    dispose();
                }
            }
        });

        // Botão Continuar (ATUALIZADO PARA A 4ª ITERAÇÃO)
        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 18));
        btnContinuar.setPreferredSize(new Dimension(150, 50));
        btnContinuar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // Exigência do professor: definir apenas o filetype como pré-definido (.txt)
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivo de Texto (*.txt)", "txt"));

                int escolha = fileChooser.showOpenDialog(JanelaInicio.this);

                if (escolha == JFileChooser.APPROVE_OPTION) {
                    File arquivoSelecionado = fileChooser.getSelectedFile();

                    // 1. Reseta o Singleton para limpar qualquer resquício de lógicas anteriores
                    ControllerClue.resetarJogo();

                    // 2. Abre a Janela Principal (o tabuleiro do jogo)
                    JanelaPrincipal janelaJogo = new JanelaPrincipal();
                    janelaJogo.setVisible(true);

                    // 3. Solicita ao Controller carregar o arquivo TXT restaurando o estado da partida
                    ControllerClue.getInstancia().carregarPartida(arquivoSelecionado);

                    // 4. Fecha a tela de menu inicial de forma limpa
                    dispose();
                }
            }
        });

        painelBotoes.add(btnNovoJogo);
        painelBotoes.add(btnContinuar);

        add(painelBotoes, BorderLayout.CENTER);
    }
}