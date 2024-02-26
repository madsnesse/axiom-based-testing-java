package no.uib.ii.processors

import autovalue.shaded.com.google.auto.service.AutoService
import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.MethodDeclaration
import no.uib.ii.AxiomDefinition
import no.uib.ii.FileUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.lang.NullPointerException
import java.util.NoSuchElementException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import kotlin.collections.ArrayList


@SupportedAnnotationTypes("no.uib.ii.annotations.Axiom", "no.uib.ii.annotations.InheritAxioms", "no.uib.ii.annotations.AxiomForExistingClass")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor::class)
class AxiomProcessor : AbstractProcessor() {

    private val parser = JavaParser();

    private var axiomDeclarations : MutableMap<String, MutableList<AxiomDefinition>> = HashMap();

    private fun loadAxiomsFromFiles(): MutableMap<String, MutableList<AxiomDefinition>> {
        val result : MutableMap<String, MutableList<AxiomDefinition>> = axiomDeclarations;
        try {
            var index = AxiomProcessor::class.java.classLoader.getResource("META-INF/predefined_axioms/predefined_index")?.readText().orEmpty()
            println("index: $index")
            for ( line : String in index.split("\n")) {
                if (line.isBlank()) continue
                var file = AxiomProcessor::class.java.classLoader.getResource("META-INF/predefined_axioms/${line.replace(" ","")}")
                    ?.readText().orEmpty()
                if(file.isNotEmpty()){
                    var jsonArray = JSONArray(file)
                    var existing = axiomDeclarations.getOrDefault(line, ArrayList())
                    for (axiomdecl in jsonArray) {
                        val decl : JSONObject = axiomdecl as JSONObject
                        parser.parseMethodDeclaration(decl["method"].toString()).result.ifPresent { md ->
                            existing.add(AxiomDefinition(md, decl.get("isGeneric") as Boolean))
                        }

                    }
                    result[line.substringBefore(".json")] = existing
                }
            }
//            var r = processingEnv.filer.getResource(StandardLocation.SOURCE_PATH, "", "predefined_index")
//            var classes : List<String>
//            r.openReader(true).use { reader -> classes = reader.readLines() }
//            var cs = ArrayList<AxiomDefinition>()
//            classes.forEach { line ->
//                var l : List<String>
//                processingEnv.filer.getResource(StandardLocation.CLASS_OUTPUT, "predefined_axioms", line)
//                    .openReader(true).use { reader -> l = reader.readLines() }
//                l
//            }
        } catch (e : NullPointerException) {
            println("nothing to index")
        } catch (e: FileNotFoundException) {
            println("nothing to index")
        } catch (e: NoSuchElementException) {
            println(e)
        }

        return result
    }

    // mapping classnames to a list of all axioms to be applied to a class

//    private fun getImports(): List<ImportDeclaration> {
//        val result = mutableListOf<ImportDeclaration>();
//        result.add(parseOrException(parser.parseImport("import org.junit.jupiter.api.Test;"), "error parsing import, contact maintainer"));
//        result.add(parseOrException(parser.parseImport("import static org.junit.jupiter.api.Assertions.assertEquals;"),"error parsing import, contact maintainer"));
//        return result;
//    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        val predefinedAxioms = loadAxiomsFromFiles()
        val fileUtils = FileUtils(processingEnv.filer)
        axiomDeclarations = predefinedAxioms;
        annotations?.forEach(
            fun(annotation: TypeElement) {
                val elementsAnnotatedWith = roundEnv?.getElementsAnnotatedWith(annotation)
                when (annotation.toString()) {
                    "no.uib.ii.annotations.AxiomForExistingClass" ->
                        UserDefinedProcessing.processAxiomForExistingClass(
                            elementsAnnotatedWith,
                            processingEnv.filer,
                            processingEnv.typeUtils,
                            axiomDeclarations)
                    "no.uib.ii.annotations.Axiom" ->
                        UserDefinedProcessing.processAxiom(
                            elementsAnnotatedWith,
                            processingEnv.filer,
                            processingEnv.typeUtils,
                            axiomDeclarations)
                    "no.uib.ii.annotations.InheritAxioms" ->
                        //TODO if this annotation present, apply axioms from parent(s)
                        UserDefinedProcessing.applyAxiomsFromParent(
                            elementsAnnotatedWith,
                            processingEnv.typeUtils,
                            axiomDeclarations)
//            Group::class.qualifiedName ->
//                axiomDeclarations = AlgebraicStructureProcessing.process(annotation, elementsAnnotatedWith, processingEnv.filer)
                }
            }
        )

//        if (hasGenerated){
//            TestClassGenerator.generateTestClassesForAxioms(axiomDeclarations, processingEnv.filer)
//        };


        if (roundEnv?.processingOver()!!){
            TestClassGenerator.generateTestClassesForAxioms(axiomDeclarations, processingEnv.filer)
            fileUtils.writeAxiomsToPropertyFile(axiomDeclarations)
        }
        return true
    }




    private fun processExistingAxiom(elementsAnnotatedWith: Set<Element>?): Map<String, List<AxiomDefinition>> {
        println(elementsAnnotatedWith)
        //TODO
        return HashMap()
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