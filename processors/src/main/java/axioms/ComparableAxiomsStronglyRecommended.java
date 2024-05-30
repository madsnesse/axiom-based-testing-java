package axioms;

import no.uib.ii.annotations.AxiomForExistingClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Axioms capturing strong recommendations for the java interface
 * {@link Comparable}: consistency with equals.
 * <p>
 * Quoted from the Java 14 documentation:
 * <ul>
 * This interface imposes a total ordering on the objects of each class that
 * implements it. This ordering is referred to as the class's <i>natural
 * ordering</i>, and the class's {@code compareTo} method is referred to as its
 * <i>natural comparison method</i>. ...
 * <p>
 * The natural ordering for a class {@code C} is said to be <i>consistent with
 * equals</i> if and only if {@code e1.compareTo(e2) == 0} has the same boolean
 * value as {@code e1.equals(e2)} for every {@code e1} and {@code e2} of class
 * {@code C}. ...
 * <p>
 * It is strongly recommended (though not required) that natural orderings be
 * consistent with equals. This is so because sorted sets (and sorted maps)
 * without explicit comparators behave "strangely" when they are used with
 * elements (or keys) whose natural ordering is inconsistent with equals. In
 * particular, such a sorted set (or sorted map) violates the general contract
 * for set (or map), which is defined in terms of the {@code equals} method.
 * <p>
 * ...
 * <p>
 * Virtually all Java core classes that implement {@code Comparable} have
 * natural orderings that are consistent with equals. One exception is
 * {@code java.math.BigDecimal}, whose natural ordering equates
 * {@code BigDecimal} objects with equal values and different precisions (such
 * as 4.0 and 4.00).
 * </ul>
 * The axioms for the consistency is split into two parts
 * <ul>
 * <li>3a for the natural order to be more coarse grained.
 * <li>3b for the natural order to be more fine grained.
 * </ul>
 *
 * @see java.lang.Comparable
 * @see axioms.ComparableAxioms
 * Taken from JAxT @see <url>https://www.ii.uib.no/mouldable/testing/jaxt/index.html</url>
 * @author Magne Haveraaen & Karl Trygve Kalleberg, adapted to Jaxioms by Mads Bårvåg Nesse
 * @since 2007-03-06
 * @version 2024-05-30 (adapted to Jaxions and JUnit 5)
 *
 */
public class ComparableAxiomsStronglyRecommended {
    /**
     * Natural equivalence is more coarse grained than {@link Object#equals(Object)
     * equals}: checks that the equivalence defined by the
     * {@link Object#equals(Object) equals} method implies the equivalence defined
     * by the {@link Comparable#compareTo(Object) compareTo} method.
     *
     * @param T Comparable type to test
     * @param x object to be compared
     * @param y object to be compared
     */
    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToProperty3anaturalOrderingEquals(T x, T y) {
        try {
            if (x.equals(y))
                assertEquals( 0, x.compareTo(y), "Note: this class has a natural ordering that is inconsistent with equals."
                        + " Method compareTo is too fine grained:" + " x=" + x + " y=" + y);
        } catch (RuntimeException e) {
            assertTrue(true,"some run-time exception occurred");
        }
    }

    /**
     * Natural equivalence is more fine grained than {@link Object#equals(Object)
     * equals}: checks that the equivalence defined by the
     * {@link Comparable#compareTo(Object) compareTo} method implies the equivalence
     * defined by the {@link Object#equals(Object) equals} method
     *
     * @param T Comparable type to test
     * @param x object to be compared
     * @param y object to be compared
     */
    @AxiomForExistingClass(className = "java.lang.Comparable")
    public static <T extends Comparable<T>> void compareToProperty3bnaturalOrderingEquals(T x, T y) {
        try {
            if (x.compareTo(y) == 0)
                assertEquals(x, y, "Note: this class has a natural ordering that is inconsistent with equals."
                        + " Method compareTo is too coarse grained: ");
        } catch (RuntimeException e) {
            assertTrue(true, "some run-time exception occurred");
        }
    }

}
