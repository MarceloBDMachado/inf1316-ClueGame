package model;

class Carta {
    private final String nome;
    private final TipoCarta tipo;

    Carta(String nome, TipoCarta tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }

    String getNome() {
        return nome;
    }

    TipoCarta getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return nome + " (" + tipo + ")";
    }
}