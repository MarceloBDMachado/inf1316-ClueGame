package controller;

import model.JogoClueInicio;
import model.ObservadorIF;

public class Controller {
    private static Controller instance;
    private JogoClueInicio partida;

    private Controller() {
        this.partida = new JogoClueInicio();
        this.partida.prepararPartida(6);
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public JogoClueInicio getPartida() {
        return partida;
    }

    public void registrarObservador(ObservadorIF obs) {
        partida.add(obs);
    }

    public void processarRolagemDados(boolean manual, int d1, int d2) {
        partida.processarRolagem(manual, d1, d2);
    }

    public void tentarMover(int linhaDestino, int colunaDestino) {
        partida.tentarMover(linhaDestino, colunaDestino);
    }

    // NOVO: Expostos para a View
    public boolean verificarPassagemSecreta() {
        return partida.obterDestinoPassagemSecreta(partida.getJogadorDaVez()) != null;
    }

    public void usarPassagemSecreta() {
        partida.usarPassagemSecreta();
    }
}
