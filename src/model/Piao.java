package model;

class Piao {
    private final String nomeSuspeito;
    private Casa posicaoAtual;

    Piao(String nomeSuspeito) {
        this.nomeSuspeito = nomeSuspeito;
    }

    String getNome() {
        return nomeSuspeito;
    }

    Casa getPosicaoAtual() {
        return posicaoAtual;
    }

    void setPosicaoAtual(Casa novaPosicao) {
        this.posicaoAtual = novaPosicao;
    }
}