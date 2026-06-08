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

    // Método para rolar os dados aleatoriamente
    public int[] rolarDadosAleatorios() {
        return jogoFacade.rolarDados();
    }

    // Método para acionar a passagem secreta do jogador da vez
    public boolean usarPassagemSecreta(String jogador) {
        boolean sucesso = jogoFacade.moverPorPassagemSecreta(jogador);
        if (sucesso) {
            // Se o teletransporte deu certo, força o turno a passar
            jogoFacade.passarTurno();
        }
        return sucesso;
    }


    // O Controller pede à Fachada as cartas já formatadas como texto
    public List<String[]> obterDadosCartasDoJogadorAtual() {
        // Chamando o nome correto do método que está na fachada do Model
        return jogoFacade.obterDadosCartasDoJogadorAtual();
    }

    // Retorna a Façade para a View apenas pegar informações (getters)
    public JogoClueInicio getModel() {
        return jogoFacade;
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
}
