package no.uib.ii.defaultgenerators;

import java.util.Random;

public abstract class Generator<T>{

    public Random random;

    public abstract T generate();

    public Generator() {
        this.random = new Random();
    }
    public Generator(Random r) {
        this.random = r;
    }
}
