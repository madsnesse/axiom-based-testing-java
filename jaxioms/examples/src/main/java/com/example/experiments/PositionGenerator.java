package com.example.experiments;

import com.example.processors.Generator;

import java.util.Random;

public class PositionGenerator implements Generator<Position> {

    private Random random;

    public PositionGenerator() {
        this.random = new Random();
    }

    @Override
    public Position generate() {
        return new Position(random.nextInt(0,100), random.nextInt(0,100));
    }

    @Override
    public Position[] generateMany(int n) {
        Position[] positions = new Position[n];
        for (int i = 0; i < n; i++) {
            positions[i] = generate();
        }
        return positions;
    }
}
