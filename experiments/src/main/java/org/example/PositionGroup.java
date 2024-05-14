package org.example;

import no.uib.ii.algebaric_structures.Group;
import no.uib.ii.annotations.InheritAxioms;

@InheritAxioms
public class PositionGroup implements Group<PositionGroup> {
    int x;
    int y;

    public PositionGroup(int x, int y) {
        this.x = x;
        this.y = y;
    }


    @Override
    public PositionGroup binaryOperation(PositionGroup positionGroup) {
        return new PositionGroup(this.x + positionGroup.x, this.y + positionGroup.y);
    }

    @Override
    public PositionGroup inverse() {
        return new PositionGroup(-this.x, -this.y);
    }

    @Override
    public PositionGroup identity() {
        return new PositionGroup(0, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PositionGroup that = (PositionGroup) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
