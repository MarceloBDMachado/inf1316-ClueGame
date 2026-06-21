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
        // ========================================================
        // DICIONÁRIO DE MAPEAMENTO
        // 'C' = Corredor
        // 'I' = Inacessível / Parede
        // Cômodos (Números):
        // 1=Cozinha, 2=Estar, 3=Jardim, 4=Jantar, 5=Escritório
        // 6=Biblioteca, 8=Jogos, 9=Música, 0=Entrada
        // Portas (Letras minúsculas - param o peão na porta e apontam para o quarto):
        // a=Cozinha, b=Estar, c=Jardim, d=Jantar, e=Escritório
        // f=Biblioteca, g=Jogos, h=Música, i=Entrada
        // ========================================================

        String[] mapaBase = {
                "111111IIICIIIICIIIIIIIII", // 0  (1 = Cozinha | Marinho no 14, Branca no 9)
                "111111ICCC9999CCCI333333", // 1  (9 = Música, 3 = Jardim)
                "111111CC99999999CC333333", // 2
                "111111CC99999999CC333333", // 3
                "111111CC99999999CC333333", // 4
                "111111CCh999999hCCc3333I", // 5  (h = Porta Música, c = Porta Jardim)
                "I111a1CC99999999CCCCCCCC", // 6  (a = Porta Cozinha | Violeta no 23)
                "CCCCCCCC9h9999h9CCCCCCCI", // 7  (h = Porta Música)
                "ICCCCCCCCCCCCCCCCC888888", // 8  (8 = Jogos)
                "44444CCCCCCCCCCCCCg88888", // 9  (4 = Jantar, g = Porta Jogos)
                "44444444CCIIIIICCC888888", // 10
                "44444444CCIIIIICCC888888", // 11
                "4444444dCCIIIIICCC8888g8", // 12 (d = Porta Jantar, g = Porta Jogos)
                "44444444CCIIIIICCCCCCCCI", // 13
                "44444444CCIIIIICCC66666I", // 14 (6 = Biblioteca)
                "444444d4CCIIIIICC6666666", // 15 (d = Porta Jantar)
                "ICCCCCCCCCIIIIICCf666666", // 16 (f = Porta Biblioteca | Plum no 23)
                "CCCCCCCCCCCCCCCCC6666666", // 17 (Mostarda no 0)
                "ICCCCCbCC00ii00CCC66666I", // 18 (b = Porta Estar, i = Porta Entrada | 0 = Entrada)
                "2222222CC000000CCCCCCCCC", // 19 (2 = Sala de Estar)
                "2222222CC00000iCCCCCCCCI", // 20 (i = Porta Entrada)
                "2222222CC000000CCe555555", // 21 (e = Porta Escritório | 5 = Escritório)
                "2222222CC000000CC5555555", // 22
                "2222222CC000000CC5555555", // 23
                "222222ICII0000IICI555555"  // 24 (Rose no 7)
        };

        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                char caractere = mapaBase[i].charAt(j);

                if (caractere == 'C') {
                    grade[i][j] = new Casa(i, j, TipoCasa.CORREDOR);
                } else if (caractere == 'I') {
                    grade[i][j] = new Casa(i, j, TipoCasa.INACESSIVEL);
                }
                // Mapeamento das Portas
                else if (caractere >= 'a' && caractere <= 'i') {
                    grade[i][j] = new Casa(i, j, TipoCasa.PORTA);
                    switch (caractere) {
                        case 'a': grade[i][j].setNomeComodo("Cozinha"); break;
                        case 'b': grade[i][j].setNomeComodo("Sala de Estar"); break;
                        case 'c': grade[i][j].setNomeComodo("Jardim de Inverno"); break;
                        case 'd': grade[i][j].setNomeComodo("Sala de Jantar"); break;
                        case 'e': grade[i][j].setNomeComodo("Escritório"); break;
                        case 'f': grade[i][j].setNomeComodo("Biblioteca"); break;
                        case 'g': grade[i][j].setNomeComodo("Salão de Jogos"); break;
                        case 'h': grade[i][j].setNomeComodo("Sala de Música"); break;
                        case 'i': grade[i][j].setNomeComodo("Entrada"); break;
                    }
                }
                // Mapeamento do Interior dos Cômodos
                else {
                    grade[i][j] = new Casa(i, j, TipoCasa.COMODO);
                    switch (caractere) {
                        case '1': grade[i][j].setNomeComodo("Cozinha"); break;
                        case '2': grade[i][j].setNomeComodo("Sala de Estar"); break;
                        case '3': grade[i][j].setNomeComodo("Jardim de Inverno"); break;
                        case '4': grade[i][j].setNomeComodo("Sala de Jantar"); break;
                        case '5': grade[i][j].setNomeComodo("Escritório"); break;
                        case '6': grade[i][j].setNomeComodo("Biblioteca"); break;
                        case '8': grade[i][j].setNomeComodo("Salão de Jogos"); break;
                        case '9': grade[i][j].setNomeComodo("Sala de Música"); break;
                        case '0': grade[i][j].setNomeComodo("Entrada"); break;
                    }
                }
            }
        }
    }

    Casa getCasa(int x, int y) {
        if (x >= 0 && x < LINHAS && y >= 0 && y < COLUNAS) {
            return grade[x][y];
        }
        return null;
    }

    // ========================================================
    // LÓGICA DE ALOCAÇÃO CENTRALIZADA DENTRO DO CÔMODO
    // ========================================================
    private Casa encontrarLugarNoComodo(String nomeComodo) {
        List<Casa> casasDoComodo = new ArrayList<>();
        int minX = LINHAS, maxX = 0, minY = COLUNAS, maxY = 0;

        // Acha a Bounding Box do cômodo ignorando as portas
        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                Casa c = grade[i][j];
                if (c.getTipo() == TipoCasa.COMODO && nomeComodo.equals(c.getNomeComodo())) {
                    casasDoComodo.add(c);
                    if (i < minX) minX = i;
                    if (i > maxX) maxX = i;
                    if (j < minY) minY = j;
                    if (j > maxY) maxY = j;
                }
            }
        }

        int centroX = (minX + maxX) / 2;
        int centroY = (minY + maxY) / 2;

        Casa melhorCasa = null;
        double menorDistancia = Double.MAX_VALUE;

        // Procura a casa vazia mais próxima do centro exato
        for (Casa c : casasDoComodo) {
            if (!c.isOcupada()) {
                double dist = Math.pow(c.getX() - centroX, 2) + Math.pow(c.getY() - centroY, 2);
                if (dist < menorDistancia) {
                    menorDistancia = dist;
                    melhorCasa = c;
                }
            }
        }
        return melhorCasa;
    }

    boolean moverPiao(Piao piao, Casa destino) {
        if (destino == null || destino.getTipo() == TipoCasa.INACESSIVEL) {
            return false;
        }

        // Se estiver indo para um cômodo (clicou na porta ou no chão), tenta alocar no centro
        if (destino.getTipo() == TipoCasa.COMODO || destino.getTipo() == TipoCasa.PORTA) {
            String nomeComodo = destino.getNomeComodo();
            Casa assentoCentral = encontrarLugarNoComodo(nomeComodo);
            if (assentoCentral != null) {
                destino = assentoCentral; // Substitui o destino pela cadeira livre no centro
            } else {
                return false; // Prevenção: Quarto completamente lotado
            }
        } else {
            // Se for movimento comum de corredor, valida ocupação
            if (destino.isOcupada()) return false;
        }

        Casa origem = piao.getPosicaoAtual();
        if (origem != null) {
            origem.setPiao(null);
        }

        destino.setPiao(piao);
        piao.setPosicaoAtual(destino);
        return true;
    }

    // PASSAGEM SECRETA
    boolean moverPorPassagemSecreta(Piao piao) {
        Casa origem = piao.getPosicaoAtual();
        if (origem == null || origem.getNomeComodo() == null) return false;

        String comodoAtual = origem.getNomeComodo();
        String nomeDestino = null;

        if (comodoAtual.equals("Cozinha")) nomeDestino = "Escritório";
        else if (comodoAtual.equals("Escritório")) nomeDestino = "Cozinha";
        else if (comodoAtual.equals("Jardim de Inverno")) nomeDestino = "Sala de Estar";
        else if (comodoAtual.equals("Sala de Estar")) nomeDestino = "Jardim de Inverno";

        if (nomeDestino != null) {
            // Basta achar o primeiro tile do cômodo destino. O método moverPiao calculará o centro!
            for (int i = 0; i < LINHAS; i++) {
                for (int j = 0; j < COLUNAS; j++) {
                    Casa c = grade[i][j];
                    if (nomeDestino.equals(c.getNomeComodo())) {
                        return moverPiao(piao, c);
                    }
                }
            }
        }
        return false;
    }

    // ========================================================
    // ALGORITMO DE CAMINHO (ENTRADA E SAÍDA INTELIGENTES)
    // ========================================================
    List<Casa> mapearCasasAlcancaveis(Casa origem, int passos) {
        Set<Casa> casasAlcancaveis = new HashSet<>();
        Set<Casa> visitadas = new HashSet<>();

        // Se o peão está DENTRO do quarto e vai iniciar sua caminhada de saída:
        if (origem.getTipo() == TipoCasa.COMODO || origem.getTipo() == TipoCasa.PORTA) {
            String nomeComodo = origem.getNomeComodo();

            // 1. Marca todo o cômodo atual como visitado para ele não tentar andar internamente
            for (int i = 0; i < LINHAS; i++) {
                for (int j = 0; j < COLUNAS; j++) {
                    Casa c = grade[i][j];
                    if (nomeComodo.equals(c.getNomeComodo())) {
                        visitadas.add(c);
                    }
                }
            }

            // 2. Transfere a largada para todas as portas do cômodo para o corredor adjacente (Custa 1 passo)
            for (int i = 0; i < LINHAS; i++) {
                for (int j = 0; j < COLUNAS; j++) {
                    Casa porta = grade[i][j];
                    if (porta.getTipo() == TipoCasa.PORTA && nomeComodo.equals(porta.getNomeComodo())) {
                        int[][] direcoes = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                        for (int[] dir : direcoes) {
                            Casa vizinho = getCasa(porta.getX() + dir[0], porta.getY() + dir[1]);
                            if (vizinho != null && vizinho.getTipo() == TipoCasa.CORREDOR && !vizinho.isOcupada()) {
                                // Subtrai 1 passo pois ele está pisando no corredor saindo da porta
                                buscarCaminhos(vizinho, passos - 1, visitadas, casasAlcancaveis);
                            }
                        }
                    }
                }
            }
        } else {
            // Se está num corredor normal, a busca acontece normalmente
            buscarCaminhos(origem, passos, visitadas, casasAlcancaveis);
        }

        // Prepara os cômodos de destino inteiros para serem clicados
        Set<Casa> comodosExtras = new HashSet<>();
        for (Casa c : casasAlcancaveis) {
            if (c.getTipo() == TipoCasa.PORTA || c.getTipo() == TipoCasa.COMODO) {
                String nome = c.getNomeComodo();
                for (int i = 0; i < LINHAS; i++) {
                    for (int j = 0; j < COLUNAS; j++) {
                        Casa quarto = grade[i][j];
                        if (quarto.getNomeComodo() != null && quarto.getNomeComodo().equals(nome)) {
                            comodosExtras.add(quarto);
                        }
                    }
                }
            }
        }
        casasAlcancaveis.addAll(comodosExtras);
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

            if (vizinho != null && !visitadas.contains(vizinho)) {

                boolean ocupadoBloqueante = vizinho.isOcupada() && vizinho.getTipo() == TipoCasa.CORREDOR;

                if (!ocupadoBloqueante) {
                    if (vizinho.getTipo() == TipoCasa.CORREDOR) {
                        // Passando a MESMA referência de 'visitadas' para eficiência de memória (Backtracking)
                        buscarCaminhos(vizinho, passosRestantes - 1, visitadas, alcancaveis);
                    } else if (vizinho.getTipo() == TipoCasa.PORTA) {
                        // Encostou na porta? Fim de rota, você entrou no quarto e perde os passos excedentes.
                        alcancaveis.add(vizinho);
                    }
                }
            }
        }
        // O SEGREDO DO BACKTRACKING: Libera a casa atual para ser usada em outras rotas!
        visitadas.remove(atual);
    }
}