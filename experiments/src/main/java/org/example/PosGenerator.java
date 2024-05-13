package org.example;

import no.uib.ii.annotations.DefinedGenerator;
import no.uib.ii.defaultgenerators.Generator;

@DefinedGenerator
public class PosGenerator extends Generator<Pos> {
    @Override
    public Pos generate() {
        return new Pos(this.random.nextInt(8), this.random.nextInt(8));
    }
}
