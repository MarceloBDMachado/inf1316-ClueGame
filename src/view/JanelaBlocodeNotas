package view;

import javax.swing.*;
import java.awt.*;

public class JanelaBlocoNotas extends JDialog {

    public JanelaBlocoNotas(JFrame pai) {
        super(pai, "Bloco de Notas de Palpites", true);
        setSize(600, 450);
        setLocationRelativeTo(pai);
        setLayout(new GridLayout(1, 3, 10, 10));

        String[] suspeitos = {"Srta. Rose", "Coronel Mostarda", "Professor Plum", "Sr. Marinho", "Dona Violeta", "Dona Branca"};
        String[] armas = {"Corda", "Cano de Ferro", "Faca", "Chave Inglesa", "Castiçal", "Pistola"};
        String[] comodos = {"Cozinha", "Salão de Festas", "Salão de Jogos", "Biblioteca", "Escritório", "Sala de Estar", "Sala de Jantar", "Terraço", "Hall"};

        JPanel colSuspeitos = criarColunaDeNotas("Suspeitos", suspeitos);
        JPanel colArmas = criarColunaDeNotas("Armas", armas);
        JPanel colComodos = criarColunaDeNotas("Cômodos", comodos);

        add(colSuspeitos);
        add(colArmas);
        add(colComodos);

        setVisible(true);
    }

    private JPanel criarColunaDeNotas(String titulo, String[] itens) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createTitledBorder(titulo));

        for (String item : itens) {
            JCheckBox box = new JCheckBox(item);
            painel.add(box);
        }
        return painel;
    }
}
