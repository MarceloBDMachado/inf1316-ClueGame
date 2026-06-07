package model;

import java.util.*;

public class JogoClueInicio implements ObservadoIF {
    private Dado dado1;
    private Dado dado2;
    private Tabuleiro tabuleiro;

    private Map<String, Piao> pioes;
    private Map<String, Carta> baralho;
    private Map<Integer, List<Carta>> maosJogadores;
    private Envelope envelopeConfidencial;
    private final List<String> ordemJogadores = Arrays.asList("Srta. Rose", "Coronel Mostarda", "Professor Plum", "Sr. Marinho", "Dona Violeta", "Dona Branca");
    private int indiceTurnoAtual = 0;

    private List<ObservadorIF> observadores = new ArrayList<>();
    private int passosDisponiveis = 0;
    private int valorDado1 = 1;
    private int valorDado2 = 1;

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

    @Override
    public void add(ObservadorIF o) {
        if (!observadores.contains(o)) {
            observadores.add(o);
        }
    }

    @Override
    public void remove(ObservadorIF o) {
        observadores.remove(o);
    }

    @Override
    public void notificarObservadores() {
        for (ObservadorIF o : observadores) {
            o.notify(this);
        }
    }

    public int[] rolarDados() {
        return new int[]{dado1.rolar(), dado2.rolar()};
    }

    public String getJogadorDaVez() {
        return ordemJogadores.get(indiceTurnoAtual);
    }

    public int getIndiceJogadorDaVez() {
        return indiceTurnoAtual + 1;
    }

    public void passarTurno() {
        indiceTurnoAtual = (indiceTurnoAtual + 1) % ordemJogadores.size();
    }

    public List<Casa> mapearCasasPossiveis(String nomeSuspeito, int valorDados) {
        Piao piao = pioes.get(nomeSuspeito);
        if (piao == null || piao.getPosicaoAtual() == null) {
            return new ArrayList<>();
        }
        return tabuleiro.mapearCasasAlcancaveis(piao.getPosicaoAtual(), valorDados);
    }

    public boolean deslocarPiao(String nomeSuspeito, int xDestino, int yDestino, int passos) {
        Piao piao = pioes.get(nomeSuspeito);
        Casa destino = tabuleiro.getCasa(xDestino, yDestino);

        if (piao != null && destino != null) {
            List<Casa> casasPermitidas = mapearCasasPossiveis(nomeSuspeito, passos);
            if (casasPermitidas.contains(destino)) {
                return tabuleiro.moverPiao(piao, destino);
            }
        }
        return false;
    }

    public void processarRolagem(boolean manual, int val1, int val2) {
        if (manual) {
            valorDado1 = val1;
            valorDado2 = val2;
        } else {
            int[] rolados = rolarDados();
            valorDado1 = rolados[0];
            valorDado2 = rolados[1];
        }
        passosDisponiveis = valorDado1 + valorDado2;
        notificarObservadores();
    }

    public void tentarMover(int xDestino, int yDestino) {
        if (passosDisponiveis <= 0) return;

        String jogadorAtual = getJogadorDaVez();
        boolean moveu = deslocarPiao(jogadorAtual, xDestino, yDestino, passosDisponiveis);

        if (moveu) {
            passosDisponiveis = 0;
            passarTurno();
            notificarObservadores();
        }
    }
    // NOVO: Verifica se o cômodo atual tem passagem secreta e retorna o destino
    public Casa obterDestinoPassagemSecreta(String nomeJogador) {
        Piao piao = pioes.get(nomeJogador);
        if (piao == null || piao.getPosicaoAtual() == null) return null;

        Casa atual = piao.getPosicaoAtual();
        if (atual.getTipo() == TipoCasa.COMODO) {
            String nome = atual.getNomeComodo();
            if ("Cozinha".equals(nome)) return tabuleiro.encontrarCasaLivre("Escritório");
            if ("Escritório".equals(nome)) return tabuleiro.encontrarCasaLivre("Cozinha");
            if ("Terraço".equals(nome)) return tabuleiro.encontrarCasaLivre("Sala de Estar");
            if ("Sala de Estar".equals(nome)) return tabuleiro.encontrarCasaLivre("Terraço");
        }
        return null;
    }

    // NOVO: Executa o teletransporte e finaliza o turno do movimento
    public boolean usarPassagemSecreta() {
        String jogador = getJogadorDaVez();
        Casa destino = obterDestinoPassagemSecreta(jogador);

        if (destino != null) {
            Piao p = pioes.get(jogador);
            boolean moveu = tabuleiro.moverPiao(p, destino);
            if (moveu) {
                passosDisponiveis = 0; // Gastou a ação de movimento
                passarTurno(); // Passa o turno
                notificarObservadores();
                return true;
            }
        }
        return false;
    }

    public List<Carta> getCartasDoJogador(int idJogador) {
        List<Carta> cartas = maosJogadores.get(idJogador);
        if (cartas == null) {
            return new ArrayList<>();
        }
        return cartas;
    }

    public void prepararPartida(int numJogadores) {
        if(numJogadores < 3 || numJogadores > 6) {
            throw new IllegalArgumentException("Número de jogadores inválido");
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

        for (int i = 1; i <= 6; i++) {
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

    private void inicializarCartas() {
        String[] nomesSuspeitos = {"Srta. Rose", "Coronel Mostarda", "Professor Plum", "Sr. Marinho", "Dona Violeta", "Dona Branca"};
        String[] nomesArmas = {"Corda", "Cano de Ferro", "Faca", "Chave Inglesa", "Castiçal", "Pistola"};
        String[] nomesComodos = {"Cozinha", "Salão de Festas", "Salão de Jogos", "Biblioteca", "Escritório", "Sala de Estar", "Sala de Jantar", "Terraço", "Hall"};

        for (String s : nomesSuspeitos) baralho.put(s, new Carta(s, TipoCarta.SUSPEITO));
        for (String a : nomesArmas) baralho.put(a, new Carta(a, TipoCarta.ARMA));
        for (String c : nomesComodos) baralho.put(c, new Carta(c, TipoCarta.COMODO));
    }

    private void inicializarPioes() {
        String[] nomesSuspeitos = {"Srta. Rose", "Coronel Mostarda", "Professor Plum", "Sr. Marinho", "Dona Violeta", "Dona Branca"};

        int[][] posicoesIniciais = {
                {24, 7},
                {17, 0},
                {19, 23},
                {0, 14},
                {6, 23},
                {0, 9}
        };

        for (int i = 0; i < nomesSuspeitos.length; i++) {
            Piao novoPiao = new Piao(nomesSuspeitos[i]);
            Casa casaInicial = tabuleiro.getCasa(posicoesIniciais[i][0], posicoesIniciais[i][1]);
            tabuleiro.moverPiao(novoPiao, casaInicial);
            pioes.put(nomesSuspeitos[i], novoPiao);
        }
    }

    public List<String> getNomesSuspeitos() {
        return new ArrayList<>(pioes.keySet());
    }

    public int[] getCoordenadasPiao(String nomeSuspeito) {
        Piao p = pioes.get(nomeSuspeito);
        if (p != null && p.getPosicaoAtual() != null) {
            Casa c = p.getPosicaoAtual();
            return new int[]{c.getX(), c.getY()};
        }
        return null;
    }

    public int getValorDado1() { return valorDado1; }
    public int getValorDado2() { return valorDado2; }
    public int getPassosDisponiveis() { return passosDisponiveis; }
}