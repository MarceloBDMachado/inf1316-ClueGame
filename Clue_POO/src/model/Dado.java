package model;

import java.util.Random;

class Dado {
    private final Random gerador;

    Dado() {
        this.gerador = new Random();
    }
    // gera um número de 0 a 5 e soma +1 para termos 1 a 6.
    int rolar() {
        return gerador.nextInt(6) + 1;
    }
}