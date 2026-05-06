package model;

class Envelope {
    private Carta crimeSuspeito;
    private Carta crimeArma;
    private Carta crimeComodo;

    void definirSolucao(Carta suspeito, Carta arma, Carta comodo) {
        this.crimeSuspeito = suspeito;
        this.crimeArma = arma;
        this.crimeComodo = comodo;
    }

    // Os métodos para revelar a solução serão criados futuramente
    // quando implementarmos a lógica de "Acusação Final".
}