package model;

import java.util.*;

public class JogoClueFacade {
    private Dado dado1;
    private Dado dado2;
    private Tabuleiro tabuleiro; // Integrando o Tabuleiro

    // Atualizado de Object para Piao e mantendo o uso de HashMap exigido
    private Map<String, Piao> pioes;
    private Map<String, Carta> baralho;
    private Map<Integer, List<Carta>> maosJogadores;
    private Envelope envelopeConfidencial;

    public JogoClueFacade() {
        this.dado1 = new Dado();
        this.dado2 = new Dado();
        this.tabuleiro = new Tabuleiro();
        this.pioes = new HashMap<>();
        this.baralho = new HashMap<>();
        this.maosJogadores = new HashMap<>();
        this.envelopeConfidencial = new Envelope();

        inicializarCartas();
        inicializarPioes(); // Novo método para colocar os personagens no mapa
    }

    /**
     * Requisito 2: Lançamento virtual dos dados
     */
    public int[] rolarDados() {
        return new int[]{dado1.rolar(), dado2.rolar()};
    }

    /**
     * Requisito 3: Mapear todas as casas que podem ser alcançadas com o valor dos dados.
     * @return Uma lista de objetos Casa válidos para o movimento.
     */
    public List<Casa> mapearCasasPossiveis(String nomeSuspeito, int valorDados) {
        Piao piao = pioes.get(nomeSuspeito);
        if (piao == null || piao.getPosicaoAtual() == null) {
            return new ArrayList<>();
        }
        return tabuleiro.mapearCasasAlcancaveis(piao.getPosicaoAtual(), valorDados);
    }

    /**
     * Requisito 4: Executa o deslocamento do pião para a coordenada escolhida.
     */
    public boolean deslocarPiao(String nomeSuspeito, int xDestino, int yDestino) {
        Piao piao = pioes.get(nomeSuspeito);
        Casa destino = tabuleiro.getCasa(xDestino, yDestino);

        if (piao != null && destino != null) {
            return tabuleiro.moverPiao(piao, destino);
        }
        return false;
    }

    /**
     * Requisito 1: Preparação do jogo (Itens 4, 5, 6 e 7 do manual)
     */
    public void prepararPartida(int numJogadores) {
        if(numJogadores < 3 || numJogadores > 6) {
            throw new IllegalArgumentException("O jogo deve ter entre 3 e 6 jogadores.");
        }

        List<Carta> suspeitos = new ArrayList<>();
        List<Carta> armas = new ArrayList<>();
        List<Carta> comodos = new ArrayList<>();

        for (Carta c : baralho.values()) {
            if (c.getTipo() == TipoCarta.SUSPEITO) suspeitos.add(c);
            else if (c.getTipo() == TipoCarta.ARMA) armas.add(c);
            else if (c.getTipo() == TipoCarta.COMODO) comodos.add(c);
        }

        Collections.shuffle(suspeitos);
        Collections.shuffle(armas);
        Collections.shuffle(comodos);

        envelopeConfidencial.definirSolucao(
                suspeitos.remove(0),
                armas.remove(0),
                comodos.remove(0)
        );

        List<Carta> cartasRestantes = new ArrayList<>();
        cartasRestantes.addAll(suspeitos);
        cartasRestantes.addAll(armas);
        cartasRestantes.addAll(comodos);

        Collections.shuffle(cartasRestantes);

        for (int i = 1; i <= numJogadores; i++) {
            maosJogadores.put(i, new ArrayList<>());
        }

        int jogadorAtual = 1;
        for (Carta c : cartasRestantes) {
            maosJogadores.get(jogadorAtual).add(c);
            jogadorAtual++;
            if (jogadorAtual > numJogadores) {
                jogadorAtual = 1;
            }
        }
    }

    /**
     * Cadastra todas as cartas do jogo no HashMap interno usando as traduções do colega.
     */
    private void inicializarCartas() {
        String[] nomesSuspeitos = {"Srta. Rose", "Coronel Mostarda", "Professor Plum", "Sr. Marinho", "Dona Violeta", "Dona Branca"};
        String[] nomesArmas = {"Corda", "Cano de Ferro", "Faca", "Chave Inglesa", "Castiçal", "Pistola"};
        String[] nomesComodos = {"Cozinha", "Salão de Festas", "Salão de Jogos", "Biblioteca", "Escritório", "Sala de Estar", "Sala de Jantar", "Terraço", "Hall"};

        for (String s : nomesSuspeitos) baralho.put(s, new Carta(s, TipoCarta.SUSPEITO));
        for (String a : nomesArmas) baralho.put(a, new Carta(a, TipoCarta.ARMA));
        for (String c : nomesComodos) baralho.put(c, new Carta(c, TipoCarta.COMODO));
    }

    /**
     * Posiciona os piões em coordenadas iniciais no tabuleiro para os testes da 1ª iteração.
     */
    private void inicializarPioes() {
        // Atualizado para bater com as cartas do colega
        String[] nomesSuspeitos = {"Srta. Rose", "Coronel Mostarda", "Professor Plum", "Sr. Marinho", "Dona Violeta", "Dona Branca"};
        // Coordenadas iniciais fictícias para teste (baseadas em um tabuleiro 25x24)
        int[][] posicoesIniciais = {{0,7}, {0,16}, {7,0}, {18,0}, {24,7}, {24,16}};

        for (int i = 0; i < nomesSuspeitos.length; i++) {
            Piao novoPiao = new Piao(nomesSuspeitos[i]);
            Casa casaInicial = tabuleiro.getCasa(posicoesIniciais[i][0], posicoesIniciais[i][1]);

            tabuleiro.moverPiao(novoPiao, casaInicial);
            pioes.put(nomesSuspeitos[i], novoPiao);
        }
    }

    // --- MÉTODOS DE ACESSO PARA TESTES (Package-Private) ---
    
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