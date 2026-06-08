package view;

import javax.swing.*;
import controller.Controller;
import model.Carta;
import model.JogoClueInicio;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;

public class JanelaCartas extends JDialog {

    public JanelaCartas(JFrame pai) {
        super(pai, "Suas Cartas", true);
        setSize(750, 400);
        setLocationRelativeTo(pai);
        setLayout(new BorderLayout());

        // Busca o estado atual do jogo no Controller
        JogoClueInicio jogo = Controller.getInstance().getPartida();
        int idJogador = jogo.getIndiceJogadorDaVez();
        List<Carta> cartas = jogo.getCartasDoJogador(idJogador);

        // Painel central para listar as cartas lado a lado
        JPanel painelCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (cartas.isEmpty()) {
            JLabel lblVazio = new JLabel("Nenhuma carta na mão deste jogador.");
            lblVazio.setFont(new Font("Arial", Font.ITALIC, 16));
            painelCentral.add(lblVazio);
        } else {
            // Define o tamanho padrão das cartas na tela
            int larguraCarta = 140;
            int alturaCarta = 210;

            for (Carta c : cartas) {
                JPanel painelCartaVisual = new JPanel(new BorderLayout());
                painelCartaVisual.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                painelCartaVisual.setPreferredSize(new Dimension(larguraCarta, alturaCarta));
                painelCartaVisual.setBackground(Color.WHITE);

                try {
                    // Tenta carregar a imagem dinamicamente baseada no nome da carta
                    String caminhoDaImagem = gerarCaminhoDaImagem(c);
                    Image imgBruta = ImageIO.read(new File(caminhoDaImagem));
                    
                    // Escala a imagem para caber no painel mantendo a proporção visual
                    Image imgEscalada = imgBruta.getScaledInstance(larguraCarta, alturaCarta, Image.SCALE_SMOOTH);
                    JLabel lblImagem = new JLabel(new ImageIcon(imgEscalada));
                    painelCartaVisual.add(lblImagem, BorderLayout.CENTER);

                } catch (IOException ex) {
                    // Fallback visual: Se a imagem não for encontrada, mostra o texto para o jogo não quebrar
                    JLabel lblErro = new JLabel("<html><center>IMAGEM FALTANDO<br><br><b>" + 
                                              c.getNome() + "</b><br>(" + c.getTipo() + ")</center></html>", SwingConstants.CENTER);
                    lblErro.setForeground(Color.RED);
                    painelCartaVisual.setBackground(Color.LIGHT_GRAY);
                    painelCartaVisual.add(lblErro, BorderLayout.CENTER);
                    
                    System.out.println("Aviso: Imagem não encontrada no caminho: " + gerarCaminhoDaImagem(c));
                }

                painelCentral.add(painelCartaVisual);
            }
        }

        JLabel lblTitulo = new JLabel("Cartas de: " + jogo.getJogadorDaVez(), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        add(lblTitulo, BorderLayout.NORTH);
        add(new JScrollPane(painelCentral), BorderLayout.CENTER); // Adiciona scroll caso a tela fique cheia
        setVisible(true);
    }

    private String gerarCaminhoDaImagem(Carta c) {
        String pastaBase = "resources/Cartas/"; 
        

        String nomeArquivo = c.getNome()
                .replace(" ", "")
                .replace("ã", "a")
                .replace("õ", "o")
                .replace("ç", "c")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ê", "e")
                .replace(".", "");
                
        return pastaBase + nomeArquivo + ".jpg";
    }
}
