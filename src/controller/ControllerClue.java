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

    public void registrarObservador(Observador o) {
        jogoFacade.adicionarObservador(o);
    }

    public void iniciarPartida(int numJogadores, List<String> personagensSelecionados) {
        jogoFacade.prepararPartida(numJogadores, personagensSelecionados);
    }

    public void rolarDados(int d1, int d2) {
        jogoFacade.setValoresDados(d1, d2);
    }

    public boolean moverPiao(String jogador, int linha, int coluna, int passos) {
        return jogoFacade.deslocarPiao(jogador, linha, coluna, passos);
    }

    public int[] rolarDadosAleatorios() {
        return jogoFacade.rolarDados();
    }

    public boolean usarPassagemSecreta(String jogador) {
        // Move pela passagem e aguarda a ação do jogador (Palpite ou Passar a Vez)
        return jogoFacade.moverPorPassagemSecreta(jogador);
    }

    // passa a verificação se o jogador está preso do Model para a View
    public boolean isJogadorPreso(String jogador) {
        return jogoFacade.jogadorPreso(jogador);
    }

    public void encerrarTurno() {
        jogoFacade.passarTurno();
    }


    // O Controller pede as cartas já formatadas como texto
    public List<String[]> obterDadosCartasDoJogadorAtual() {
        return jogoFacade.obterDadosCartasDoJogadorAtual();
    }

    // Retorna a Façade para a View apenas pegar informações (getters)
    public JogoClueInicio getModel() {
        return jogoFacade;
    }

    // intermedia as anotações do bloco de notas entre a View e o Model
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

    // Recria o Singleton do zero
    public static void resetarJogo() {
        instancia = new ControllerClue(); 
    }

    // Faz a ponte para a View saber em qual cômodo o jogador está
    public String getComodoAtualJogador() {
        return jogoFacade.getComodoAtualJogador();
    }
}
