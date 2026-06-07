package model;

public class Casa {
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

    public String getNomeComodo() {
        return nomeComodo;
    }

    public TipoCasa getTipo() {
        return tipo;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOcupada() {
        return piaoOcupante != null;
    }

    Piao getPiao() {
        return piaoOcupante;
    }

    void setPiao(Piao piao) {
        this.piaoOcupante = piao;
    }
}