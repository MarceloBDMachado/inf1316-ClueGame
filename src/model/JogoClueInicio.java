package model;

import java.util.*;


// Inicializa o inicio do Jogo
public class JogoClueInicio {
    private Dado dado1;
    private Dado dado2;
    private Tabuleiro tabuleiro;

    //HashMap mapeando os piões, o baralho e as mãos dos jogadores
    private Map<String, Piao> pioes;
    private Map<String, Carta> baralho;
    private Map<Integer, List<Carta>> maosJogadores;
    private Envelope envelopeConfidencial;

    // Inicializa todas as diferentes funcionalidades para o Jogo
    public JogoClueInicio() {
        this.dado1 = new Dado();
        this.dado2 = new Dado();
        this.tabuleiro = new Tabuleiro();
        this.pioes = new HashMap<>();
        this.baralho = new HashMap<>();
        this.maosJogadores = new HashMap<>();
        this.envelopeConfidencial = new Envelope();

        inicializarCartas();
        inicializarPioes();
    }

    // função simples para sempre girar os dois dados ao mesmo tempo
    public int[] rolarDados() {
        return new int[]{dado1.rolar(), dado2.rolar()};
    }

    // no começo do jogo dá nome de um suspeito para cada um dos piões
    // passa o valor dos dados e a posição atual para fazer as casas alcançáveis
    public List<Casa> mapearCasasPossiveis(String nomeSuspeito, int valorDados) {
        Piao piao = pioes.get(nomeSuspeito);
        if (piao == null || piao.getPosicaoAtual() == null) {
            return new ArrayList<>();
        }
        return tabuleiro.mapearCasasAlcancaveis(piao.getPosicaoAtual(), valorDados);
    }

    // Deslocamos o pião para a casa que for escolhida
    public boolean deslocarPiao(String nomeSuspeito, int xDestino, int yDestino) {
        Piao piao = pioes.get(nomeSuspeito);
        Casa destino = tabuleiro.getCasa(xDestino, yDestino);
        if (piao != null && destino != null) {
            return tabuleiro.moverPiao(piao, destino);
        }
        return false;
    }

    public void prepararPartida(int numJogadores) {
        // controle de erro caso descumpra a regra de número de jogadores
        if(numJogadores < 3 || numJogadores > 6) { // Throw é um botão de abortar caso de este erro
            throw new IllegalArgumentException("número de jogadores inválido");
        }

        // separa as cartas para sortear
        List<Carta> suspeitos = new ArrayList<>();
        List<Carta> armas = new ArrayList<>();
        List<Carta> comodos = new ArrayList<>();

        // separamos as cartas por tipo e as adicionamos a c
        for (Carta c : baralho.values()) {
            if (c.getTipo() == TipoCarta.SUSPEITO) suspeitos.add(c);
            else if (c.getTipo() == TipoCarta.ARMA) armas.add(c);
            else if (c.getTipo() == TipoCarta.COMODO) comodos.add(c);
        }

        // Embaralha tudo com o metodo shuffle
        Collections.shuffle(suspeitos);
        Collections.shuffle(armas);
        Collections.shuffle(comodos);

        // Tira a primeira carta de cada pilha e esconde no envelope de resposta
        envelopeConfidencial.definirSolucao(
                suspeitos.remove(0),
                armas.remove(0),
                comodos.remove(0)
        );

        // Junta o que sobrou, embaralha de novo e distribui para os jogadores
        List<Carta> cartasRestantes = new ArrayList<>();
        cartasRestantes.addAll(suspeitos);
        cartasRestantes.addAll(armas);
        cartasRestantes.addAll(comodos);
        Collections.shuffle(cartasRestantes);

        // Inicializa a mão de cada jogador no nosso HashMap.
        // Nós criamos um ArrayList vazio e associamos ao número do jogador
        for (int i = 1; i <= numJogadores; i++) {
            maosJogadores.put(i, new ArrayList<>());
        }

        // aqui distribuimos uma carta de cada vez para cada jogador até acabarem as cartas.
        int jogadorAtual = 1;
        for (Carta c : cartasRestantes) {
            maosJogadores.get(jogadorAtual).add(c);
            jogadorAtual++;
            if (jogadorAtual > numJogadores) {
                jogadorAtual = 1;
            }
        }
    }

    // Cria as cartas com os nomes originais do Clue, em seus respectivos tipos.
    private void inicializarCartas() {
        String[] nomesSuspeitos = {"Srta. Rose", "Coronel Mostarda", "Professor Plum", "Sr. Marinho", "Dona Violeta", "Dona Branca"};
        String[] nomesArmas = {"Corda", "Cano de Ferro", "Faca", "Chave Inglesa", "Castiçal", "Pistola"};
        String[] nomesComodos = {"Cozinha", "Salão de Festas", "Salão de Jogos", "Biblioteca", "Escritório", "Sala de Estar", "Sala de Jantar", "Terraço", "Hall"};

        // Pegamos os arrays com os nomes originais do Clue e, para cada nome, damos um new Carta.
        // Em seguida, usamos o metodo put para salvar essa carta no nosso HashMap do baralho
        for (String s : nomesSuspeitos) baralho.put(s, new Carta(s, TipoCarta.SUSPEITO));
        for (String a : nomesArmas) baralho.put(a, new Carta(a, TipoCarta.ARMA));
        for (String c : nomesComodos) baralho.put(c, new Carta(c, TipoCarta.COMODO));
    }

    // Cria os piões, coloca eles nas coordenadas iniciais corretas do tabuleiro.
    private void inicializarPioes() {
        String[] nomesSuspeitos = {"Srta. Rose", "Coronel Mostarda", "Professor Plum", "Sr. Marinho", "Dona Violeta", "Dona Branca"};
        int[][] posicoesIniciais = {{0,7}, {0,16}, {7,0}, {18,0}, {24,7}, {24,16}};

        // Cria cada pião, busca as coordenadas (X e Y) de largada na nossa matriz,
        // coloca a peça no tabuleiro e salva no HashMap para acessarmos rápido depois.
        for (int i = 0; i < nomesSuspeitos.length; i++) {
            Piao novoPiao = new Piao(nomesSuspeitos[i]);
            // pega pelo index cada piao e sua posicao, entao o suspeito[1] está na posicaoinicial[1]
            Casa casaInicial = tabuleiro.getCasa(posicoesIniciais[i][0], posicoesIniciais[i][1]);
            tabuleiro.moverPiao(novoPiao, casaInicial);
            pioes.put(nomesSuspeitos[i], novoPiao);
        }
    }

    // Retorna a lista de nomes dos suspeitos para a View conseguir iterar e desenhar
    public List<String> getNomesSuspeitos() {
        return new ArrayList<>(pioes.keySet());
    }

    // Retorna a posição (linha e coluna) de um pião específico para a View desenhar
    // Retorna um array onde o index 0 é a Linha (X) e index 1 é a Coluna (Y)
    public int[] getCoordenadasPiao(String nomeSuspeito) {
        Piao p = pioes.get(nomeSuspeito);
        if (p != null && p.getPosicaoAtual() != null) {
            Casa c = p.getPosicaoAtual();
            return new int[]{c.getX(), c.getY()};
        }
        return null; // Caso o peão não esteja no tabuleiro
    }

    // Getters restritos ao pacote para testes e checagens internas.
    Map<Integer, List<Carta>> getMaosJogadores() {
        return maosJogadores;
    }
    Envelope getEnvelopeConfidencial() {
        return envelopeConfidencial;
    }
    Piao getPiao(String nome) {
        return pioes.get(nome);
    }
}