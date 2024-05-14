package no.uib.ii.processors

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.MethodDeclaration
import no.uib.ii.AxiomDefinition
import no.uib.ii.DataGenerator
import no.uib.ii.FileUtils
import no.uib.ii.QualifiedClassName
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.tools.Diagnostic


@SupportedAnnotationTypes(
    "no.uib.ii.annotations.Axiom",
    "no.uib.ii.annotations.InheritAxioms",
    "no.uib.ii.annotations.AxiomForExistingClass",
    "no.uib.ii.annotations.DefinedGenerator"
)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class AxiomProcessor : AbstractProcessor() {

    private val parser = JavaParser();
    private val dataGenerator = DataGenerator();
    private var testClassGenerator = TestClassGenerator(dataGenerator);
    private var axiomDeclarations: MutableMap<String, MutableList<AxiomDefinition>> = HashMap();
    private var generatorProcessing = GeneratorProcessing(dataGenerator);
    private fun loadAxiomsFromFiles(): MutableMap<String, MutableList<AxiomDefinition>> {
        val result: MutableMap<String, MutableList<AxiomDefinition>> = axiomDeclarations;
         val index = this.javaClass.classLoader.getResource("META-INF/predefined_axioms/predefined_index")
                ?.readText().orEmpty()
        for (line: String in index.split("\n")) {
            if (line.isBlank()) continue
            val file = this.javaClass.classLoader.getResource(
                "META-INF/predefined_axioms/${
                    line.replace(
                        " ",
                        ""
                    )
                }"
            )
                ?.readText().orEmpty()
            if (file.isNotEmpty()) {
                val jsonArray = JSONArray(file)
                val existing = axiomDeclarations.getOrDefault(line, ArrayList())
                for (axiomdecl in jsonArray) {
                    val decl: JSONObject = axiomdecl as JSONObject
                    parser.parseMethodDeclaration(decl["method"].toString()).result.ifPresent { md ->
                        existing.add(
                            AxiomDefinition(
                                md,
                                decl.get("isGeneric") as Boolean,
                                QualifiedClassName(decl.get("qualifiedClassName") as String)
                            )
                        )
                    }
                }
                result[line.substringBefore(".json")] = existing
            }
        }
        return result
    }

    // mapping classnames to a list of all axioms to be applied to a class

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        val predefinedAxioms = loadAxiomsFromFiles()
        val fileUtils = FileUtils(processingEnv.filer)
        val jsonGenerators = loadGeneratorMap()
        dataGenerator.addGenerators(jsonGenerators)
        axiomDeclarations = predefinedAxioms;
        annotations?.forEach(
            fun(annotation: TypeElement) {
                val elementsAnnotatedWith = roundEnv?.getElementsAnnotatedWith(annotation)
                try{
                    when (annotation.toString()) {
                        "no.uib.ii.annotations.AxiomForExistingClass" -> {
                            UserDefinedProcessing.processAxiomForExistingClass(
                                elementsAnnotatedWith,
                                processingEnv.filer,
                                processingEnv.typeUtils,
                                axiomDeclarations
                            )
                        }
                        "no.uib.ii.annotations.Axiom" -> {
                            UserDefinedProcessing.processAxiom(
                                elementsAnnotatedWith,
                                processingEnv.filer,
                                processingEnv.typeUtils,
                                axiomDeclarations
                            )
                        }
                        "no.uib.ii.annotations.InheritAxioms" -> {
                            UserDefinedProcessing.applyAxiomsFromParent(
                                elementsAnnotatedWith,
                                processingEnv.filer,
                                processingEnv.typeUtils,
                                axiomDeclarations
                            )
                        }
                        "no.uib.ii.annotations.DefinedGenerator" -> {
                            generatorProcessing.processGenerator(
                                elementsAnnotatedWith,
                                processingEnv.filer,
                                processingEnv.typeUtils,
                                axiomDeclarations
                            )
                        }
                    }
                }catch (e: Exception){
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.WARNING,
                        "Error processing annotation ${annotation.simpleName}: ${e.message}")
                }
            }
        )
        if (roundEnv?.processingOver()!!) {
            testClassGenerator.generateTestClassesForAxioms(axiomDeclarations, processingEnv.filer)
            fileUtils.writeGeneratorList(dataGenerator.availableGenerators)
            fileUtils.writeAxiomsToPropertyFile(axiomDeclarations)
        }
        return false
    }

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
    }

    private fun loadGeneratorMap(): Map<String, String> {
        val result: MutableMap<String, String> = HashMap();
        try {
            val index = AxiomProcessor::class.java.classLoader.getResource("META-INF/predefined_axioms/generator_index")
                    ?.readText().orEmpty()
            if (index.isEmpty()) {
                return result
            }
            val jsonObject = JSONObject(index)
            for (key in jsonObject.keys()) {
                result[key] = jsonObject.getString(key)
            }
        } catch (e: NullPointerException) {
            println("nothing to index")
        } catch (e: FileNotFoundException) {
            println("nothing to index")
        } catch (e: NoSuchElementException) {
            println(e)
        }

        return result
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