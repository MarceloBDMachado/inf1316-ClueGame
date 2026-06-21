package model;

class Carta {
    private final String nome;
    private final TipoCarta tipo;

    Carta(String nome, TipoCarta tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }
    // Getters simples para ler as informações da carta. Todos finals, logo imutáveis.
    String getNome() {
        return nome;
    }
    TipoCarta getTipo() {
        return tipo;
    }
    // Override no toString para printar, por exemplo, Faca (ARMA)
    @Override
    public String toString() {
        return nome + " (" + tipo + ")";
    }
}