package java.lang.axioms;

import no.uib.ii.annotations.AxiomForExistingClass;

import static no.uib.ii.StaticMethods.assertEquals;
import static no.uib.ii.StaticMethods.assertTrue;

/**
 * Axioms for the {@link Comparable} interface.
 * As defined in {@link <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Comparable.html">...</a>}.
 * Code inspired by JAxT {@link <a href="https://www.ii.uib.no/mouldable/testing/jaxt/index.html">...</a>}
 */
public class ComparableAxioms {

    /**
     * The implementor must ensure {@code signum(x.compareTo(y)) == -signum(y.compareTo(x))} for all {@code x} and {@code y}.
     * (This implies that {@code x.compareTo(y)} must throw an exception if and only if {@code y.compareTo(x)} throws an exception.)
     */
    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToConsistentWithSignum(T x, T y) {
        try{
            var left = x.compareTo(y);
            var right = y.compareTo(x);
            assertEquals(Integer.signum(left), -Integer.signum(right));
        } catch (RuntimeException e) {
            try {
                y.compareTo(x);
                assertEquals(1,0);
            } catch (RuntimeException e2) {
                assertEquals(1,1);
            }
        }
    }

    /**
     * The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies {@code x.compareTo(z) > 0}.
     */
    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToTransitive(T x, T y, T z) {
        if (x.compareTo(y) > 0 && y.compareTo(z) > 0) {
            assertTrue(x.compareTo(z) > 0);
        }
    }

    /**
     * It is strongly recommended, but not strictly required that (x.compareTo(y)==0) == (x.equals(y)).//TODO create support for recommended axioms
     * Generally speaking, any class that implements the Comparable interface
     * and violates this condition should clearly indicate this fact. The recommended language is
     * "Note: this class has a natural ordering that is inconsistent with equals."
     */
    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToEqualsConsistent(T x, T y) {
        assertEquals(x.compareTo(y) == 0, x.equals(y));
    }

}
