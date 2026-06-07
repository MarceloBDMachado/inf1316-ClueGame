package controller;

import model.JogoClueInicio;
import observer.Observador;

public class ControllerClue {
    // 1. Padrão Singleton: Instância única estática
    private static ControllerClue instancia;

    // O Controller conhece o Model (Façade)
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

    // Método para inicializar os dados da partida
    public void iniciarPartida(int numJogadores) {
        jogoFacade.prepararPartida(numJogadores);
    }

    // Delegação de responsabilidades (O Controller pede para a Façade agir)
    public void rolarDados(int d1, int d2) {
        jogoFacade.setValoresDados(d1, d2);
    }

    public boolean moverPiao(String jogador, int linha, int coluna, int passos) {
        boolean sucesso = jogoFacade.deslocarPiao(jogador, linha, coluna, passos);
        if (sucesso) {
            jogoFacade.passarTurno(); // Passa a vez se o movimento foi válido
        }
        return sucesso;
    }

    // ========================================================
    // MÉTODOS ADICIONADOS PARA A 3ª ITERAÇÃO
    // ========================================================

    // Método para rolar os dados aleatoriamente (Mecânica de Sorte do Jogo Real)
    public int[] rolarDadosAleatorios() {
        return jogoFacade.rolarDados();
    }

    // Método para acionar a passagem secreta do jogador da vez
    public boolean usarPassagemSecreta(String jogador) {
        // Correção: Passamos apenas a String com o nome do jogador para a fachada
        boolean sucesso = jogoFacade.moverPorPassagemSecreta(jogador);
        if (sucesso) {
            // Se o teletransporte deu certo, força o turno a passar
            jogoFacade.passarTurno();
        }
        return sucesso;
    }
    // Retorna a Façade para a View apenas pegar informações (getters)
    public JogoClueInicio getModel() {
        return jogoFacade;
    }
}