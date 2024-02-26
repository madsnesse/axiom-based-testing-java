package org.example;

import no.uib.ii.algebaric_structures.Group;
import no.uib.ii.algebaric_structures.Groupoid;
import no.uib.ii.annotations.Axiom;
import no.uib.ii.annotations.InheritAxioms;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.util.Comparator;

import static no.uib.ii.StaticMethods.assertTrue;

@InheritAxioms
public class Pos implements Comparable<Pos>, Group<Pos> {

    public int x;
    public int y;

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
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
    public int compareTo(@NotNull Pos o) {
        return 0;
    }

    @Axiom //TODO kanskje datainvariant som egen annotasjon
    public void dataInvariant(Pos p) {

        assertTrue(p.x > 0 && p.x <= 100, "x should be between 0 and 100");
        assertTrue(p.y > 0 && p.y <= 100, "y should be between 0 and 100");
    }


}


//TODO: se på å arve på denne måten i stedet:
class PosCompare implements Comparator<Pos> {

    @Override
    public int compare(Pos o1, Pos o2) {
        return o1.compareTo(o2);
    }
}

class PosAdditive implements Groupoid<Pos> {

    @Override
    public Pos binaryOperation(Pos a, Pos b) {
        return a.binaryOperation(b);
    }

    @Override
    public Pos identity() {
        return null;
    }

    @Override
    public Pos inverse(Pos a) {
        return null;
    }
}
