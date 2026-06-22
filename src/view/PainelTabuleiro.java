package view;

import model.JogoClueInicio; 
import controller.ControllerClue; 

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

    // Tamanho da grade
    private final int totalLinhas = 25;
    private final int totalColunas = 24;

    // Valores dos quadrados da imagem
    private final float propMargemEsq = 100.0f / 1350.0f;
    private final float propMargemTop = 60.0f / 900.0f;

    // Tamanho dos quadrados para que a grade termine onde a imagem termina
    private final float propLarguraCasa = 48.0f / 1350.0f;
    private final float propAlturaCasa = 31.0f / 900.0f;

    private JogoClueInicio jogo;
    private String jogadorDaVez = "Srta. Rose"; // Ajusta conforme a lógica de turnos
    private JanelaPrincipal janelaPai;

    // Mapeamento dos nomes do Model para os nomes das imagens
    private Map<String, String> mapeamentoNomesImagens = new HashMap<>();

    // Construtor recebe JogoClueInicio e JanelaPrincipal
    public PainelTabuleiro(JogoClueInicio jogo, JanelaPrincipal janelaPai) {
        this.jogo = jogo;
        this.janelaPai = janelaPai;

        // Carrega o tabuleiro
        try {
            imagemTabuleiro = ImageIO.read(new File("resources/Tabuleiros/Tabuleiro-Clue-A.JPG"));

            // Carrega os peões
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

        // Listener do mouse
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

                // Pega o jogador da vez direto do Model
                String jogadorDaVezAtual = jogo.getJogadorDaVez();
                System.out.println("Tentando mover " + jogadorDaVezAtual + " para [" + linhaLogica + "][" + colunaLogica + "]");

                boolean movimentoValido = ControllerClue.getInstancia().moverPiao(jogadorDaVezAtual, linhaLogica, colunaLogica, passosDisponiveis);

                if (movimentoValido) {
                    System.out.println("Peão movido com sucesso pelo Controller!");
                    passosDisponiveis = 0; // Gastou a jogada
                } else {
                    System.out.println("Movimento inválido. Ignorando.");
                }
            }
        });
    }

    public void setPassosDisponiveis(int passos) {
        this.passosDisponiveis = passos;
    }

    // Metodo para a View conseguir consultar se o jogador já terminou de gastar seus passos no mapa
    public int getPassosDisponiveis() {
        return this.passosDisponiveis;
    }

    // Permite mudar o jogador da vez
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

        // Pega todos os suspeitos ativos no jogo e desenha eles
        if (jogo != null) {
            for (String nomeSuspeito : jogo.getNomesSuspeitos()) {

                int[] coords = jogo.getCoordenadasPiao(nomeSuspeito);

                if (coords != null) {
                    int piaoLinha = coords[0]; // X
                    int piaoColuna = coords[1]; // Y

                    Image imgPiao = imagensPeoes.get(nomeSuspeito);
                    if (imgPiao != null) {
                        // --- AJUSTES NO PAINT COMPONENT ---
                        int pixelX = (int) (margemEsq + (piaoColuna * larguraCasa));
                        int pixelY = (int) (margemTop + (piaoLinha * alturaCasa));
                        g2d.drawImage(imgPiao, pixelX + 2, pixelY + 2, (int)larguraCasa - 4, (int)alturaCasa - 4, this);
                    }
                }
            }
        }
    }
}
