package axioms;

import no.uib.ii.annotations.AxiomForExistingClass;

import static no.uib.ii.StaticMethods.assertEquals;
import static no.uib.ii.StaticMethods.assertTrue;

public class ComparableAxioms {

    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToConsistentWithSignum(T x, T y) {
        assertEquals(java.lang.Integer.signum(x.compareTo(y)), -java.lang.Integer.signum(y.compareTo(x)));
    }

    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToTransitive(T x, T y, T z) {
        if (x.compareTo(y) < 0 && y.compareTo(z) < 0) {
            assertEquals(true, x.compareTo(z) < 0);
        } else assertTrue(true);
    }
}