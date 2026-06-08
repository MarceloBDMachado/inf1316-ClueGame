package view;

import javax.swing.*;
import java.awt.*;

public class JanelaBlocoNotas extends JDialog {

    public JanelaBlocoNotas(JFrame pai) {
        // Inicializa o JDialog como modal (bloqueia a janela de trás até fechar)
        super(pai, "Bloco de Notas de Palpites", true);
        setSize(600, 480); // Ajustado levemente para dar mais conforto vertical aos itens
        setLocationRelativeTo(pai);
        setLayout(new GridLayout(1, 3, 10, 10));

        // Arrays que condizem perfeitamente com o mapeamento de cartas do seu JogoClueInicio
        String[] suspeitos = {"Srta. Rose", "Coronel Mostarda", "Professor Plum", "Sr. Marinho", "Dona Violeta", "Dona Branca"};
        String[] armas = {"Corda", "Cano de Ferro", "Faca", "Chave Inglesa", "Castiçal", "Pistola"};
        String[] comodos = {"Cozinha", "Salão de Festas", "Salão de Jogos", "Biblioteca", "Escritório", "Sala de Estar", "Sala de Jantar", "Terraço", "Hall"};

        // Criação e adição direta dos componentes estruturados
        add(criarColunaDeNotas("Suspeitos", suspeitos));
        add(criarColunaDeNotas("Armas", armas));
        add(criarColunaDeNotas("Cômodos", comodos));

        // Torna visível apenas após a montagem completa da interface
        setVisible(true);
    }

    private JComponent criarColunaDeNotas(String titulo, String[] itens) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        for (String item : itens) {
            JCheckBox box = new JCheckBox(item);
            box.setFont(new Font("Arial", Font.PLAIN, 13));
            painel.add(box);
            painel.add(Box.createRigidArea(new Dimension(0, 3))); // Pequeno espaçamento vertical entre as caixas
        }

        // Adaptação de qualidade: o painel de caixas é envelopado em um JScrollPane rolável
        // A borda com o título da seção foi transferida para o ScrollPane para manter o design limpo
        JScrollPane scrollPane = new JScrollPane(painel);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Ajusta a velocidade do scroll do mouse para ficar suave
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        return scrollPane;
    }
}
