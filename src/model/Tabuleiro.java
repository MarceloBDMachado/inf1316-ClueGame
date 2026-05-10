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

    private void inicializarGrade() {
        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                grade[i][j] = new Casa(i, j, TipoCasa.CORREDOR);
            }
        }
    }

    Casa getCasa(int x, int y) {
        if (x >= 0 && x < LINHAS && y >= 0 && y < COLUNAS) {
            return grade[x][y];
        }
        return null;
    }

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

    List<Casa> mapearCasasAlcancaveis(Casa origem, int passos) {
        Set<Casa> casasAlcancaveis = new HashSet<>();
        Set<Casa> visitadas = new HashSet<>();

        buscarCaminhos(origem, passos, visitadas, casasAlcancaveis);

        return new ArrayList<>(casasAlcancaveis);
    }


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
                if (!visitadas.contains(vizinho) && !vizinho.isOcupada()) {
                    buscarCaminhos(vizinho, passosRestantes - 1, new HashSet<>(visitadas), alcancaveis);
                }
            }
        }
    }
}