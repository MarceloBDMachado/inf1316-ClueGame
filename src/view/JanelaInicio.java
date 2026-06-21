package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

// NOVO: Importações necessárias para a tela de seleção de imagens
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

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
                iniciarFluxoNovoJogo();
            }
        });

        // Botão Continuar
        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 18));
        btnContinuar.setPreferredSize(new Dimension(150, 50));
        btnContinuar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivo de Texto (*.txt)", "txt"));

                int escolha = fileChooser.showOpenDialog(JanelaInicio.this);

                if (escolha == JFileChooser.APPROVE_OPTION) {
                    File arquivoSelecionado = fileChooser.getSelectedFile();

                    ControllerClue.resetarJogo();
                    JanelaPrincipal janelaJogo = new JanelaPrincipal();
                    janelaJogo.setVisible(true);

                    ControllerClue.getInstancia().carregarPartida(arquivoSelecionado);
                    dispose();
                }
            }
        });

        painelBotoes.add(btnNovoJogo);
        painelBotoes.add(btnContinuar);

        add(painelBotoes, BorderLayout.CENTER);
    }

    // NOVO: Isolamos o fluxo de Novo Jogo para poder ser chamado diretamente após uma vitória
    public void iniciarFluxoNovoJogo() {
        String[] opcoes = {"3", "4", "5", "6"};
        String num = (String) JOptionPane.showInputDialog(this,
                "Quantos jogadores vão participar?",
                "Novo Jogo",
                JOptionPane.QUESTION_MESSAGE,
                null, opcoes, opcoes[0]);

        if (num != null) {
            int numJogadores = Integer.parseInt(num);
            abrirJanelaSelecao(numJogadores);
        } else {
            // Se cancelar, garante que o menu continue visível
            this.setVisible(true);
        }
    }

    // NOVO: Método para abrir a seleção de imagens de piões baseada na quantidade escolhida
    private void abrirJanelaSelecao(int numJogadores) {
        JDialog dialog = new JDialog(this, "Escolha " + numJogadores + " Personagens", true);
        dialog.setSize(650, 550);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JLabel labelInstrucao = new JLabel("Selecione os " + numJogadores + " detetives que entrarão na mansão:", SwingConstants.CENTER);
        labelInstrucao.setFont(new Font("Arial", Font.BOLD, 16));
        labelInstrucao.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        dialog.add(labelInstrucao, BorderLayout.NORTH);

        JPanel panelImagens = new JPanel(new GridLayout(2, 3, 15, 15));
        panelImagens.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        String[] nomes = {"Srta. Rose", "Coronel Mostarda", "Dona Branca", "Sr. Marinho", "Dona Violeta", "Professor Plum"};
        String[] caminhos = {
                "resources/Suspeitos/Scarlet.jpg",
                "resources/Suspeitos/Mustard.jpg",
                "resources/Suspeitos/White.jpg",
                "resources/Suspeitos/Green.jpg",
                "resources/Suspeitos/Peacock.jpg",
                "resources/Suspeitos/Plum.jpg"
        };

        List<String> selecionados = new ArrayList<>();
        JButton btnIniciar = new JButton("Confirmar e Iniciar");
        btnIniciar.setFont(new Font("Arial", Font.BOLD, 18));
        btnIniciar.setEnabled(false);

        for (int i = 0; i < nomes.length; i++) {
            String nome = nomes[i];
            JButton btnIcon = new JButton();
            btnIcon.setLayout(new BorderLayout());

            try {
                Image img = ImageIO.read(new File(caminhos[i]));
                btnIcon.setIcon(new ImageIcon(img.getScaledInstance(130, 180, Image.SCALE_SMOOTH)));
            } catch (Exception ex) {
                btnIcon.setText(nome);
            }

            btnIcon.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

            // NOVO: Lambdas removidas e substituídas por ActionListener tradicional
            btnIcon.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (selecionados.contains(nome)) {
                        selecionados.remove(nome);
                        btnIcon.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                    } else if (selecionados.size() < numJogadores) {
                        selecionados.add(nome);
                        btnIcon.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
                    }

                    btnIniciar.setEnabled(selecionados.size() == numJogadores);
                    labelInstrucao.setText("Escolhidos: " + selecionados.size() + " / " + numJogadores);
                }
            });

            panelImagens.add(btnIcon);
        }

        // NOVO: Lambdas removidas e substituídas por ActionListener tradicional
        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ControllerClue.resetarJogo();
                JanelaPrincipal jogo = new JanelaPrincipal();
                jogo.iniciarPartidaComJogadores(numJogadores, selecionados);
                jogo.setVisible(true);

                dialog.dispose();
                dispose();
            }
        });

        dialog.add(panelImagens, BorderLayout.CENTER);

        JPanel panelSul = new JPanel();
        panelSul.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelSul.add(btnIniciar);
        dialog.add(panelSul, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
