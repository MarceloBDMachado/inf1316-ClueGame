package model;

import observer.Observado;

import java.util.*;

/**
 * Classe principal que gerencia a lógica de negócio do jogo.
 * Implementa o padrão Observer para atualizar a View automaticamente quando o estado do jogo muda.
 */
public class JogoClueInicio implements Observado {
    private Dado dado1;
    private Dado dado2;
    private Tabuleiro tabuleiro;

    private Map<String, Piao> pioes;
    private Map<String, Carta> baralho;
    private Map<Integer, List<Carta>> maosJogadores;
    private Envelope envelopeConfidencial;
    private final List<String> ordemJogadores = Arrays.asList("Srta. Rose", "Coronel Mostarda", "Professor Plum", "Sr. Marinho", "Dona Violeta", "Dona Branca");
    private int indiceTurnoAtual = 0;
    private List<String> jogadoresEliminados = new ArrayList<>();

    private List<Observador> observadores = new ArrayList<>();

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
    public void adicionarObservador(Observador o) {
        observadores.add(o);
    }

    @Override
    public void removerObservador(Observador o) {
        observadores.remove(o);
    }

    @Override
    public void notificarObservadores() {
        for (Observador o : observadores) {
            o.atualizar();
        }
    }

    public int[] rolarDados() {
        int[] resultado = new int[]{dado1.rolar(), dado2.rolar()};
        notificarObservadores();
        return resultado;
    }

    public void setValoresDados(int v1, int v2) {
        notificarObservadores();
    }

    public String getJogadorDaVez() {
        return ordemJogadores.get(indiceTurnoAtual);
    }

