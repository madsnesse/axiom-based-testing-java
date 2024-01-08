package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PositionTest {

    @Test
    void add() {
        System.out.println("running tests...");
        PositionGeneratedTest.addIsAssociative(new Position(1, 2), new Position(3, 4), new Position(5, 6));
    }
}