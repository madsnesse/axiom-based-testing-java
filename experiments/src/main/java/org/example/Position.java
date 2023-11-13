package org.example;

import no.uib.ii.Axiom;
import no.uib.ii.AxiomType;
import no.uib.ii.Test;

import java.util.Objects;

import static junit.framework.Assert.assertEquals;
/**
 * A position in 2D space.
 */
public class Position implements Test {

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
    public void add(Position p) {
        this.x += p.x;
        this.y += p.y;
    }

    /**
     * addition is associative
     * @param a
     * @param b
     * @param c
     */
    @Axiom(type = AxiomType.REQUIRED)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public static void main(String[] args) {
        Position p = new Position(1,2);
        System.out.println(p.equals(null));
    }
}
