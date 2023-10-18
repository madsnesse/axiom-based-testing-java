package no.ii.uib.processors

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import no.uib.ii.AxiomDefinition
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
                                  axiomDeclarations: List<AxiomDefinition>) {

                var cu: CompilationUnit = CompilationUnit("no.uib.ii.jaxioms");

                val imports = resolveImports(axiomDeclarations);
                val methods: List<MethodDeclaration> = listOf()

            imports.forEach(fun(import: ImportDeclaration) {
                    cu.addImport(import)
                })
                var classDeclaration = cu.addClass("GeneratedTestClass${nameOfClassToBeTested}")
                methods.forEach(fun(method: MethodDeclaration) {
                    classDeclaration.addMember(method)
                })
                println(cu.toString())
                filer.createSourceFile("no.uib.ii.jaxioms.GeneratedTestClass${nameOfClassToBeTested}").openWriter().use { writer ->
                    writer.write(cu.toString())
                }

            }

        private fun resolveImports(axiomDeclarations: List<AxiomDefinition>): List<ImportDeclaration> {
            TODO("Not yet implemented")
        }
    }
}
