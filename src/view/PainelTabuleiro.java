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

    // Dentro do seu PainelTabuleiro.java, ajuste estas variáveis:

    // 1. Defina o tamanho da grade (confirme se é 24x24 ou 25x24)
    private final int totalLinhas = 25;
    private final int totalColunas = 24;

    // 2. Mude estes valores manuais até a grade vermelha encaixar nos quadrados da imagem
    private final float propMargemEsq = 100.0f / 1350.0f;
    private final float propMargemTop = 60.0f / 900.0f;

    // 3. Mude o tamanho dos quadrados para que a grade termine onde a imagem termina
    private final float propLarguraCasa = 48.0f / 1350.0f;
    private final float propAlturaCasa = 31.0f / 900.0f;



    // NOVO: Referência ao jogo (Controller/Model) e controle de turno
    private JogoClueInicio jogo;
    private String jogadorDaVez = "Srta. Rose"; // Ajuste conforme a lógica de turnos

    // Mapeamento dos nomes do Model para os nomes das imagens (como Scarlet virou Srta. Rose no seu Model)
    private Map<String, String> mapeamentoNomesImagens = new HashMap<>();

    public PainelTabuleiro(JogoClueInicio jogo) {
        this.jogo = jogo; // Injeta o jogo!

        // Carrega o tabuleiro
        try {
            imagemTabuleiro = ImageIO.read(new File("resources/Tabuleiros/Tabuleiro-Clue-A.JPG"));

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

                // --- AJUSTES NO MOUSE LISTENER ---
                float margemEsq = getWidth() * propMargemEsq;
                float margemTop = getHeight() * propMargemTop;
                float larguraCasa = getWidth() * propLarguraCasa;
                float alturaCasa = getHeight() * propAlturaCasa;

                int colunaLogica = (int) ((e.getX() - margemEsq) / larguraCasa);
                int linhaLogica = (int) ((e.getY() - margemTop) / alturaCasa);

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
    }

    // Metodo que permite mudar o jogador da vez quando a interface pedir
    public void setJogadorDaVez(String jogador) {
        this.jogadorDaVez = jogador;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Calcula os valores reais baseados no tamanho ATUAL da janela
        float margemEsq = getWidth() * propMargemEsq;
        float margemTop = getHeight() * propMargemTop;
        float larguraCasa = getWidth() * propLarguraCasa;
        float alturaCasa = getHeight() * propAlturaCasa;

        Graphics2D g2d = (Graphics2D) g;

        if (imagemTabuleiro != null) {
            g2d.drawImage(imagemTabuleiro, 0, 0, getWidth(), getHeight(), this);
        }

        // LÓGICA DE RENDERIZAÇÃO DE TODOS OS PEÕES USANDO O SEU MODEL

        // Pega todos os suspeitos ativos no seu jogo e desenha eles
        if (jogo != null) {
            for (String nomeSuspeito : jogo.getNomesSuspeitos()) {
                int[] coords = jogo.getCoordenadasPiao(nomeSuspeito);

                if (coords != null) {
                    int piaoLinha = coords[0]; // X na sua lógica
                    int piaoColuna = coords[1]; // Y na sua lógica

                    Image imgPiao = imagensPeoes.get(nomeSuspeito);
                    if (imgPiao != null) {
                        // --- AJUSTES NO PAINT COMPONENT ---
                        int pixelX = (int) (margemEsq + (piaoColuna * larguraCasa));
                        int pixelY = (int) (margemTop + (piaoLinha * alturaCasa));
                        // Centraliza o peão e diminui a imagem pra não ficar esticado
                        g2d.drawImage(imgPiao, pixelX + 2, pixelY + 2, (int)larguraCasa - 4, (int)alturaCasa - 4, this);
                    }
                }
            }
        }

        // Grade Vermelha de Debug
        g2d.setColor(Color.RED);
        for (int i = 0; i <= totalLinhas; i++) {
            g2d.drawLine((int)margemEsq, (int)(margemTop + (i * alturaCasa)), (int)(margemEsq + (totalColunas * larguraCasa)), (int)(margemTop + (i * alturaCasa)));
        }
        for (int j = 0; j <= totalColunas; j++) {
            g2d.drawLine((int)(margemEsq + (j * larguraCasa)), (int)margemTop, (int)(margemEsq + (j * larguraCasa)), (int)(margemTop + (totalLinhas * alturaCasa)));
        }
    }
}