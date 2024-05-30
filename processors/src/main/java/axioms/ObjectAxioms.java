package axioms;


import no.uib.ii.annotations.AxiomForExistingClass;

import static no.uib.ii.StaticMethods.assertEquals;

/**
 * Axioms for the {@link Object} class.
 * As defined in {@link <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Object.html">...</a>}.
 * Code inspired by JAxT {@link <a href="https://www.ii.uib.no/mouldable/testing/jaxt/index.html">...</a>}
 */
public class ObjectAxioms {

    /**
     * It is reflexive: for any non-null reference value x, x.equals(x) should return true.
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsIsReflexive(Object o) {
        assertEquals(o,o);
    }
    /**
     * It is symmetric: for any non-null reference values x and y, x.equals(y)
     * should return true if and only if y.equals(x) returns true.
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsIsSymmetric(Object x, Object y) {
        assertEquals(x.equals(y), y.equals(x));
    }
    /**
     * It is transitive: for any non-null reference values x, y, and z,
     * if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should return true.
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsIsTransitive(Object x, Object y, Object z) {
        if (x.equals(y) && y.equals(z)) {
            assertEquals(x, z);
        }
    }
    /**
     * For any non-null reference value x, x.equals(null) should return false.
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsNullIsFalse(Object x) {
        assertEquals(false, x.equals(null));
    }

    /**
     * If two objects are equal according to the {@code equals} method,
     * then calling the {@code hashCode} method on each of the two objects must produce the same integer result.
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void hashCodeCongruenceOnEquals(Object x, Object y) {
        if (x.equals(y)) {
            assertEquals(x.hashCode(), y.hashCode());
        }
    }

}
