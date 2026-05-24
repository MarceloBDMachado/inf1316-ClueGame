package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Tabuleiro {
    private final int LINHAS = 25;
    private final int COLUNAS = 24;
    private final Casa[][] grade;

    Tabuleiro() {
        grade = new Casa[LINHAS][COLUNAS];
        inicializarGrade();
    }

    // Inicializa o tabuleiro com base numa matriz visual (mapa)
    private void inicializarGrade() {
        // Mapa visual do Clue: 25 Linhas por 24 Colunas
        // 'C' = Corredor (onde se pode andar)
        // 'I' = Inacessível (Paredes, relva exterior, interior dos cómodos onde não há portas)
        String[] mapaBase = {
                "IIIIIIIIICIIIIICIIIIIIII", // Linha 0 (Topo)
                "IIIIIIIIICIIIIICIIIIIIII", // Linha 1
                "IIIIIIIIICIIIIICIIIIIIII", // Linha 2
                "IIIIIIIIICIIIIICIIIIIIII", // Linha 3
                "IIIIIIIIICIIIIICIIIIIIII", // Linha 4
                "IIIIIIIIICIIIIICIIIIIIII", // Linha 5
                "IIIIIIIICCCCCCCCIIIIIIIC", // Linha 6 (Início da Dona Violeta na ponta direita)
                "CCCCCCCCCCCCCCCCCCCCCCCC", // Linha 7 (Corredor principal superior)
                "IIIIIIICIIIIIIIICIIIIIII", // Linha 8
                "IIIIIIICIIIIIIIICIIIIIII", // Linha 9
                "IIIIIIICIIIIIIIICIIIIIII", // Linha 10
                "IIIIIIICIIIIIIIICIIIIIII", // Linha 11
                "IIIIIIICIIIIIIIICIIIIIII", // Linha 12
                "IIIIIIICIIIIIIIICIIIIIII", // Linha 13
                "IIIIIIICIIIIIIIICIIIIIII", // Linha 14
                "IIIIIIICIIIIIIIICIIIIIII", // Linha 15
                "CCCCCCCCCCCCCCCCCCCCCCCC", // Linha 16 (Corredor principal inferior)
                "CIIIIIIICIIIIIIICIIIIIII", // Linha 17 (Início do Coronel Mostarda na ponta esquerda)
                "IIIIIIIICIIIIIIICIIIIIII", // Linha 18
                "IIIIIIIICIIIIIIICIIIIIIC", // Linha 19 (Início do Prof. Plum na ponta direita)
                "IIIIIIIICIIIIIIICIIIIIII", // Linha 20
                "IIIIIIIICIIIIIIICIIIIIII", // Linha 21
                "IIIIIIIICIIIIIIICIIIIIII", // Linha 22
                "IIIIIIIICIIIIIIICIIIIIII", // Linha 23
                "IIIIIIICIIIIIIIICIIIIIII"  // Linha 24 (Início da Srta. Rose na coluna 7)
        };

        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                char tipo = mapaBase[i].charAt(j);
                if (tipo == 'C') {
                    grade[i][j] = new Casa(i, j, TipoCasa.CORREDOR);
                } else {
                    grade[i][j] = new Casa(i, j, TipoCasa.INACESSIVEL);
                }
            }
        }
    }

    // metodo de busca de casa
    Casa getCasa(int x, int y) {
        if (x >= 0 && x < LINHAS && y >= 0 && y < COLUNAS) {
            return grade[x][y];
        }
        return null;
    }

    // função para ver se é possivel mover o pião e garante que não possa ficar parado
    // casas inacessiveis ainda foram, no momento, definidos apenas como casas com outros suspeitos
    boolean moverPiao(Piao piao, Casa destino) {
        if (destino == null || destino.getTipo() == TipoCasa.INACESSIVEL || destino.isOcupada()) {
            return false;
        }

        Casa origem = piao.getPosicaoAtual();
        if (origem != null) {
            origem.setPiao(null);
        }

        destino.setPiao(piao);
        piao.setPosicaoAtual(destino);
        return true;
    }

    // inicializa as casas alcançaveis e as casas que ja foram visitadas,
    // impedindo loops e tempo gasto em jogadas idiotas (frente, trás, frente, trás...)
    List<Casa> mapearCasasAlcancaveis(Casa origem, int passos) {
        Set<Casa> casasAlcancaveis = new HashSet<>();
        Set<Casa> visitadas = new HashSet<>();

        buscarCaminhos(origem, passos, visitadas, casasAlcancaveis);

        return new ArrayList<>(casasAlcancaveis);
    }

    // função para buscar por onde o jogador pode passar
    // garante que o jogador pode mover de uma vez, sem que atravesse piões ou casas inacessíveis
    private void buscarCaminhos(Casa atual, int passosRestantes, Set<Casa> visitadas, Set<Casa> alcancaveis) {
        if (passosRestantes == 0) {
            alcancaveis.add(atual);
            return;
        }

        visitadas.add(atual);

        int[][] direcoes = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : direcoes) {
            Casa vizinho = getCasa(atual.getX() + dir[0], atual.getY() + dir[1]);

            if (vizinho != null && vizinho.getTipo() != TipoCasa.INACESSIVEL) {
                // garante que o movimento nao seja idiota (que faça duas jogadas que dão no mesmo lugar)
                // garante que a casa não seja ocupada
                if (!visitadas.contains(vizinho) && !vizinho.isOcupada()) {
                    buscarCaminhos(vizinho, passosRestantes - 1, new HashSet<>(visitadas), alcancaveis);
                }
            }
        }
    }
}