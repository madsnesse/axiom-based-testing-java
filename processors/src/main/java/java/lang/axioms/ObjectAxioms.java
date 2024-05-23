package java.lang.axioms;


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
        assertEquals(true,o.equals(o));
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
     * It is consistent: for any non-null reference values x and y, multiple invocations of x.equals(y)
     * consistently return true or consistently return false,
     * provided no information used in equals comparisons on the objects is modified.
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsIsConsistent(Object x, Object y) {
        assertEquals(x.equals(y), x.equals(y));
    }

    /**
     * For any non-null reference value x, x.equals(null) should return false.
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsNullIsFalse(Object x) {
        assertEquals(x.equals(null), false);
    }


    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsCongruence(Object o) {
        //TODO implement equalsCongruence
    }

    /**
     * Whenever it is invoked on the same object more than once during an execution of a Java application,
     * the hashCode method must consistently return the same integer,
     * provided no information used in equals comparisons on the object is modified.
     * This integer need not remain consistent from one execution of an application to another execution of the same application.
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void hashCodeConsistent(Object o) {
        assertEquals(o.hashCode(), o.hashCode());
    }

    /**
     * If two objects are equal according to the {@code equals} method,
     * then calling the {@code hashCode} method on each of the two objects must produce the same integer result.
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsHashCodeCongruence(Object a, Object b) {
        if (a.equals(b)) {
            assertEquals(a.hashCode(), b.hashCode());
        }
    }

}
