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
        // '0'=Cozinha, '1'=Salão Festas, '2'=Salão Jogos, '3'=Biblioteca, '4'=Escritório
        // '5'=Sala Estar, '6'=Sala Jantar, '7'=Terraço, '8'=Hall
        String[] mapaBase = {
                "000000IIIC1111CII2222222",
                "000000IIC111111CII222222",
                "000000IC11111111CI222222",
                "000000IC11111111CI222222",
                "000000IC11111111C2222222",
                "000000IC11111111CII22222",
                "000000IICCCCCCCCIII2222C",
                "CCCCCCCCCCCCCCCCCCCCCCCC",
                "IIIIIIIC88888888CII33333",
                "666666IC88888888CI333333",
                "666666IC88888888CI333333",
                "666666IC88888888CI333333",
                "666666IC88888888CIIIIIII",
                "666666IC88888888CII44444",
                "666666IC88888888CI444444",
                "666666IICCCCCCCCIC444444",
                "CCCCCCCCCCCCCCCCCCCCCCCC",
                "CIIIIIIIC7777777C4444444",
                "5555555IC7777777CIIIIIII",
                "5555555IC7777777CIIIIII2",
                "5555555IC7777777CIIIIIII",
                "5555555IC7777777CIIIIIII",
                "5555555IC7777777CIIIIIII",
                "5555555IC7777777CIIIIIII",
                "555555ICIIIIIIIICIIIIIII"
        };

        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                char tipo = mapaBase[i].charAt(j);
                if (tipo == 'C') {
                    grade[i][j] = new Casa(i, j, TipoCasa.CORREDOR);
                } else if (tipo == 'I') {
                    grade[i][j] = new Casa(i, j, TipoCasa.INACESSIVEL);
                } else {
                    grade[i][j] = new Casa(i, j, TipoCasa.COMODO);
                    configurarNomeComodo(grade[i][j], tipo);
                }
            }
        }
    }

    private void configurarNomeComodo(Casa casa, char cod) {
        switch (cod) {
            case '0': casa.setNomeComodo("Cozinha"); break;
            case '1': casa.setNomeComodo("Salão de Festas"); break;
            case '2': casa.setNomeComodo("Salão de Jogos"); break;
            case '3': casa.setNomeComodo("Biblioteca"); break;
            case '4': casa.setNomeComodo("Escritório"); break;
            case '5': casa.setNomeComodo("Sala de Estar"); break;
            case '6': casa.setNomeComodo("Sala de Jantar"); break;
            case '7': casa.setNomeComodo("Terraço"); break;
            case '8': casa.setNomeComodo("Hall"); break;
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
                    if (vizinho.getTipo() == TipoCasa.COMODO) {
                        alcancaveis.add(vizinho);
                    } else {
                        buscarCaminhos(vizinho, passosRestantes - 1, new HashSet<>(visitadas), alcancaveis);
                    }
                }
            }
        }
    }

    //novo passagem
    public Casa encontrarCasaLivre(String nomeComodo) {
        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                Casa c = grade[i][j];
                if (c != null && c.getTipo() == TipoCasa.COMODO &&
                        nomeComodo.equals(c.getNomeComodo()) && !c.isOcupada()) {
                    return c;
                }
            }
        }
        return null;
    }
}

