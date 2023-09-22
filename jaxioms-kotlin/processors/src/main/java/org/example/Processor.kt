package org.example

import autovalue.shaded.com.google.auto.service.AutoService
import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import java.nio.file.Files
import java.nio.file.Paths
import javax.annotation.processing.*
import javax.annotation.processing.Processor
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement


@SupportedAnnotationTypes("no.uib.ii.Axiom", "no.uib.ii.Generator")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor::class)
class Processor : AbstractProcessor() {
    private fun getImports(): List<ImportDeclaration> {
        val result = mutableListOf<ImportDeclaration>();
        val parser = JavaParser();
        val parseResult = parser.parseImport("import org.junit.jupiter.api.Test;");

        val import = parseResult.result.orElseThrow();

        result.add(import);
        return result;
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {

        annotations?.forEach(
            fun(annotation: TypeElement) {
                roundEnv?.getElementsAnnotatedWith(annotation)?.forEach(
                        fun (element: Element) {
                            val enclosingElement : Element = annotation.enclosingElement
                            val elementKind : ElementKind = element.kind
                            val typeElement : TypeElement = element.enclosingElement as TypeElement

                            if (elementKind == ElementKind.METHOD) {
                                val executableElement : ExecutableElement = element as ExecutableElement
                            }
                            var parser : JavaParser = JavaParser();

                            var classToTest = parser.parseClassOrInterfaceType(typeElement.simpleName.toString());

                            classToTest.ifSuccessful(fun (classOrInterfaceType : ClassOrInterfaceType) {
                                println(classOrInterfaceType.toString())
                            })

                            generateTestClass(
                                    getImports(),
                                    getMethods(),
                                    typeElement.simpleName.toString()
                            );

                        }
                )



            }
        )
        return true;
    }

    private fun getMethods(): List<MethodDeclaration> {
        val result = mutableListOf<MethodDeclaration>();
        val parser = JavaParser();
        val parseResult = parser.parseMethodDeclaration("public void test() {}");

        val methodDeclaration = parseResult.result.orElseThrow();
        methodDeclaration.addAnnotation("Test");

        result.add(methodDeclaration);
        return result;

    }

    private fun generateTestClass(imports: List<ImportDeclaration>,
                                  methods: List<MethodDeclaration>,
                                  nameOfClassToBeTested: String) {

        var cu: CompilationUnit = CompilationUnit("no.uib.ii");
        imports.forEach(fun (import: ImportDeclaration) {
            cu.addImport(import)
        })
        var classDeclaration = cu.addClass("GeneratedTestClass${nameOfClassToBeTested}")
        methods.forEach(fun (method: MethodDeclaration) {
            classDeclaration.addMember(method)
        })

        println(cu.toString())

    }

}