package org.example;

import no.uib.ii.annotations.Axiom;
import no.uib.ii.annotations.InheritAxioms;

import static no.uib.ii.StaticMethods.assertEquals;

@InheritAxioms
public class Position3D extends Pos {

    private int z;
    public Position3D(int x, int y, int z) {
        super(x, y);
        this.z = z;
    }

    @Axiom
    public static void binaryOperationWithPositionReturnsPosition(Position3D p, Pos q) {
        Pos newP = p.binaryOperation(q);
        assertEquals(Pos.class, newP.getClass());
    }


}
