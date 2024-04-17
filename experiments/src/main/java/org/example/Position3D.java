package org.example;

import no.uib.ii.annotations.InheritAxioms;

@InheritAxioms
public class Position3D extends Position {

    private int z;
    public Position3D(int x, int y, int z) {
        super(x, y);
        this.z = z;
    }

    public Position3D deepCopy() {
        return new Position3D(this.x, this.y, z); // skriv om at x og y ikke kan være private
    }


}
