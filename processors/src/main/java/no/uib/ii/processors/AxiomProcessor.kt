package no.uib.ii.processors

import autovalue.shaded.com.google.auto.service.AutoService
import com.github.javaparser.JavaParser
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.Parameter
import no.uib.ii.AxiomDefinition
import no.uib.ii.DataGenerator
import no.uib.ii.FileUtils
import javax.annotation.processing.*
import javax.annotation.processing.Processor
import javax.lang.model.SourceVersion
import javax.lang.model.element.*


@SupportedAnnotationTypes("no.uib.ii.Axiom")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor::class)
class AxiomProcessor : AbstractProcessor() {

    private val parser = JavaParser();

    // mapping classnames to a list of all axioms to be applied to a class
    private val axiomDeclarations = HashMap<String, List<AxiomDefinition>>()

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
        var hasGenerated = false;
        annotations?.forEach(
            fun(annotation) {
                roundEnv?.getElementsAnnotatedWith(annotation)?.forEach(
                        fun (element: Element) {
                            val enclosingElement : Element = annotation.enclosingElement
                            val elementKind : ElementKind = element.kind
                            val typeElement = element.enclosingElement as TypeElement

                            if (elementKind != ElementKind.METHOD) {
                                //TODO maybe throw exception if annotation is not on a method
                                return;
                            }
                            val methodName = (element as ExecutableElement).simpleName.toString();
                            val parameters = element.parameters;

                            var sourceFile = FileUtils.getSourceFile(
                                processingEnv.filer,
                                packageFromQualifiedName(typeElement.qualifiedName.toString()),
                                classFromQualifiedName(typeElement.qualifiedName.toString())
                            );

                            var traverser = ASTTraverser();
                            val cu = traverser.loadClassFromSource(sourceFile);

                            var methodDeclaration : MethodDeclaration? = null;
                            //TODO extract this to a function
                            //var decl : ClassOrInterfaceDeclaration = cu.second;
                            //var d = DataGenerator.generateGeneratorForClass(decl);

                            cu.second.methods.forEach(fun (method: MethodDeclaration) {
                                if (method.nameAsString == methodName) {
                                    var allContains = true
                                    if (allParametersAreEqual(method.parameters, parameters)) {
                                        methodDeclaration = method;
                                    }
                                }
                            })
                            if (methodDeclaration == null) {
                                //TODO throw exception
                                return;
                            }
                            val existingAxiomsForClass = axiomDeclarations.getOrDefault(typeElement.qualifiedName.toString(),
                                    ArrayList());
                            axiomDeclarations[typeElement.qualifiedName.toString()] = existingAxiomsForClass.plus(
                                    AxiomDefinition(
                                            methodDeclaration!!
                                    )
                            )
                            hasGenerated = true;

                        }
                )



            }
        )

        if (hasGenerated){
            TestClassGenerator.generateTestClassesForAxioms(axiomDeclarations, processingEnv.filer)
        };

        return true;
    }

    private fun allParametersAreEqual(parameters: NodeList<Parameter>?, parameters1: List<VariableElement>): Boolean {
        if (parameters == null) {
            return false;
        }
        if (parameters.size != parameters1.size) {
            return false;
        }
        for (i in 0 until parameters.size) {
            var p = parameters1[i].asType().toString().split(".").last(); //TODO: maybe not do this so hacky
            val p1 = parameters[i].type.toString()
            if (p1 != p) {
                return false;
            }
        }
        return true;

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