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

    // MÉTODO ADICIONADO: Compara os nomes textuais recebidos com as cartas secretas
    boolean verificarSolucao(String suspeito, String arma, String comodo) {
        if (crimeSuspeito == null || crimeArma == null || crimeComodo == null) {
            return false;
        }
        return crimeSuspeito.getNome().equals(suspeito) &&
                crimeArma.getNome().equals(arma) &&
                crimeComodo.getNome().equals(comodo);
    }
}