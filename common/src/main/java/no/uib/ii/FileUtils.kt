package no.uib.ii

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import org.json.JSONArray
import org.json.JSONObject
import javax.annotation.processing.Filer
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types
import javax.tools.StandardLocation

class FileUtils(private val filer: Filer) {

    fun writeAxiomsToPropertyFile(axiomDeclarations: MutableMap<String, MutableList<AxiomDefinition>>) {
        var classes = ""
        for ((c,d) in axiomDeclarations) {
            classes += "$c.json\n"
            var ds = ""
            var axiomDeclarations = JSONArray()
            d.forEach {
                var o = JSONObject()
                o.put("isGeneric", it.isGeneric())
                o.put("qualifiedClassName", it.getQualifiedClassName())
                o.put("method", it.getMethod().toString())
                axiomDeclarations.put(o)
            }
            filer.createResource(StandardLocation.SOURCE_OUTPUT, "predefined_axioms", "$c.json")
                .openWriter().use {  writer ->
                    writer.write(axiomDeclarations.toString())
                    writer.close()
                }

        }
        filer.createResource(StandardLocation.SOURCE_OUTPUT, "predefined_axioms", "predefined_index")
            .openWriter().use {  writer ->
                writer.write(classes)
                writer.close()
            }

    }

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
                println("Could not find file: $packageName$fileName") //TODO handle exception
            }
            return s
        }

        fun saveFile(filer: Filer, packageName: String, fileName: String, content: String) {
            var fileObject = filer.createResource(StandardLocation.SOURCE_OUTPUT, packageName, fileName);
            fileObject.openOutputStream().use { outputStream ->
                outputStream.write(content.toByteArray())
            }
        }
        fun getCompilationUnitForTypeElement(typeElement: TypeElement, filer: Filer) : ClassOrInterfaceDeclaration {
            var sourceFile = FileUtils.getSourceFileForTypeElement(typeElement, filer)

            var traverser = ASTTraverser();
            val cu = traverser.loadClassFromSource(sourceFile);

            return cu.second;
        }
        fun getSourceFileForTypeElement(typeElement: TypeElement, filer: Filer): String {

            var sourceFile = FileUtils.getSourceFile(
                filer,
                packageFromQualifiedName(typeElement.qualifiedName.toString()),
                classFromQualifiedName(typeElement.qualifiedName.toString())
            );
            return sourceFile
        }

        fun getCompilationUnitForRelativePath(path: String, filer: Filer) : ClassOrInterfaceDeclaration {
            var sourceFile = FileUtils.getSourceFile(filer, path.substringBeforeLast("."), path.substringAfterLast("."))
            return ASTTraverser().loadClassFromSource(sourceFile).second
        }
        private fun packageFromQualifiedName(qualifiedName: String): String {
            return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
        }
        private fun classFromQualifiedName(qualifiedName: String): String {
            return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1) + ".java";
        }

        fun getAllSourceFilesInClassPath(filer: Filer, typeUtils: Types): List<String> {
            var s = StandardLocation.SOURCE_PATH.name
            var d = javaClass.classLoader

            return emptyList();

        }

        fun writeAxiomsToPropertyFile() {

        }

    }
}