package axioms;


import no.uib.ii.annotations.AxiomForExistingClass;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Required axioms for the java top level class java.lang.Object as taken from
 * <a url="http://java.sun.com/j2se/1.5.0/docs/api/">Java 5.0 API</a>
 * <p>
 * These are axioms for the requirements on the class. See separate files for
 * recommendations.
 *
 * @see java.lang.Object
 * Taken from JAxT @see <url>https://www.ii.uib.no/mouldable/testing/jaxt/index.html</url>
 * @author Magne Haveraaen & Karl Trygve Kalleberg, 2007, adapted to Jaxioms by Mads Bårvåg Nesse
 * @version 2024-05-30 (adapted to Jaxions and JUnit 5)
 */
public class ObjectAxioms {

    /**
     * equals property 1: reflexive.
     * <p>
     * The equals method implements an equivalence relation on non-null object
     * references:<br>
     * It is reflexive: for any non-null reference value x, x.equals(x) should
     * return true.
     *
     * @param x
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsProperty1reflexive(Object x) {
        if (x != null)
            assertEquals(x, x, "Compares (" + x.getClass() + ")" + x.toString());
    }

    /**
     * equals property 2: symmetric.
     * <p>
     * The equals method implements an equivalence relation on non-null object
     * references:<br>
     * It is symmetric: for any non-null reference values x and y, x.equals(y)
     * should return true if and only if y.equals(x) returns true
     *
     * @param x
     * @param y
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsProperty2symmetric(Object x, Object y) {
        if (x != null && y != null)
            assertEquals(x.equals(y), y.equals(x), "Compares (" + x.getClass() + ")" + x.toString() + " (" + y.getClass() + ")" + y.toString());
    }

    /**
     * equals property 3: transitive.
     * <p>
     * The equals method implements an equivalence relation on non-null object
     * references:<br>
     * It is transitive: for any non-null reference values x, y, and z, if
     * x.equals(y) returns true and y.equals(z) returns true, then x.equals(z)
     * should return true.
     *
     * @param x
     * @param y
     * @param z
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsProperty3transitive(Object x, Object y, Object z) {
        if (x != null && y != null && z != null)
            if (x.equals(y) && y.equals(z))
                assertEquals(x, z, "Compares (" + x.getClass() + ")" + x.toString() + " (" + y.getClass() + ")" + y.toString()
                        + " (" + z.getClass() + ")" + z.toString());
    }


    /**
     * equals property 5: comparison to null.
     * <p>
     * The equals method implements an equivalence relation on non-null object
     * references:<br>
     * For any non-null reference value x, x.equals(null) should return false.
     *
     * @param x
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void equalsProperty5null(Object x) {
        if (x != null)
            assertFalse(x.equals(null), "Compares (" + x.getClass() + ")" + x.toString());
    }

    /**
     * hashCode property 1: behaves like a deterministic function.
     * <p>
     * Whenever it is invoked on the same object more than once during an execution
     * of a Java application, the hashCode method must consistently return the same
     * integer, provided no information used in equals comparisons on the object is
     * modified. This integer need not remain consistent from one execution of an
     * application to another execution of the same application.
     *
     * @param a Object that should not be modified
     * @param r number of times to repeat check of hashCode
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void hashCodeProperty1function(Object a, int r) {
        int h = a.hashCode();
        for (int i = 0; i < r; i++) {
            // assuming a remains unchanged wrt method equals()
            assertEquals(h,
                    a.hashCode(), "Checks " + i + " out of " + r + " times (" + a.getClass() + ")" + a.toString());
        }
    }

    /**
     * hashCode property 2: equals is a congruence relation wrt. hashCode.
     * <p>
     * If two objects are equal according to the equals(Object) method, then calling
     * the hashCode method on each of the two objects must produce the same integer
     * result.
     *
     */
    @AxiomForExistingClass(className = "java.lang.Object")
    public static void hashCodeProperty2congruenceEquals(Object a, Object b) {
        if (a == null)
            return;
        if (a.equals(b))
            assertEquals(
                    a.hashCode(), b.hashCode(), "Checks (" + a.getClass() + ")" + a.toString() + " (" + b.getClass() + ")" + b.toString());
    }
}
