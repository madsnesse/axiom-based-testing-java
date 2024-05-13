package no.uib.ii.algebaric_structures;

import no.uib.ii.annotations.AxiomForExistingClass;

import static no.uib.ii.StaticMethods.assertEquals;

public class ComparableAxioms {

    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToConsistentWithSignum(T x, T y) {
        assertEquals(java.lang.Integer.signum(x.compareTo(y)), -java.lang.Integer.signum(y.compareTo(x)));
    }
}
