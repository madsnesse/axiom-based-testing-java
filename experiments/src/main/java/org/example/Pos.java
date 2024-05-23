package org.example;
import no.uib.ii.algebaric_structures.Group;
import no.uib.ii.annotations.InheritAxioms;

import java.util.Objects;

import static no.uib.ii.StaticMethods.assertEquals;
import static no.uib.ii.StaticMethods.assertTrue;

@InheritAxioms
public class Pos implements Comparable<Pos>, Group<Pos> {

    public int x;
    public int y;

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
        dataInvariant(this);
    }

    @Override
    public Pos binaryOperation(Pos a) {
        return new Pos((this.x + a.x) % 8, (this.y + a.y) % 8);
    }

    @Override
    public Pos inverse() {
        return new Pos(this.y, this.x);
    }

    @Override
    public Pos identity() {
        return new Pos(0,0);
    }

    @Override
    public int compareTo(Pos o) {
        return 0;
    }

    //@Axiom //TODO kanskje datainvariant som egen annotasjon
    public static void dataInvariant(Pos p) {
        assertEquals(true, 0 <= p.x && p.y < 8);
        assertEquals(true, 0 <= p.y && p.y < 8);
    }

    @Override
    public String toString() {
        return "Pos{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object p) {
        if (this == p) return true;
        if (p == null) return false;
        if (!(p instanceof Pos)) return false;
        Pos pos = ((Pos) p);
        return x == pos.x && y == pos.y;
    }

}


//TODO: se på å arve på denne måten i stedet:
//class PosCompare implements Comparator<Pos> {
//
//    @Override
//    public int compare(Pos o1, Pos o2) {
//        return o1.compareTo(o2);
//    }
//}
//
//class PosAdditive implements Groupoid<Pos> {
//
//    @Override
//    public Pos binaryOperation(Pos a, Pos b) {
//        return a.binaryOperation(b);
//    }
//
//    @Override
//    public Pos identity() {
//        return null;
//    }
//
//    @Override
//    public Pos inverse(Pos a) {
//        return null;
//    }
//}
