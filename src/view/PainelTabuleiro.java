package view;

import controller.Controller;
import model.JogoClueInicio;
import model.ObservadoIF;
import model.ObservadorIF;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class PainelTabuleiro extends JPanel implements ObservadorIF {
    private Image imagemTabuleiro;
    private Map<String, Image> imagensPeoes = new HashMap<>();

    public PainelTabuleiro() {
        Controller.getInstance().registrarObservador(this);

        try {
            imagemTabuleiro = ImageIO.read(new File("resources/Tabuleiros/Tabuleiro-Clue-A.jpg"));

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

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int totalLinhas = 25;
                int totalColunas = 24;
                int larguraCasa = getWidth() / totalColunas;
                int alturaCasa = getHeight() / totalLinhas;

                int colunaLogica = e.getX() / larguraCasa;
                int linhaLogica = e.getY() / alturaCasa;

                Controller.getInstance().tentarMover(linhaLogica, colunaLogica);
            }
        });
    }

    @Override
    public void notify(ObservadoIF o) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (imagemTabuleiro != null) {
            g2d.drawImage(imagemTabuleiro, 0, 0, getWidth(), getHeight(), this);
        }

        int totalLinhas = 25;
        int totalColunas = 24;
        int larguraCasa = getWidth() / totalColunas;
        int alturaCasa = getHeight() / totalLinhas;

        JogoClueInicio jogo = Controller.getInstance().getPartida();

        if (jogo != null) {
            for (String nomeSuspeito : jogo.getNomesSuspeitos()) {
                int[] coords = jogo.getCoordenadasPiao(nomeSuspeito);

                if (coords != null) {
                    int piaoLinha = coords[0];
                    int piaoColuna = coords[1];

                    Image imgPiao = imagensPeoes.get(nomeSuspeito);
                    if (imgPiao != null) {
                        int pixelX = piaoColuna * larguraCasa;
                        int pixelY = piaoLinha * alturaCasa;
                        g2d.drawImage(imgPiao, pixelX + 2, pixelY + 2, larguraCasa - 4, alturaCasa - 4, this);
                    }
                }
            }
        }
    }
}