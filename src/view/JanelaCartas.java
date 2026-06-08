package view;

import controller.ControllerClue;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class JanelaCartas extends JDialog {

    private PainelVisualCartas painelVisual;

    public JanelaCartas(JFrame pai) {
        // Inicializa o JDialog como modal
        super(pai, "Suas Cartas", true);
        setSize(700, 260);
        setLocationRelativeTo(pai);
        setLayout(new BorderLayout());

        // Recupera as instâncias via Controller
        ControllerClue controller = ControllerClue.getInstancia();
        List<String[]> cartas = controller.obterDadosCartasDoJogadorAtual();
        String nomeJogador = controller.getModel().getJogadorDaVez();

        // 1. Título
        JLabel labelTitulo = new JLabel("Cartas de: " + nomeJogador, SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        labelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(labelTitulo, BorderLayout.NORTH);

        // 2. Instancia o Painel Customizado que usará Java2D
        painelVisual = new PainelVisualCartas(cartas);

        // 3. Coloca o painel dentro de um JScrollPane para comportar muitas cartas
        JScrollPane scrollPane = new JScrollPane(painelVisual);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    // ========================================================
    // PAINEL INTERNO: Renderiza as cartas usando exclusivamente Java2D e drawImage()
    // Isso evita o uso de JLabel/JPanel internos para as imagens, respeitando as regras.
    // ========================================================
    private class PainelVisualCartas extends JPanel {
        private List<String[]> cartas;
        private Map<String, Image> cacheImagens = new HashMap<>();

        // Dimensões de cada carta desenhada
        private final int CARTA_LARGURA = 110;
        private final int CARTA_ALTURA = 160;
        private final int ESPACAMENTO = 15;

        public PainelVisualCartas(List<String[]> cartas) {
            this.cartas = cartas;

            // Define o tamanho preferencial do painel com base no número de cartas
            // para que a barra de rolagem horizontal funcione corretamente
            int qtdCartas = (cartas == null) ? 0 : cartas.size();
            int larguraNecessaria = ESPACAMENTO + (qtdCartas * (CARTA_LARGURA + ESPACAMENTO));
            setPreferredSize(new Dimension(Math.max(larguraNecessaria, 680), 200));
            setBackground(Color.WHITE);

            carregarImagens();
        }

        private void carregarImagens() {
            if (cartas == null) return;

            for (String[] c : cartas) {
                String nomeCarta = c[0];
                String caminhoImg = obterCaminhoImagemCarta(nomeCarta);

                if (caminhoImg != null) {
                    try {
                        Image img = ImageIO.read(new File(caminhoImg));
                        cacheImagens.put(nomeCarta, img);
                    } catch (IOException e) {
                        System.out.println("Aviso: Falha ao carregar imagem para: " + nomeCarta + " - Usando modo texto.");
                    }
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Suaviza o desenho das bordas e textos
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (cartas == null || cartas.isEmpty()) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.ITALIC, 14));
                g2d.drawString("Nenhuma carta na mão deste jogador.", 20, 100);
                return;
            }

            int posX = ESPACAMENTO;
            int posY = ESPACAMENTO; // Margem superior

            for (String[] c : cartas) {
                String nomeCarta = c[0];
                String tipoCarta = c[1];
                Image img = cacheImagens.get(nomeCarta);

                // Desenha a borda preta da carta
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(posX, posY, CARTA_LARGURA, CARTA_ALTURA);

                if (img != null) {
                    // DESENHA A IMAGEM VIA METODO drawImage() DO JAVA2D (REGRA DO PDF)
                    g2d.drawImage(img, posX + 2, posY + 2, CARTA_LARGURA - 4, CARTA_ALTURA - 4, this);
                } else {
                    // FALLBACK: Se não achar a imagem, desenha o texto dentro do retângulo da carta
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));

                    // Centralizar nome da carta
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(nomeCarta);
                    g2d.drawString(nomeCarta, posX + (CARTA_LARGURA - textWidth) / 2, posY + (CARTA_ALTURA / 2) - 10);

                    // Centralizar o tipo da carta embaixo
                    g2d.setColor(Color.GRAY);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                    fm = g2d.getFontMetrics();
                    textWidth = fm.stringWidth(tipoCarta);
                    g2d.drawString(tipoCarta, posX + (CARTA_LARGURA - textWidth) / 2, posY + (CARTA_ALTURA / 2) + 15);
                }

                // Avança para a posição da próxima carta
                posX += CARTA_LARGURA + ESPACAMENTO;
            }
        }
    }

    // ========================================================
    // MÉTODO TRADUTOR: Conecta o nome do Model ao arquivo real
    // ========================================================
    private String obterCaminhoImagemCarta(String nomeCarta) {
        switch (nomeCarta) {
            case "Srta. Rose": return "resources/Suspeitos/Scarlet.jpg";
            case "Coronel Mostarda": return "resources/Suspeitos/Mustard.jpg";
            case "Professor Plum": return "resources/Suspeitos/Plum.jpg";
            case "Sr. Marinho": return "resources/Suspeitos/Green.jpg";
            case "Dona Violeta": return "resources/Suspeitos/Peacock.jpg";
            case "Dona Branca": return "resources/Suspeitos/White.jpg";

            case "Corda": return "resources/Armas/Corda.jpg";
            case "Cano de Ferro": return "resources/Armas/Cano.jpg";
            case "Faca": return "resources/Armas/Faca.jpg";
            case "Chave Inglesa": return "resources/Armas/ChaveInglesa.jpg";
            case "Castiçal": return "resources/Armas/Castical.jpg";
            case "Pistola": return "resources/Armas/Revolver.jpg";

            case "Cozinha": return "resources/Comodos/Cozinha.jpg";
            case "Sala de Música": return "resources/Comodos/SalaDeMusica.jpg";
            case "Salão de Jogos": return "resources/Comodos/SalaoDeJogos.jpg";
            case "Biblioteca": return "resources/Comodos/Biblioteca.jpg";
            case "Escritório": return "resources/Comodos/Escritorio.jpg";
            case "Sala de Estar": return "resources/Comodos/SalaDeEstar.jpg";
            case "Sala de Jantar": return "resources/Comodos/SalaDeJantar.jpg";
            case "Jardim de Inverno": return "resources/Comodos/JardimInverno.jpg";
            case "Entrada": return "resources/Comodos/Entrada.jpg";

            default: return null;
        }
    }
}