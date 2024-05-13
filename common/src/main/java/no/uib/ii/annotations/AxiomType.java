package no.uib.ii.annotations;

/**
 * Taken from JAxT
 */
public enum AxiomType {

    /**
     * Axiom should hold for this class and all subclasses
     */
    REQUIRED,
    /**
     * Inactive axioms that are not active unless specified //TODO: implement
     */
    OPTIONAL,
    /**
     * Axiom should hold for all subclasses
     */
    SUBCLASS,
    /**
     * Axiom should hold for this class only
     */
    THISCLASS
}
