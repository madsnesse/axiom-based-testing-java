package no.uib.ii.defaultgenerators;

import java.util.Random;

public abstract class Generator<T>{

    Random random;

    public abstract T generate();

    public Generator(Random r) {
        this.random = r;
    }
}
