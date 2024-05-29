package org.example;

import no.uib.ii.annotations.DefinedGenerator;
import no.uib.ii.defaultgenerators.Generator;

@DefinedGenerator
public class Position3DGenerator extends Generator<Position3D> {
    @Override
    public Position3D generate() {
        return new Position3D(random.nextInt(), random.nextInt(), random.nextInt());
    }
}
