package org.example;

import no.uib.ii.Axiom;

import static junit.framework.Assert.assertEquals;

/**
 * A position in 2D space.
 */
public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Adds the given position to this position.
     * @param p
     */
    void add(Position p) {
        this.x += p.x;
        this.y += p.y;
    }

    /**
     * addition is associative
     * @param a
     * @param b
     * @param c
     */
    @Axiom
    public static void addIsAssociative(Position a, Position b, Position c) {
        Position aPlusB = a;
        aPlusB.add(b);
        Position bPlusC = b;
        bPlusC.add(c);
        Position aPlusBPlusC = a;
        aPlusBPlusC.add(c);
        Position bPlusCPlusA = b;
        bPlusCPlusA.add(a);
        assertEquals(aPlusBPlusC, bPlusCPlusA);
    }

}
