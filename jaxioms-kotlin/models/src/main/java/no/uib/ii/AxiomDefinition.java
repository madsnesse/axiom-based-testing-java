package no.uib.ii;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.List;

/*
* Used to store all axioms found during the processing of annotations
* */
public class AxiomDefinition {

    private MethodDeclaration method;
    private String name;
    private List l;


    public AxiomDefinition(MethodDeclaration method, String name) {
        this.method = method;
        this.name = name;

    }

    public MethodDeclaration getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + method.hashCode();
    }
}
