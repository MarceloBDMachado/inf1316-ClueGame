package controller;

import model.JogoClueInicio;
import observer.Observador;
import java.util.List;

public class ControllerClue {
    // 1. Padrão Singleton: Instância única estática
    private static ControllerClue instancia;
    private JogoClueInicio jogoFacade;

    // 2. Padrão Singleton: Construtor privado (ninguém pode dar 'new' de fora)
    private ControllerClue() {
        this.jogoFacade = new JogoClueInicio();
    }

    // 3. Padrão Singleton: Método global para pegar a única instância
    public static ControllerClue getInstancia() {
        if (instancia == null) {
            instancia = new ControllerClue();
        }
        return instancia;
    }

    // Método para a View se registrar como ouvinte do Model
    public void registrarObservador(Observador o) {
        jogoFacade.adicionarObservador(o);
    }

    // NOVO: Método atualizado para receber os personagensEscolhidos repassados pela Janela
    public void iniciarPartida(int numJogadores, List<String> personagensSelecionados) {
        jogoFacade.prepararPartida(numJogadores, personagensSelecionados);
    }

    // Delegação de responsabilidades (O Controller pede para a Façade agir)
    public void rolarDados(int d1, int d2) {
        jogoFacade.setValoresDados(d1, d2);
    }

    public boolean moverPiao(String jogador, int linha, int coluna, int passos) {
        // Agora o Controller apenas move o pião, mas NÃO rouba o turno do jogador!
        return jogoFacade.deslocarPiao(jogador, linha, coluna, passos);
    }

    // Metodo para rolar os dados aleatoriamente
    public int[] rolarDadosAleatorios() {
        return jogoFacade.rolarDados();
    }

    // Metodo para acionar a passagem secreta do jogador da vez
    public boolean usarPassagemSecreta(String jogador) {
        // Move pela passagem e aguarda a ação do jogador (Palpite ou Passar a Vez)
        return jogoFacade.moverPorPassagemSecreta(jogador);
    }

    // Metodo para acessar o encerramento do turno do jogador
    public void encerrarTurno() {
        jogoFacade.passarTurno();
    }


    // O Controller pede à Fachada as cartas já formatadas como texto
    public List<String[]> obterDadosCartasDoJogadorAtual() {
        // Chamando o nome correto do metodo que está na fachada do Model
        return jogoFacade.obterDadosCartasDoJogadorAtual();
    }

    // Retorna a Façade para a View apenas pegar informações (getters)
    public JogoClueInicio getModel() {
        return jogoFacade;
    }

    // NOVO: Métodos criados para intermediar as anotações do bloco de notas entre a View e o Model
    public void marcarNota(String jogador, String item, boolean marcado) {
        jogoFacade.marcarNota(jogador, item, marcado);
    }

    public boolean isNotaMarcada(String jogador, String item) {
        return jogoFacade.isNotaMarcada(jogador, item);
    }

    // Recebe o palpite da View e envia para o Model
    public String[] fazerPalpite(String suspeito, String arma, String comodo) {
        String jogadorAtual = jogoFacade.getJogadorDaVez();
        return jogoFacade.realizarPalpite(jogadorAtual, suspeito, arma, comodo);
    }

    // Recebe a acusação da View e envia para o Model
    public boolean fazerAcusacao(String suspeito, String arma, String comodo) {
        String jogadorAtual = jogoFacade.getJogadorDaVez();
        return jogoFacade.realizarAcusacaoFinal(jogadorAtual, suspeito, arma, comodo);
    }

    public void salvarPartida(java.io.File arquivo) {
        jogoFacade.salvarEstado(arquivo);
    }

    public void carregarPartida(java.io.File arquivo) {
        jogoFacade.carregarEstado(arquivo);
    }

    public static void resetarJogo() {
        instancia = new ControllerClue(); // Recria o Singleton do zero
    }

    // Faz a ponte para a View saber em qual cômodo o jogador está
    public String getComodoAtualJogador() {
        return jogoFacade.getComodoAtualJogador();
    }
}