    public void passarTurno() {
        do {
            indiceTurnoAtual = (indiceTurnoAtual + 1) % ordemJogadores.size();
        } while (jogadoresEliminados.contains(ordemJogadores.get(indiceTurnoAtual)));
        notificarObservadores();
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
                boolean sucesso = tabuleiro.moverPiao(piao, destino);
                if (sucesso) {
                    notificarObservadores();
                }
                return sucesso;
            }
        }
        return false;
    }

    public void prepararPartida(int numJogadores) {
        if(numJogadores < 3 || numJogadores > 6) {
            throw new IllegalArgumentException("número de jogadores inválido");
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

        // Define a solução secreta do jogo retirando uma carta de cada tipo
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

        // Distribuição circular das cartas restantes entre os jogadores ativos
        int jogadorAtual = 1;
        for (Carta c : cartasRestantes) {
            maosJogadores.get(jogadorAtual).add(c);
            jogadorAtual++;
            if (jogadorAtual > numJogadores) {
                jogadorAtual = 1;
            }
        }
        notificarObservadores();
    }

    private void inicializarCartas() {
        String[] nomesSuspeitos = {"Srta. Rose", "Coronel Mostarda", "Professor Plum", "Sr. Marinho", "Dona Violeta", "Dona Branca"};
        String[] nomesArmas = {"Corda", "Cano de Ferro", "Faca", "Chave Inglesa", "Castiçal", "Pistola"};
        String[] nomesComodos = {"Cozinha", "Sala de Musica", "Salão de Jogos", "Biblioteca", "Escritório", "Sala de Estar", "Sala de Jantar", "Jardim de Inverno", "Entrada"};

        for (String s : nomesSuspeitos) baralho.put(s, new Carta(s, TipoCarta.SUSPEITO));
        for (String a : nomesArmas) baralho.put(a, new Carta(a, TipoCarta.ARMA));
        for (String c : nomesComodos) baralho.put(c, new Carta(c, TipoCarta.COMODO));
    }

    public List<String> getNomesSuspeitos() {
        List<String> lista = new ArrayList<>();
        for (Carta c : baralho.values()) {
            if (c.getTipo() == TipoCarta.SUSPEITO) lista.add(c.getNome());
        }
        return lista;
    }

    public List<String> getNomesArmas() {
        List<String> lista = new ArrayList<>();
        for (Carta c : baralho.values()) {
            if (c.getTipo() == TipoCarta.ARMA) lista.add(c.getNome());
        }
        return lista;
    }

    public List<String> getNomesComodos() {
        List<String> lista = new ArrayList<>();
        for (Carta c : baralho.values()) {
            if (c.getTipo() == TipoCarta.COMODO) lista.add(c.getNome());
        }
        return lista;
    }

    private void inicializarPioes() {
        String[] nomesSuspeitos = {"Srta. Rose", "Coronel Mostarda", "Professor Plum", "Sr. Marinho", "Dona Violeta", "Dona Branca"};

        // Definição das coordenadas iniciais de cada personagem no tabuleiro 25x24
        int[][] posicoesIniciais = {
                {24, 7},  // Srta. Rose
                {17, 0},  // Coronel Mostarda
                {19, 23}, // Professor Plum
                {0, 14},  // Sr. Marinho
                {6, 23},  // Dona Violeta
                {0, 9}    // Dona Branca
        };

        for (int i = 0; i < nomesSuspeitos.length; i++) {
            Piao novoPiao = new Piao(nomesSuspeitos[i]);
            Casa casaInicial = tabuleiro.getCasa(posicoesIniciais[i][0], posicoesIniciais[i][1]);
            tabuleiro.moverPiao(novoPiao, casaInicial);
            pioes.put(nomesSuspeitos[i], novoPiao);
        }
    }

    public int[] getCoordenadasPiao(String nomeSuspeito) {
        Piao p = pioes.get(nomeSuspeito);
        if (p != null && p.getPosicaoAtual() != null) {
            Casa c = p.getPosicaoAtual();
            return new int[]{c.getX(), c.getY()};
        }
        return null;
    }

    Map<Integer, List<Carta>> getMaosJogadores() {
        return maosJogadores;
    }
    Envelope getEnvelopeConfidencial() {
        return envelopeConfidencial;
    }
    Piao getPiao(String nome) {
        return pioes.get(nome);
    }

    public boolean moverPorPassagemSecreta(String nomeJogador) {
        Piao piao = pioes.get(nomeJogador);
        if (piao != null) {
            return tabuleiro.moverPorPassagemSecreta(piao);
        }
        return false;
    }

    public List<String[]> obterDadosCartasDoJogadorAtual() {
        List<Carta> cartasDoJogador = buscarCartasDoJogadorAtualInterno();
        List<String[]> dadosDasCartas = new ArrayList<>();

        if (cartasDoJogador != null) {
            for (Carta c : cartasDoJogador) {
                dadosDasCartas.add(new String[]{ c.getNome(), c.getTipo().toString() });
            }
        }
        return dadosDasCartas;
    }

    private List<Carta> buscarCartasDoJogadorAtualInterno() {
        if (maosJogadores == null || maosJogadores.isEmpty()) {
            return new ArrayList<>();
        }

        int idJogadorAtual = (indiceTurnoAtual % maosJogadores.size()) + 1;

        return maosJogadores.get(idJogadorAtual);
    }

    public String[] realizarPalpite(String nomeAcusador, String suspeito, String arma, String comodo) {

        // Regra do jogo: ao dar um palpite, o peão do suspeito sugerido deve ser movido para o mesmo local do acusador
        Piao piaoSuspeito = pioes.get(suspeito);
        Piao piaoAcusador = pioes.get(nomeAcusador);
        if (piaoSuspeito != null && piaoAcusador != null && piaoAcusador.getPosicaoAtual() != null) {
            tabuleiro.moverPiao(piaoSuspeito, piaoAcusador.getPosicaoAtual());
            notificarObservadores();
        }

        // Verifica na mão dos oponentes, começando pelo próximo da rodada
        int numJogadoresJogando = maosJogadores.size();
        int idAcusador = (indiceTurnoAtual % numJogadoresJogando) + 1;

        for (int i = 1; i < numJogadoresJogando; i++) {
            int idInspecionado = ((idAcusador - 1 + i) % numJogadoresJogando) + 1;

            List<Carta> mao = maosJogadores.get(idInspecionado);
            if (mao != null) {
                for (Carta c : mao) {
                    if (c.getNome().equals(suspeito) ||
                            c.getNome().equals(arma) ||
                            c.getNome().equals(comodo)) {

                        String nomeJogadorQueMostrou = ordemJogadores.get(idInspecionado - 1);
                        return new String[]{ c.getNome(), c.getTipo().toString(), nomeJogadorQueMostrou };
                    }
                }
            }
        }
        return null;
    }

    public boolean realizarAcusacaoFinal(String nomeAcusador, String suspeito, String arma, String comodo) {
        boolean acertou = envelopeConfidencial.verificarSolucao(suspeito, arma, comodo);

        if (acertou) {
            return true;
        } else {
            // Caso erre a acusação, o jogador é eliminado da partida
            jogadoresEliminados.add(nomeAcusador);
            passarTurno();
            return false;
        }
    }

    // MÉTODOS DE SALVAMENTO E RECUPERAÇÃO ROBUSTOS (ITERACÃO 4)
    public void salvarEstado(java.io.File arquivo) {
        try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(arquivo))) {
            out.println("TurnoAtual:" + indiceTurnoAtual);

            // Salvar posição dos peões
            for (Map.Entry<String, Piao> entry : pioes.entrySet()) {
                Casa pos = entry.getValue().getPosicaoAtual();
                if(pos != null) {
                    out.println("Piao:" + entry.getKey() + "," + pos.getX() + "," + pos.getY());
                }
            }

            // Salvar Cartas nas Mãos dos Jogadores
            for (Map.Entry<Integer, List<Carta>> entry : maosJogadores.entrySet()) {
                out.print("Mao:" + entry.getKey());
                for (Carta c : entry.getValue()) {
                    out.print("," + c.getNome());
                }
                out.println();
            }

            // Salvar os jogadores que já foram eliminados (A Grande Sacada!)
            for (String eliminado : jogadoresEliminados) {
                out.println("Eliminado:" + eliminado);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void carregarEstado(java.io.File arquivo) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(arquivo))) {
            String linha;
            maosJogadores.clear(); // Limpa as mãos antigas antes de carregar
            jogadoresEliminados.clear(); // Limpa o cemitério de jogadores antes de carregar

            while ((linha = br.readLine()) != null) {
                if (linha.startsWith("TurnoAtual:")) {
                    this.indiceTurnoAtual = Integer.parseInt(linha.split(":")[1]);
                }
                else if (linha.startsWith("Piao:")) {
                    String[] partes = linha.substring(5).split(",");
                    String nome = partes[0];
                    int x = Integer.parseInt(partes[1]);
                    int y = Integer.parseInt(partes[2]);

                    Piao p = pioes.get(nome);
                    Casa destino = tabuleiro.getCasa(x, y);
                    if(p != null && destino != null) {
                        tabuleiro.moverPiao(p, destino);
                    }
                }
                else if (linha.startsWith("Mao:")) {
                    String[] partes = linha.substring(4).split(",");
                    int idJogador = Integer.parseInt(partes[0]);
                    List<Carta> maoRecuperada = new ArrayList<>();

                    for (int i = 1; i < partes.length; i++) {
                        Carta c = baralho.get(partes[i]);
                        if (c != null) maoRecuperada.add(c);
                    }
                    maosJogadores.put(idJogador, maoRecuperada);
                }
                else if (linha.startsWith("Eliminado:")) {
                    // Restaura o jogador banido de volta para a lista de eliminados
                    jogadoresEliminados.add(linha.substring(10));
                }
            }
            notificarObservadores();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Retorna o nome do cômodo onde o jogador está, ou null se estiver no corredor
    public String getComodoAtualJogador() {
        Piao p = pioes.get(getJogadorDaVez());
        if (p != null && p.getPosicaoAtual() != null &&
                (p.getPosicaoAtual().getTipo() == TipoCasa.COMODO || p.getPosicaoAtual().getTipo() == TipoCasa.PORTA)) {
            return p.getPosicaoAtual().getNomeComodo();
        }
        return null;
    }
}