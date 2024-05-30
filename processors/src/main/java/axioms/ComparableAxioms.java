package axioms;

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
     * The implementor must ensure signum(x.compareTo(y)) == -signum(y.compareTo(x)) for all x and y.
     * (This implies that x.compareTo(y) must throw an exception if and only if y.compareTo(x) throws an exception.)
     */
    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToConsistentWithSignum(T x, T y) {
        try {
            var left = java.lang.Integer.signum(x.compareTo(y));
            assertEquals(left,  -java.lang.Integer.signum(y.compareTo(x)));
        } catch (Exception e) {
            try {
                var right = -java.lang.Integer.signum(y.compareTo(x));
            }catch (Exception ex) {
                assertTrue(true);
            }
            assertTrue(false);
        }
    }

    /**
     * The implementor must also ensure that the relation is transitive:
     * (x.compareTo(y) > 0 && y.compareTo(z) > 0) implies x.compareTo(z) > 0.
     */
    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToTransitive(T x, T y, T z) {
        if (x.compareTo(y) > 0 && y.compareTo(z) > 0) {
            assertEquals(true, x.compareTo(z) > 0);
        } else assertTrue(true);
    }
}