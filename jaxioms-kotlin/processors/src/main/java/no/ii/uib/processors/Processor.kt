package no.ii.uib.processors

import autovalue.shaded.com.google.auto.service.AutoService
import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import javax.annotation.processing.*
import javax.annotation.processing.Processor
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.StandardLocation


@SupportedAnnotationTypes("no.uib.ii.Axiom", "no.uib.ii.Generator")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor::class)
class Processor : AbstractProcessor() {

    private val parser = JavaParser();

    private fun getImports(): List<ImportDeclaration> {
        val result = mutableListOf<ImportDeclaration>();
        val parseResult = parser.parseImport("import org.junit.jupiter.api.Test;");
        val parseR = parser.parseImport("import static org.junit.jupiter.api.Assertions.assertEquals;")
        val import = parseResult.result.orElseThrow();
        result.add(import);
        result.add(parseR.result.orElseThrow());
        return result;
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        annotations?.forEach(
            fun(annotation) {
                roundEnv?.getElementsAnnotatedWith(annotation)?.forEach(
                        fun (element: Element) {
                            val enclosingElement : Element = annotation.enclosingElement
                            val elementKind : ElementKind = element.kind
                            val typeElement = element.enclosingElement as TypeElement

                            if (elementKind == ElementKind.METHOD) {
                                val executableElement : ExecutableElement = element as ExecutableElement
                            }

                            var sourceFile = FileUtils.getSourceFile(processingEnv.filer,
                                    packageFromQualifiedName(typeElement.qualifiedName.toString()),
                                    classFromQualifiedName(typeElement.qualifiedName.toString()));

                            var traverser = ASTTraverser();
                            val cu = traverser.loadClassFromSource(sourceFile);

                            val pathToObject = traverser.getPathToObject(cu.first, cu.second);
                            println(pathToObject)

                            FileUtils.generateTestClass(
                                    processingEnv.filer,
                                    getImports(),
                                    getMethods(),
                                    typeElement.simpleName.toString()
                            );

                        }
                )



            }
        )
        return false;
    }

    private fun packageFromQualifiedName(qualifiedName: String): String {
        return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
    }
    private fun classFromQualifiedName(qualifiedName: String): String {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1) + ".java";
    }

    private fun getMethods(): List<MethodDeclaration> {
        val result = mutableListOf<MethodDeclaration>();
        val parseResult = parser.parseMethodDeclaration("public void test() {assertEquals(1, 2);}");

        val methodDeclaration = parseResult.result.orElseThrow();
        methodDeclaration.addAnnotation("Test");


        result.add(methodDeclaration);
        return result;

    }

}