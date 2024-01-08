package no.uib.ii.processors

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.tools.StandardLocation

class FileUtils () {

    companion object {
        fun getSourceFile(filer: Filer, packageName: String, fileName: String): String {
            var s = ""
            try {
               var fileObject = filer.getResource(StandardLocation.SOURCE_PATH, packageName, fileName);
               println(fileObject.toString())


               fileObject.openInputStream().use { inputStream ->
                    inputStream.reader().use { reader ->
                        reader.forEachLine { line ->
                            s += line + "\n"
                        }
                    }
                }
            } catch (e: Exception) {
                println(packageName + fileName) //TODO handle exception
            }
            return s
        }

        fun saveFile(filer: Filer, packageName: String, fileName: String, content: String) {
            var fileObject = filer.createResource(StandardLocation.SOURCE_OUTPUT, packageName, fileName);
            fileObject.openOutputStream().use { outputStream ->
                outputStream.write(content.toByteArray())
            }
        }
    }
}