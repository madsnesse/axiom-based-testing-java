package no.uib.ii;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GeneratorBuilder {
    List<ImportDeclaration> imports;
    String name;
    public GeneratorBuilder() {
        this.imports = new ArrayList<>();
    }

    public ClassOrInterfaceDeclaration build() {
        return null;
    }

    public GeneratorBuilder addImport(ImportDeclaration s) {
        this.imports.add(s);
        return this;
    }

    public GeneratorBuilder addImports(Collection<ImportDeclaration> importDeclarationCollection) {
        this.imports.addAll(importDeclarationCollection);
        return this;
    }

    public GeneratorBuilder setName(String s) {
        this.name = s;
        return this;
    }
}
