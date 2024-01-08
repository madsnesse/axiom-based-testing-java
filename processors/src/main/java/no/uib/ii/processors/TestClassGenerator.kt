package no.uib.ii.processors

import com.github.javaparser.JavaParser
import com.github.javaparser.ParseException
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.SimpleName
import com.google.common.collect.Lists
import no.uib.ii.AxiomDefinition
import no.uib.ii.DataGenerator
import java.io.File
import java.lang.Exception
import java.util.Optional
import javax.annotation.processing.Filer
import kotlin.collections.HashMap

class TestClassGenerator {
    companion object {

        private val parser = JavaParser();
        fun generateTestClassesForAxioms(axiomDeclarations: HashMap<String, List<AxiomDefinition>>, filer: Filer?) {

            var generators: Map<String, List<String>> = getGeneratorsForAxioms(filer!!, axiomDeclarations);

            for (axiomDeclaration in axiomDeclarations) {
                generateTestClass(filer!!, axiomDeclaration.key, axiomDeclaration.value, generators[axiomDeclaration.key]!!)
            }


        }

        private fun getGeneratorsForAxioms(filer : Filer, axiomDeclarations: HashMap<String, List<AxiomDefinition>>): Map<String, List<String>> {
            var result = HashMap<String, List<String>>()
            for ((className,axiomDefinitions) in axiomDeclarations){
                var generators = ArrayList<String>()
                for (axiomDeclaration in axiomDefinitions) {
                    val requiredClasses = getRequiredClasses(axiomDeclaration.getMethod())
                    requiredClasses.forEach { requiredClass ->
                        val clazz = getClassFromClassName(requiredClass);
                        if(clazz.isPresent){
                            if (DataGenerator.hasGeneratorForClass(clazz.get())) {
//                                predefinedGenerators.add(DataGenerator.getGeneratorForClass(clazz.get()));
                                val generator = DataGenerator.getGeneratorForClass(clazz.get()).javaClass
                                generators.add(generator.name)
                            }
                            //generators.add(DataGenerator.generateGeneratorForClass(clazz.get()))
                        } else {
                            val fileName = requiredClass.split(".").last() + ".java"
                            val packageName = requiredClass.split(".").dropLast(1).joinToString(".")
                            val c = FileUtils.getSourceFile(filer, packageName, fileName)
                            val cd = ASTTraverser().loadClassFromSource(c).second
                            //TODO check if exists
                            val f : File = File("target/generated-sources/annotations/no/uib/ii/jaxioms/${cd.nameAsString}Generator.java");
                            if (f.exists()) {
                                f.delete();
                            }
                            //TODO create in test folder instead

                            filer.createSourceFile("no.uib.ii.jaxioms.generators.${cd.nameAsString}Generator").openWriter().use { writer ->
                                writer.write(getDataGenerator(cd).toString())
                            }
                            generators.add("${cd.nameAsString}Generator")
                        }
                    }
                }
                result[className] = generators
            }

            return result;
        }

        private fun getClassFromClassName(requiredClass: String): Optional<Class<* >> {
            return try {
                val c = Class.forName(requiredClass);
                Optional.of(c as Class<*>);
            } catch (e: Exception) {
                println("$requiredClass is not a class")
                Optional.empty();
            }
        }

        private fun getRequiredClasses(method: MethodDeclaration): List<String> {
            var result = HashSet<String>()
            method.parameters.forEach { parameter ->
                result.add(resolveName(parameter))
            }
            return result.toList();

        }

        private fun resolveName(param: Parameter): String {
            val m = param.parentNode.get() as MethodDeclaration
            val c = m.parentNode.get() as ClassOrInterfaceDeclaration
            val p = c.parentNode.get() as CompilationUnit
            return if (p.packageDeclaration.isPresent) {
                p.packageDeclaration.get().nameAsString + "." + param.type.asString()
            }else {
                param.type.asString()
            }
        }

        private fun generateTestClass(
            filer: Filer,
            className: String,
            axiomDeclarations: List<AxiomDefinition>,
            generators: List<String>
        ) {

            var cu: CompilationUnit = CompilationUnit("annotations.no.uib.ii.jaxioms");

            val imports = resolveImports(className, axiomDeclarations, generators)
            val methods: List<MethodDeclaration> = axiomDeclarations.map { axiomDeclaration -> axiomDeclaration.getMethod() }

            imports.forEach(fun(import: ImportDeclaration) {
                    cu.addImport(import)
                })
                val className = className.split(".").last();

                var classDeclaration = cu.addClass("${className}GeneratedTest");
                methods.forEach(fun(method: MethodDeclaration) {
                    classDeclaration.addMember(
                            MethodDeclaration().setBody(method.body.orElseThrow())
                                    .setType(method.type)
                                    .setName(method.name)
                                    .setParameters(method.parameters)
                                    .setModifiers(method.modifiers)
                                    .setStatic(false) //TODO skriv om hvorfor static ikke fungerte, tester ble ikke oppdaget
                                    .setThrownExceptions(method.thrownExceptions)
                                    .addAnnotation("Test")
                    )
                })

//                var b = MethodDeclaration().setBody(StaticJavaParser.parseBlock("""
//                    |System.out.println("Hello world");
//                """.trimMargin()))
//                try{
//                    classDeclaration.addMember(
//                        b.setName("printSomething")
//                            .setType("void")
//                    )
//                }catch (e: Exception) {
//                    println(e)
//                }
//                println(cu.toString())

                //TODO check if exists
                val f : File = File("target/generated-sources/annotations/no/uib/ii/jaxioms/GeneratedTestClass${className}.java");
                if (f.exists()) {
                    f.delete();
                }
                //TODO create in test folder instead

                filer.createSourceFile("no.uib.ii.jaxioms.${className}GeneratedTest").openWriter().use { writer ->
                    writer.write(cu.toString())
                }

            }

        private fun resolveImports(
            className: String,
            axiomDeclarations: List<AxiomDefinition>,
            generators: List<String>
        ): List<ImportDeclaration> {

            val list = mutableListOf<ImportDeclaration>();
            list += (ImportDeclaration("org.junit.jupiter.api.Assertions.assertEquals", true, false));
            list += (ImportDeclaration("org.junit.jupiter.api.Test", false, false));
            //list += (ImportDeclaration("org.junit.jupiter.api.extension.ExtendWith", false, false));
            list += (ImportDeclaration(className, false, false));
            axiomDeclarations.forEach { axDef ->
                axDef.getMethod().parameters.forEach { parameter ->

//                    var import = Resolver.resolveType(parameter.type)
//                    list += ImportDeclaration(import , false, false);
                    //TODO resolve import for custom types
                    //TODO use parameter.type.resolve() maybe

                }

            }
            generators.forEach { generator ->
                list += ImportDeclaration(generator, false, false);
            }

            return list;
        }

        private fun getDataGenerator(clazz: ClassOrInterfaceDeclaration) : CompilationUnit {
            val s = DataGenerator.generateGeneratorForClass(clazz)
            val parseResult = parser.parse(s)
            if (!parseResult.isSuccessful) {
                throw ParseException("Could not parse generated data generator")
            }

            return parseResult.result.get();
        }
    }
}
