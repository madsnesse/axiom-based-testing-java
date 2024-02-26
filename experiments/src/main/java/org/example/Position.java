package org.example;
import no.uib.ii.annotations.Axiom;
import static no.uib.ii.StaticMethods.assertEquals;

public class Position {

    private int x;
    private int y;

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
        a.add(bPlusC); // a + (b+c)
        assertEquals(aPlusB, bPlusC);
    }
    //helper method
    public Position deepCopy() {
        return new Position(x,y);
    }
}