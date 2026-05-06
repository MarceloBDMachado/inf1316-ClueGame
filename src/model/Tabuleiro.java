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

    /**
     * Preenche a matriz bidimensional com instâncias de Casas.
     * Por padrão, faremos tudo como CORREDOR para podermos testar a movimentação.
     * (A geometria exata dos cômodos será definida aqui futuramente).
     */
    private void inicializarGrade() {
        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                grade[i][j] = new Casa(i, j, TipoCasa.CORREDOR);
            }
        }
    }

    /**
     * Retorna a Casa específica em uma coordenada.
     */
    Casa getCasa(int x, int y) {
        if (x >= 0 && x < LINHAS && y >= 0 && y < COLUNAS) {
            return grade[x][y];
        }
        return null;
    }

    /**
     * Move um peão de sua casa atual para a casa de destino.
     */
    boolean moverPiao(Piao piao, Casa destino) {
        if (destino == null || destino.getTipo() == TipoCasa.INACESSIVEL || destino.isOcupada()) {
            return false; // Movimento inválido
        }

        Casa origem = piao.getPosicaoAtual();
        if (origem != null) {
            origem.setPiao(null); // Esvazia a casa antiga
        }

        destino.setPiao(piao); // Ocupa a nova casa
        piao.setPosicaoAtual(destino); // Atualiza a coordenada no peão
        return true;
    }

    /**
     * Mapeia todas as casas que podem ser alcançadas a partir de uma origem.
     */
    List<Casa> mapearCasasAlcancaveis(Casa origem, int passos) {
        Set<Casa> casasAlcancaveis = new HashSet<>();
        Set<Casa> visitadas = new HashSet<>();

        buscarCaminhos(origem, passos, visitadas, casasAlcancaveis);

        return new ArrayList<>(casasAlcancaveis);
    }

    /**
     * Algoritmo de Busca em Profundidade (DFS) para explorar os caminhos válidos.
     */
    private void buscarCaminhos(Casa atual, int passosRestantes, Set<Casa> visitadas, Set<Casa> alcancaveis) {
        // Se acabaram os passos, adiciona a casa final na lista de alcançáveis
        if (passosRestantes == 0) {
            alcancaveis.add(atual);
            return;
        }

        visitadas.add(atual);

        // Movimentos possíveis: Cima, Baixo, Esquerda, Direita
        int[][] direcoes = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : direcoes) {
            Casa vizinho = getCasa(atual.getX() + dir[0], atual.getY() + dir[1]);

            // Verifica se o vizinho existe, não é parede e não está ocupado por outro peão
            if (vizinho != null && vizinho.getTipo() != TipoCasa.INACESSIVEL) {
                if (!visitadas.contains(vizinho) && !vizinho.isOcupada()) {
                    // Clona o set de visitadas para esta ramificação do caminho
                    buscarCaminhos(vizinho, passosRestantes - 1, new HashSet<>(visitadas), alcancaveis);
                }
            }
        }
    }
}