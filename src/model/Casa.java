package model;

class Casa {
    private final int x;
    private final int y;
    private final TipoCasa tipo;
    private String nomeComodo;
    private Piao piaoOcupante;

    Casa(int x, int y, TipoCasa tipo) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.piaoOcupante = null;
    }

    void setNomeComodo(String nomeComodo) {
        this.nomeComodo = nomeComodo;
    }

    String getNomeComodo() {
        return nomeComodo;
    }

    TipoCasa getTipo() {
        return tipo;
    }

    int getX() { return x; }
    int getY() { return y; }

    boolean isOcupada() {
        return piaoOcupante != null;
    }

    Piao getPiao() {
        return piaoOcupante;
    }

    void setPiao(Piao piao) {
        this.piaoOcupante = piao;
    }
}