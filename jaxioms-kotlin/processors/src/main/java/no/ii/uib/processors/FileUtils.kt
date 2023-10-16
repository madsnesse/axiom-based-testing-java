package no.ii.uib.processors

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.tools.StandardLocation

class FileUtils () {

    //static function
    companion object {


        fun getSourceFile(filer: Filer, packageName: String, fileName: String): String {
            var fileObject = filer.getResource(StandardLocation.SOURCE_PATH, packageName, fileName);
            var s = ""
            try {
                fileObject.openInputStream().use { inputStream ->
                    inputStream.reader().use { reader ->
                        reader.forEachLine { line ->
                            s += line
                        }
                    }
                }
            } catch (e: Exception) {
                println(fileObject.toUri().toString()) //TODO handle exception
            }
            return s
        }

        fun generateTestClass(filer: Filer,
                              imports: List<ImportDeclaration>,
                              methods: List<MethodDeclaration>,
                              nameOfClassToBeTested: String) {

            var cu: CompilationUnit = CompilationUnit("no.uib.ii.jaxioms");
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
    }
}