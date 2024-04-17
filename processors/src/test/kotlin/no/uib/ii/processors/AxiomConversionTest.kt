package no.uib.ii.processors

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.Type
import no.uib.ii.AxiomDefinition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import javax.annotation.processing.Filer
import javax.lang.model.element.TypeElement
import kotlin.test.assertTrue

internal class AxiomConversionTest {
    private val typeElement: TypeElement = mock(TypeElement::class.java)
    private val filer: Filer = mock(Filer::class.java)

    private val axiomDefinitions: MutableList<AxiomDefinition> = ArrayList();
    @BeforeEach
    fun setUp() {
        val def = StaticJavaParser.parse(readFileFromResources("ParentWithOneAxiom.java"))
        val parentWithOneAxiom = def.types.first() as ClassOrInterfaceDeclaration
        println(def)
    }

    @Test
    fun convertParentAxiomsEmptyListShouldReturnEmptyList() {
        val ml : List<AxiomDefinition> = emptyList();
        assertTrue { convertParentAxioms(ml, typeElement = typeElement, filer = filer).isEmpty() }
    }

    @Test
    fun convertParentAxiomsListWithOneAxiomShouldReturnEmptyList() {
        val ml: List<AxiomDefinition> = listOf(
            axiomDefinitions[0]
        )
        assertTrue { convertParentAxioms(ml, typeElement = typeElement, filer = filer).isEmpty() }
    }
    @Test
    fun convertTypeInBlockStmtConvertsStringToInteger() {
       val body : BlockStmt = StaticJavaParser.parseBlock("{T s;}")
        val oldType : Type? = StaticJavaParser.parseType("T")
        val newType : Type? = StaticJavaParser.parseType("Integer")
        convertTypeInBlockStmt(body, oldType, newType)
        assertTrue { body.toString().contains("Integer") }
        assertTrue { !body.toString().contains("T") }
    }

    @Test
    fun convertTypeInMehodDeclaration() {
        val method = StaticJavaParser.parseMethodDeclaration("public void method(T t) { return t; }")
        val newType : Type? = StaticJavaParser.parseType("Integer")
        convertTypeInMethod(method, newType)
        assertTrue { method.toString().contains("Integer") }
        assertTrue { !method.toString().contains("T", false) }
    }
}
