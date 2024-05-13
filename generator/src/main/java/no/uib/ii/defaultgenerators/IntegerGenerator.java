package no.uib.ii.defaultgenerators;

import no.uib.ii.annotations.DefinedGenerator;

import java.util.Random;

@DefinedGenerator
public class IntegerGenerator extends Generator<Integer> {
    public IntegerGenerator(Random r) {
        super(r);
    }

    @Override
    public Integer generate(){
        return random.nextInt();
    }

}
