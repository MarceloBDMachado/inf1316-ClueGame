package model;

import observer.Observado;
import observer.Observador;

import java.util.*;
import java.io.*;

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

    // NOVO: Removido o 'final' e a lista estática (Arrays.asList gera uma lista fixa que daria erro no .clear()), agora é preenchida dinamicamente
    private List<String> ordemJogadores = new ArrayList<>();

    private int indiceTurnoAtual = 0;
    private List<String> jogadoresEliminados = new ArrayList<>();

    private List<Observador> observadores = new ArrayList<>();

    // NOVO: Mapa para controlar a regra de restrição de palpites consecutivos no mesmo cômodo
    private Map<String, Boolean> jaPalpitouNoComodoAtual = new HashMap<>();

    // NOVO: Estrutura de dados para armazenar de forma persistente as anotações manuais do bloco de notas de cada personagem
    private Map<String, Set<String>> anotacoesJogadores = new HashMap<>();

    public JogoClueInicio() {
        this.dado1 = new Dado();
        this.dado2 = new Dado();
        this.tabuleiro = new Tabuleiro();
        this.pioes = new HashMap<>();
        this.baralho = new HashMap<>();
        this.maosJogadores = new HashMap<>();
        this.envelopeConfidencial = new Envelope();

        inicializarCartas();
        // NOVO: A inicialização fixa foi comentada/removida daqui, para iniciar somente quem foi selecionado
        // inicializarPioes();
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
        // NOVO: Tratamento para evitar NullPointerException caso a lista esteja vazia ao iniciar
        if (ordemJogadores.isEmpty()) return "Nenhum";
        return ordemJogadores.get(indiceTurnoAtual);
    }

    public void passarTurno() {
        // NOVO: Proteção extra
        if (ordemJogadores.isEmpty()) return;
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
                    // NOVO: Se o movimento foi bem-sucedido, limpa a flag de palpite feito para permitir palpite no novo cômodo
                    jaPalpitouNoComodoAtual.put(nomeSuspeito, false);
                    notificarObservadores();
                }
                return sucesso;
            }
        }
        return false;
    }

    // NOVO: Verifica se o jogador está preso em um cômodo com as portas bloqueadas pelos oponentes
    public boolean isJogadorPreso(String nomeJogador) {
        Piao p = pioes.get(nomeJogador);
        if (p == null || p.getPosicaoAtual() == null) return false;
        Casa origem = p.getPosicaoAtual();

        if (origem.getTipo() == TipoCasa.COMODO || origem.getTipo() == TipoCasa.PORTA) {
            // Usa o próprio algoritmo do tabuleiro para tentar dar 1 passo para fora do quarto
            List<Casa> alcancaveis = tabuleiro.mapearCasasAlcancaveis(origem, 1);
            for (Casa c : alcancaveis) {
                if (c.getTipo() == TipoCasa.CORREDOR) {
                    return false; // Consegue acessar o corredor livremente, não está preso
                }
            }
            return true; // Preso! Nenhuma saída para o corredor adjacente está livre
        }
        return false; // Se estiver no corredor, não se aplica a regra de "preso numa sala"
    }

    // NOVO: Adicionado parametro para a lista de personagens selecionados e injetando neles
    public void prepararPartida(int numJogadores, List<String> personagensSelecionados) {
        if(numJogadores < 3 || numJogadores > 6) {
            throw new IllegalArgumentException("número de jogadores inválido");
        }

        // NOVO: Alimenta a ordem de turnos do jogo com base na escolha da interface gráfica
        this.ordemJogadores.clear();

        // NOVO: Força a ordenação oficial (Scarlet, Mustard, White, Green, Peacock, Plum) independentemente de como vieram da interface
        List<String> ordemOficial = Arrays.asList("Srta. Rose", "Coronel Mostarda", "Dona Branca", "Sr. Marinho", "Dona Violeta", "Professor Plum");
        for (String personagem : ordemOficial) {
            if (personagensSelecionados.contains(personagem)) {
                this.ordemJogadores.add(personagem);
            }
        }

        // NOVO: Inicializa o mapa de restrição de palpites consecutivos para todos os personagens oficiais
        jaPalpitouNoComodoAtual.clear();
        for (String personagem : ordemOficial) {
            jaPalpitouNoComodoAtual.put(personagem, false);
        }

        // NOVO: Inicializa os conjuntos de anotações vazios para cada um dos personagens oficiais
        anotacoesJogadores.clear();
        for (String personagem : ordemOficial) {
            anotacoesJogadores.put(personagem, new HashSet<String>());
        }

        inicializarPioes(personagensSelecionados);

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

    // NOVO: Metodo inicializarPioes foi atualizado para receber uma lista e gerar os peões apenas dos escolhidos
    // NOVO: Modificado para sempre carregar todos os 6 piões no tabuleiro, permitindo que NPCs sejam teletransportados por palpites
    private void inicializarPioes(List<String> escolhidos) {
        pioes.clear(); // Limpa as existencias antes de setar novas
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
            // NOVO: Removemos a condicional de filtro para instanciar todos os 6 piões fisicamente na grade do mapa
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
            boolean viajou = tabuleiro.moverPorPassagemSecreta(piao);
            // NOVO: Se usou a passagem, limpa o bloqueio de palpite consecutivo para o novo aposento
            if (viajou) {
                jaPalpitouNoComodoAtual.put(nomeJogador, false);
            }
            return viajou;
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

    // NOVO: Métodos adicionados para gerenciar e persistir as anotações manuais dos blocos de notas
    public void marcarNota(String jogador, String item, boolean marcado) {
        if (!anotacoesJogadores.containsKey(jogador)) {
            anotacoesJogadores.put(jogador, new HashSet<String>());
        }
        if (marcado) {
            anotacoesJogadores.get(jogador).add(item);
        } else {
            anotacoesJogadores.get(jogador).remove(item);
        }
    }

    public boolean isNotaMarcada(String jogador, String item) {
        if (!anotacoesJogadores.containsKey(jogador)) {
            return false;
        }
        return anotacoesJogadores.get(jogador).contains(item);
    }

    public String[] realizarPalpite(String nomeAcusador, String suspeito, String arma, String comodo) {
        // NOVO: Validação da regra que proíbe palpites consecutivos no mesmo cômodo sem deslocamento prévio
        if (jaPalpitouNoComodoAtual.getOrDefault(nomeAcusador, false)) {
            return new String[]{"ERRO_CONSECUTIVO", "", ""};
        }

        // Regra do jogo: ao dar um palpite, o peão do suspeito sugerido deve ser movido para o mesmo local do acusador
        Piao piaoSuspeito = pioes.get(suspeito);
        Piao piaoAcusador = pioes.get(nomeAcusador);

        // NOVO: Adicionada verificação extra de integridade, apenas move o suspeito se o peão dele fisicamente existe no mapa
        if (piaoSuspeito != null && piaoAcusador != null && piaoAcusador.getPosicaoAtual() != null) {
            tabuleiro.moverPiao(piaoSuspeito, piaoAcusador.getPosicaoAtual());
            // NOVO: Caso o suspeito movido seja um jogador ativo arrastado por uma sugestão, ele ganha o direito de palpitar ali no seu turno
            jaPalpitouNoComodoAtual.put(suspeito, false);
            notificarObservadores();
        }

        // NOVO: Define a trava de palpite efetuado para o acusador atual neste cômodo
        jaPalpitouNoComodoAtual.put(nomeAcusador, true);

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

            // NOVO: Como a ordem de jogadores agora é dinâmica, é estritamente necessário salvar ela para carregar depois
            out.print("Ordem:");
            for (int i = 0; i < ordemJogadores.size(); i++) {
                out.print(ordemJogadores.get(i) + (i < ordemJogadores.size() - 1 ? "," : ""));
            }
            out.println();

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

            // NOVO: Gravação de cada item marcado no bloco de notas dos detetives no arquivo TXT
            for (Map.Entry<String, Set<String>> entry : anotacoesJogadores.entrySet()) {
                if (entry.getValue().size() > 0) {
                    out.print("Anotacao:" + entry.getKey());
                    for (String marcado : entry.getValue()) {
                        out.print("," + marcado);
                    }
                    out.println();
                }
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

            // NOVO: O Limpador de peões precisou ser adicionado para não duplicar objetos visuais
            pioes.clear();

            // NOVO: Limpa as anotações da memória antes de restaurar o save para evitar sobreposição
            anotacoesJogadores.clear();

            while ((linha = br.readLine()) != null) {
                if (linha.startsWith("TurnoAtual:")) {
                    this.indiceTurnoAtual = Integer.parseInt(linha.split(":")[1]);
                }
                // NOVO: Linha de recuperação e ativação dos personagens específicos do Save
                else if (linha.startsWith("Ordem:")) {
                    String[] partes = linha.substring(6).split(",");
                    ordemJogadores.clear();
                    for (String p : partes) {
                        if (!p.isEmpty()) ordemJogadores.add(p);
                    }
                    inicializarPioes(ordemJogadores); // Coloca eles no tabuleiro
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
                // NOVO: Reconstrói o bloco de notas individual de cada jogador a partir das linhas lidas do TXT
                else if (linha.startsWith("Anotacao:")) {
                    String[] partes = linha.substring(9).split(",");
                    String jogador = partes[0];
                    Set<String> marcacoes = new HashSet<>();
                    for (int i = 1; i < partes.length; i++) {
                        if (!partes[i].isEmpty()) marcacoes.add(partes[i]);
                    }
                    anotacoesJogadores.put(jogador, marcacoes);
                }
            }

            // NOVO: Estrutura de Fallback para caso abra um save game MUITO antigo, que foi salvo sem as variaveis de ordem
            if (ordemJogadores.isEmpty()) {
                // NOVO: Estrutura de Fallback atualizada para la ordem oficial
                ordemJogadores.addAll(Arrays.asList("Srta. Rose", "Coronel Mostarda", "Dona Branca", "Sr. Marinho", "Dona Violeta", "Professor Plum"));
                inicializarPioes(ordemJogadores);
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