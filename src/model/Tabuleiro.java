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

    //inicializa o tabuleiro como uma grande grande com apenas corredores 
    private void inicializarGrade() {
        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                grade[i][j] = new Casa(i, j, TipoCasa.CORREDOR);
            }
        }
    }

    // procura uma casa
    Casa getCasa(int x, int y) {
        if (x >= 0 && x < LINHAS && y >= 0 && y < COLUNAS) {
            return grade[x][y];
        }
        return null;
    }

    // função para ver se é possivel mover o pião e garante que não possa ficar parado
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

    // inicializa as casas alcansaveis e as casas que ja foram visitadas, impedindo loops e tempo gasto em jogadas idiotas (frente, trás, frente, trás...)
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