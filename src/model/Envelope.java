package model;

class Envelope {
    private Carta crimeSuspeito;
    private Carta crimeArma;
    private Carta crimeComodo;

    // inicializa o envelope da resposta
    void definirSolucao(Carta suspeito, Carta arma, Carta comodo) {
        this.crimeSuspeito = suspeito;
        this.crimeArma = arma;
        this.crimeComodo = comodo;
    }
}