package model;

class Piao {
    private final String nomeSuspeito; // Final pois um pião nunca pode se tornar outro
    private Casa posicaoAtual;

    Piao(String nomeSuspeito) {
        this.nomeSuspeito = nomeSuspeito;
    }

    // Getters simples pra descobrir de quem é o pião e onde ele está.
    String getNome() {
        return nomeSuspeito;
    }
    Casa getPosicaoAtual() {
        return posicaoAtual;
    }
    // Setter para o posição, atualiza a posição atual para ser a nova posição pós movimento
    void setPosicaoAtual(Casa novaPosicao) {
        this.posicaoAtual = novaPosicao;
    }
}