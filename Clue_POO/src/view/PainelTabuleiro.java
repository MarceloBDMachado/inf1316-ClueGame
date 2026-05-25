package view;

import model.JogoClueInicio; // Importa a fachada do Model

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class PainelTabuleiro extends JPanel {
    private Image imagemTabuleiro;
    private int passosDisponiveis = 0;
    private Map<String, Image> imagensPeoes = new HashMap<>();

    // NOVO: Referência ao jogo (Controller/Model) e controle de turno
    private JogoClueInicio jogo;
    private String jogadorDaVez = "Srta. Rose"; // Ajuste conforme a lógica de turnos

    // Mapeamento dos nomes do Model para os nomes das imagens (como Scarlet virou Srta. Rose no seu Model)
    private Map<String, String> mapeamentoNomesImagens = new HashMap<>();

    public PainelTabuleiro(JogoClueInicio jogo) {
        this.jogo = jogo; // Injeta o jogo!

        // Carrega o tabuleiro
        try {
            imagemTabuleiro = ImageIO.read(new File("resources/Tabuleiros/Tabuleiro-Original.JPG"));

            // Carrega os peões (O HashMap de nomes foi adaptado para a realidade do seu model)
            imagensPeoes.put("Srta. Rose", ImageIO.read(new File("resources/Suspeitos/Scarlet.jpg")));
            imagensPeoes.put("Coronel Mostarda", ImageIO.read(new File("resources/Suspeitos/Mustard.jpg")));
            imagensPeoes.put("Professor Plum", ImageIO.read(new File("resources/Suspeitos/Plum.jpg")));
            imagensPeoes.put("Sr. Marinho", ImageIO.read(new File("resources/Suspeitos/Green.jpg")));
            imagensPeoes.put("Dona Violeta", ImageIO.read(new File("resources/Suspeitos/Peacock.jpg")));
            imagensPeoes.put("Dona Branca", ImageIO.read(new File("resources/Suspeitos/White.jpg")));

        } catch (IOException e) {
            System.out.println("Erro crítico: Falha ao carregar imagens!");
            e.printStackTrace();
        }

        // Listener do mouse (A Lógica de Movimento Real)
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (passosDisponiveis <= 0) {
                    System.out.println("Lance os dados ou defina-os antes de tentar mover!");
                    return;
                }

                int totalLinhas = 25;
                int totalColunas = 24;
                int larguraCasa = getWidth() / totalColunas;
                int alturaCasa = getHeight() / totalLinhas;

                // Transforma pixel em coordenada do seu Tabuleiro.java
                int colunaLogica = e.getX() / larguraCasa;
                int linhaLogica = e.getY() / alturaCasa;

                System.out.println("Tentando mover " + jogadorDaVez + " para [" + linhaLogica + "][" + colunaLogica + "]");

                // CONTROLLER REAL: Manda a requisição de movimento para a sua fachada JogoClueInicio
                boolean movimentoValido = jogo.deslocarPiao(jogadorDaVez, linhaLogica, colunaLogica, passosDisponiveis);
                if (movimentoValido) {
                    System.out.println("Peão movido com sucesso no Model!");
                    passosDisponiveis = 0; // Gastou a jogada
                    repaint(); // Redesenha a tela com as novas posições
                } else {
                    System.out.println("Movimento inválido. Ignorando.");
                }
            }
        });
    }

    public void setPassosDisponiveis(int passos) {
        this.passosDisponiveis = passos;

        // Opcional: Aqui nós já poderíamos chamar a função jogo.mapearCasasPossiveis
        // e pintar o caminho de amarelo na tela (usando FillRect com opacidade).
        // Mas se a falta de tempo apertar, o deslocarPiao já valida a posição.
    }

    // Metodo que permite mudar o jogador da vez quando a interface pedir
    public void setJogadorDaVez(String jogador) {
        this.jogadorDaVez = jogador;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (imagemTabuleiro != null) {
            g2d.drawImage(imagemTabuleiro, 0, 0, getWidth(), getHeight(), this);
        }

        // LÓGICA DE RENDERIZAÇÃO DE TODOS OS PEÕES USANDO O SEU MODEL
        int totalLinhas = 25;
        int totalColunas = 24;
        int larguraCasa = getWidth() / totalColunas;
        int alturaCasa = getHeight() / totalLinhas;

        // Pega todos os suspeitos ativos no seu jogo e desenha eles
        if (jogo != null) {
            for (String nomeSuspeito : jogo.getNomesSuspeitos()) {
                int[] coords = jogo.getCoordenadasPiao(nomeSuspeito);

                if (coords != null) {
                    int piaoLinha = coords[0]; // X na sua lógica
                    int piaoColuna = coords[1]; // Y na sua lógica

                    Image imgPiao = imagensPeoes.get(nomeSuspeito);
                    if (imgPiao != null) {
                        int pixelX = piaoColuna * larguraCasa;
                        int pixelY = piaoLinha * alturaCasa;
                        // Centraliza o peão e diminui a imagem pra não ficar esticado
                        g2d.drawImage(imgPiao, pixelX + 2, pixelY + 2, larguraCasa - 4, alturaCasa - 4, this);
                    }
                }
            }
        }
    }
}