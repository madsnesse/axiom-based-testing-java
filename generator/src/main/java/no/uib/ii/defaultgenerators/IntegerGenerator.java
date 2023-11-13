package no.uib.ii.defaultgenerators;

import java.util.Random;

public class IntegerGenerator extends Generator<Integer> {
    public IntegerGenerator(Random r) {
        super(r);
    }

    @Override
    public Integer generate(){
        return random.nextInt();
    }

}
