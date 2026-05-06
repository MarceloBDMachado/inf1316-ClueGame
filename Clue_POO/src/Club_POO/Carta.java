package Club_POO;

class Carta {
    private String nome;
    private TipoCarta tipo;

    enum TipoCarta { SUSPEITO, ARMA, APOSENTO }

    public Carta(String nome, TipoCarta tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }
    // Getters...
}
