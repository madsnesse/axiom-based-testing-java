package org.example;

import no.uib.ii.annotations.Group;

@Group(identity = "zero", binaryOperation = "add", inverse = "negative")
public class Position1 {

    private final int x;
    private final int y;




    public Position1(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Position1 zero() {
        return new Position1(0,0);
    }

    Position1 add(Position1 p, Position1 q) {
        return new Position1(p.x + q.x, p.y + q.y);
    }

    Position1 negative(Position1 p) {
        return new Position1(-1 * p.x, -1 * p.y);
    }


}
