package org.example;
import no.uib.ii.annotations.Axiom;
import no.uib.ii.annotations.InheritAxioms;

import java.util.Objects;

import static no.uib.ii.StaticMethods.assertEquals;

@InheritAxioms
public class Position {

    int x;
    int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void add(Position c) {
        this.x += c.x;
        this.y += c.y;
    }

    @Axiom
    public static void addIsAssociative(Position a, Position b, Position c) {
        Position aPlusB = a.deepCopy();
        aPlusB.add(b); //a + b
        aPlusB.add(c); // (a+b) + c

        Position bPlusC = b.deepCopy();
        bPlusC.add(c); //b + c
        bPlusC.add(a); // a + (b+c)
        assertEquals(aPlusB, bPlusC);
    }
    //helper method
    public Position deepCopy() {
        return new Position(x,y);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof Position)) return false;
        Position p = ((Position) o);
        return x == p.x && y == p.y;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}