package org.example;

import no.uib.ii.annotations.InheritAxioms;

import java.util.Objects;

@InheritAxioms
public class PositionComparable implements Comparable<PositionComparable> {
    int x;
    int y;

    public PositionComparable(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(PositionComparable o) {
        return Integer.compare(this.x + this.y, o.x + o.y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PositionComparable )) return false;
        PositionComparable p = (PositionComparable) o;
        return this.x == p.x && this.y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public static void main(String[] args) {
        PositionComparable p1 = new PositionComparable(1, 2);
        PositionComparable p2 = new PositionComparable(2, 1);
        System.out.println(p1.compareTo(p2));
        System.out.println(p1.equals(p2));
    }
}
