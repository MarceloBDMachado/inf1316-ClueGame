package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JanelaInicio extends JFrame {

    public JanelaInicio() {
        setTitle("Clue - Início");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra a janela no ecrã
        setLayout(new BorderLayout());

        // Título estilizado (podes trocar por uma imagem do Clue se tiveres depois)
        JLabel titulo = new JLabel("CLUE - DETETIVE", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 36));
        titulo.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        add(titulo, BorderLayout.NORTH);

        // Painel para conter os botões
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
                // Fecha este ecrã inicial e abre o jogo principal
                dispose();
                new JanelaPrincipal().setVisible(true);
            }
        });

        // Botão Continuar
        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 18));
        btnContinuar.setPreferredSize(new Dimension(150, 50));
        btnContinuar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Como o "Continuar" é só na 4ª iteração, deixamos um aviso
                JOptionPane.showMessageDialog(JanelaInicio.this,
                        "A funcionalidade de carregar jogo será implementada apenas na 4ª iteração!",
                        "Em breve", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        painelBotoes.add(btnNovoJogo);
        painelBotoes.add(btnContinuar);

        add(painelBotoes, BorderLayout.CENTER);
    }
}