package no.ii.uib.processors

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import no.uib.ii.AxiomDefinition
import java.io.File
import java.util.HashMap
import javax.annotation.processing.Filer

class TestClassGenerator {
    companion object {
        fun generateTestClassesForAxioms(axiomDeclarations: HashMap<String, List<AxiomDefinition>>, filer: Filer?) {

            for (axiomDeclaration in axiomDeclarations) {
                generateTestClass(filer!!, axiomDeclaration.key, axiomDeclaration.value);
            }


        }

        private fun generateTestClass(filer: Filer,
                                  className: String,
                                      axiomDeclarations: List<AxiomDefinition>) {

            var cu: CompilationUnit = CompilationUnit("no.uib.ii.jaxioms");

            val imports = resolveImports(axiomDeclarations);
            val methods: List<MethodDeclaration> = axiomDeclarations.map { axiomDeclaration -> axiomDeclaration.getMethod() }

            imports.forEach(fun(import: ImportDeclaration) {
                    cu.addImport(import)
                })
                val className = className.split(".").last();
                var classDeclaration = cu.addClass("GeneratedTestClass${className}")
                methods.forEach(fun(method: MethodDeclaration) {
                    classDeclaration.addMember(
                            MethodDeclaration().setBody(method.body.orElseThrow())
                                    .setType(method.type)
                                    .setName(method.name)
                                    .setParameters(method.parameters)
                                    .setModifiers(method.modifiers)
                                    .setThrownExceptions(method.thrownExceptions)
                                    .addAnnotation("Test")
                    )
                })
                println(cu.toString())

                //TODO check if exists
                val f : File = File("target/generated-sources/annotations/no/uib/ii/jaxioms/GeneratedTestClass${className}.java");
                if (f.exists()) {
                    f.delete();
                }
            //TODO create in test folder

                filer.createSourceFile("no.uib.ii.jaxioms.GeneratedTestClass${className}").openWriter().use { writer ->
                    writer.write(cu.toString())
                }

            }

        private fun resolveImports(axiomDeclarations: List<AxiomDefinition>): List<ImportDeclaration> {

            val list = mutableListOf<ImportDeclaration>();
            list += (ImportDeclaration("org.junit.jupiter.api.Assertions.assertEquals", true, false));
            list += (ImportDeclaration("org.junit.jupiter.api.Test", false, false));
            axiomDeclarations.forEach { axDef ->
                axDef.getMethod().parameters.forEach { parameter ->
                    //TODO resolve import for custom types
                    //TODO use parameter.type.resolve() maybe

                }

            }

            return list;
        }
    }
}
