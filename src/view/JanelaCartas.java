package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;

import controller.ControllerClue;
import model.Carta;

public class JanelaCartas extends JDialog {

    public JanelaCartas(JFrame pai) {
        // Inicializa o JDialog como modal
        super(pai, "Suas Cartas", true);
        setSize(700, 260);
        setLocationRelativeTo(pai);
        setLayout(new BorderLayout());

        // Recupera as instâncias via Controller
        ControllerClue controller = ControllerClue.getInstancia();
        List<Carta> cartas = controller.obterCartasDoJogadorAtual();
        String nomeJogador = controller.getModel().getJogadorDaVez();

        JPanel painelCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (cartas == null || cartas.isEmpty()) {
            JLabel lblAviso = new JLabel("Nenhuma carta na mão deste jogador.");
            lblAviso.setFont(new Font("Arial", Font.ITALIC, 14));
            painelCentral.add(lblAviso);
        } else {
            for (Carta c : cartas) {
                JPanel painelCartaVisual = new JPanel();
                painelCartaVisual.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                painelCartaVisual.setPreferredSize(new Dimension(110, 160));
                painelCartaVisual.setLayout(new BorderLayout());
                painelCartaVisual.setBackground(Color.WHITE);

                // Chama o nosso método mapeador para descobrir onde está a imagem correta
                String caminhoImg = obterCaminhoImagemCarta(c.getNome());

                try {
                    if (caminhoImg == null) {
                        throw new IOException("Mapeamento não encontrado para a carta: " + c.getNome());
                    }

                    Image imgOriginal = ImageIO.read(new File(caminhoImg));
                    ImageIcon iconeRedimensionado = new ImageIcon(
                        imgOriginal.getScaledInstance(110, 160, Image.SCALE_SMOOTH)
                    );
                    JLabel lblImagem = new JLabel(iconeRedimensionado);
                    painelCartaVisual.add(lblImagem, BorderLayout.CENTER);

                } catch (IOException e) {
                    // Fallback visual caso a imagem falhe (Mostra o texto puro)
                    System.out.println("Aviso: " + e.getMessage() + " - Usando modo texto.");
                    JLabel lblTexto = new JLabel(
                        "<html><center><font size='4'><b>" + c.getNome() + "</b></font><br><br><font color='gray'>" + c.getTipo() + "</font></center></html>", 
                        SwingConstants.CENTER
                    );
                    painelCartaVisual.add(lblTexto, BorderLayout.CENTER);
                }

                painelCentral.add(painelCartaVisual);
            }
        }

        JLabel labelTitulo = new JLabel("Cartas de: " + nomeJogador, SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        labelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(labelTitulo, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(painelCentral);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    // ========================================================
    // MÉTODO TRADUTOR: Conecta o nome do Model ao arquivo real
    // ========================================================
    private String obterCaminhoImagemCarta(String nomeCarta) {
        switch (nomeCarta) {
            // SUSPEITOS
            case "Srta. Rose": return "resources/Suspeitos/Scarlet.jpg";
            case "Coronel Mostarda": return "resources/Suspeitos/Mustard.jpg";
            case "Professor Plum": return "resources/Suspeitos/Plum.jpg";
            case "Sr. Marinho": return "resources/Suspeitos/Green.jpg";
            case "Dona Violeta": return "resources/Suspeitos/Peacock.jpg";
            case "Dona Branca": return "resources/Suspeitos/White.jpg";

            // ARMAS
            case "Corda": return "resources/Armas/Corda.jpg";
            case "Cano de Ferro": return "resources/Armas/Cano.jpg";
            case "Faca": return "resources/Armas/Faca.jpg";
            case "Chave Inglesa": return "resources/Armas/ChaveInglesa.jpg";
            case "Castiçal": return "resources/Armas/Castical.jpg";
            case "Pistola": return "resources/Armas/Revolver.jpg";

            // CÔMODOS (Trata as diferenças de nomeação entre o Model e os Arquivos)
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
