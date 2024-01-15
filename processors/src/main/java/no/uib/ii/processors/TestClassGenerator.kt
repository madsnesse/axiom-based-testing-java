package no.uib.ii.processors

import com.github.javaparser.JavaParser
import com.github.javaparser.ParseException
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.stmt.BlockStmt
import no.uib.ii.AxiomDefinition
import no.uib.ii.DataGenerator
import no.uib.ii.FileUtils
import no.uib.ii.parser.CommonParserMethods
import java.io.File
import java.lang.Exception
import java.util.Optional
import javax.annotation.processing.Filer
import kotlin.collections.HashMap

class TestClassGenerator {
    companion object {
        private val dataGenerator = DataGenerator()
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
                            val f : File = File("target/generated-sources/annotations/no/uib/ii/jaxioms/generators/${cd.nameAsString}Generator.java");
                            if (f.exists()) {
                                f.delete();
                            }
                            //TODO create in test folder instead
                            var gen = getDataGenerator(cd, filer);
                            filer.createSourceFile("no.uib.ii.jaxioms.generators.${cd.nameAsString}Generator").openWriter().use { writer ->
                                writer.write(gen.toString())
                            }
                            generators.add(gen.getType(0).fullyQualifiedName.get())
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

        private fun getDebugMethod() : BodyDeclaration<*>? {
            var d = "   @BeforeAll\n" +
                    "    public static void t() {\n" +
                    "        System.out.println(\"fei8has\");\n" +
                    "    }"

            return CommonParserMethods.parseOrException(parser.parseMethodDeclaration(d), "error")
        }
        private fun getStreamMethod(className: String): BodyDeclaration<*>? {

            var s = "public static Stream<Arguments> method(){\n" +
                    "        Generator<Clazz> clazzGenerator = new ClazzGenerator();\n" +
                    "        List<Arguments> clazzStream = new ArrayList(); \n" +
                    "        for (int i = 0; i < NUMBER_OF_CASES; i++) {\n" +
                    "            clazzStream.add(Arguments.of(clazzGenerator.generate(), clazzGenerator.generate(), clazzGenerator.generate()));\n" + //TODO dont have fixed number
                    "        }\n" +
                    "        System.out.println(clazzStream); \n"+
                    "        return clazzStream.stream();\n" +
                    "    }"
            s = s.replace("Clazz", className)
            s = s.replace("NUMBER_OF_CASES", "100"); //TODO replace with value for 100
            var body = CommonParserMethods.parseOrException(parser.parseMethodDeclaration(s), "could not parse")
            return body
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
                                .addAnnotation("ParameterizedTest")
                            .addSingleMemberAnnotation("MethodSource", "\"method\"")
                )
            })
            classDeclaration.addMember(
                getStreamMethod(className)
            )
            //classDeclaration.addMember(getDebugMethod())

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
            list += (ImportDeclaration("org.junit.jupiter.params.ParameterizedTest", false, false));
            list += (ImportDeclaration("org.junit.jupiter.api.BeforeAll", false, false));
            list += (ImportDeclaration("org.junit.jupiter.params.provider.Arguments", false, false));
            list += (ImportDeclaration("org.junit.jupiter.params.provider.MethodSource", false, false));
            list += (ImportDeclaration("java.util.ArrayList", false, false));
            list += (ImportDeclaration("java.util.List", false, false));
            list += (ImportDeclaration("java.util.stream.Stream", false, false));
            list += (ImportDeclaration("no.uib.ii.defaultgenerators.Generator", false, false));
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

        private fun getDataGenerator(clazz: ClassOrInterfaceDeclaration, filer: Filer) : CompilationUnit {
            val s = dataGenerator.generateGeneratorForClass(clazz, filer)
            val parseResult = parser.parse(s)
            if (!parseResult.isSuccessful) {
                throw ParseException("Could not parse generated data generator")
            }

            return parseResult.result.get();
        }
    }
}
