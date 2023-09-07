package org.example;


import no.uib.ii.DataGenerator;
import no.uib.ii.Generator;

import java.util.Random;


@Generator
public class PositionGenerator implements DataGenerator<Position> {

    private Random random;
    
    public PositionGenerator() {
        this.random = new Random();
    }

    public Position generate() {
        return new Position(random.nextInt(0,100), random.nextInt(0,100));
    }

    public Position[] generateMany(int n) {
        Position[] positions = new Position[n];
        for (int i = 0; i < n; i++) {
            positions[i] = generate();
        }
        return positions;
    }
}
