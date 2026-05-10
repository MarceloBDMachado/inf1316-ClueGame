package model;

import java.util.Random;

class Dado {
    private final Random gerador;

    Dado() {
        this.gerador = new Random();
    }

    int rolar() {
        return gerador.nextInt(6) + 1;
    }
}