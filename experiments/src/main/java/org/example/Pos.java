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
        //dataInvariant(this);
    }

    @Override
    public Pos binaryOperation(Pos a) {
        return this;
    }

    @Override
    public Pos inverse() {
        return this;
    }

    @Override
    public Pos identity() {
        return this;
    }

    @Override
    public int compareTo(Pos o) {
        return 0;
    }

    //@Axiom //TODO kanskje datainvariant som egen annotasjon
    public static void dataInvariant(Pos p) {
        assertEquals(p.x, Math.abs(p.x));
        assertEquals(p.y, Math.abs(p.y));
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