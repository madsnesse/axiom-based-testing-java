package no.uib.ii.processors

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.type.Type
import no.uib.ii.AxiomDefinition
import no.uib.ii.FileUtils
import no.uib.ii.QualifiedClassName
import no.uib.ii.exceptions.UnexpectedError
import javax.annotation.processing.Filer
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types

fun convertParentAxioms(
    axioms: List<AxiomDefinition>,
    typeElement: TypeElement,
    filer: Filer
): List<AxiomDefinition> {
    val result = ArrayList<AxiomDefinition>();
    axioms.forEach { axiomDefinition ->
        if (!axiomDefinition.isGeneric() &&
            !axiomDefinition.getQualifiedClassName().equals(typeElement.qualifiedName)
        ) {
            val cd = FileUtils.getClassOrInterfaceForTypeElement(typeElement, filer)
            var containsAxiomOwner = false;
            cd.extendedTypes.forEach { classOrInterfaceType ->
                val qualifiedTypeName = StaticJavaParser.parseType(classOrInterfaceType.toString())
                if (qualifiedTypeName.asClassOrInterfaceType().nameWithScope ==
                    (axiomDefinition.getQualifiedClassName().getClassName())
                )
                    containsAxiomOwner = true
            }
            cd.implementedTypes.forEach { classOrInterfaceType ->
                val qualifiedTypeName = StaticJavaParser.parseType(classOrInterfaceType.toString())
                if (qualifiedTypeName.asClassOrInterfaceType().nameWithScope ==
                    (axiomDefinition.getQualifiedClassName().getClassName())
                )
                    containsAxiomOwner = true
            }
            if (!(axiomDefinition.getQualifiedClassName().equalsString(typeElement.qualifiedName.toString()))) {
                if (!axiomDefinition.getQualifiedClassName().equalsString("java.lang.Object") && !containsAxiomOwner) {
                    throw UnexpectedError("Axiom owner is not a parent of the class")
                }
            } //TODO error handling
            val m = axiomDefinition.getMethod()
            val newType = StaticJavaParser.parseType(typeElement.qualifiedName.toString())

            val oldType = StaticJavaParser.parseType(axiomDefinition.getQualifiedClassName().getClassName())

            m.parameters.forEach { p ->
                if (p.type.equals(oldType)) {
                    p.type = newType
                }
            }
            convertTypeInBlockStmt(m.body.orElseThrow(), oldType, newType)
            axiomDefinition.setQualifiedClassName(QualifiedClassName(typeElement.qualifiedName.toString()));

            result.add(axiomDefinition)
        }
    }
    return result
}

fun convertGenericAxioms(
    axioms: MutableList<AxiomDefinition>,
    typeElement: TypeElement,
    typeUtils: Types?
): MutableList<AxiomDefinition> {

    axioms.forEach { axiomDefinition ->
        if (axiomDefinition.isGeneric()) {
            val m = axiomDefinition.getMethod()
            val t = StaticJavaParser.parseType(typeElement.qualifiedName.toString())
            convertTypeInMethod(m, t)
            axiomDefinition.setGeneric(false)
        }
    }
    return axioms
}

/**
* Converts the parameters of the given void method to the new type
* @param m the method to convert
* @param newType the new type to convert to
 * @throws Exception if the return type of the method is not void
*/
fun convertTypeInMethod(
    m: MethodDeclaration,
    newType: Type?
) {
    if (!m.type.isVoidType) throw Exception("Method must be void")
    val body = m.body.orElseThrow()
    m.parameters.forEach {
        val interfaceType = it.type.asClassOrInterfaceType()

        it.type = newType
        convertTypeInBlockStmt(body, interfaceType, newType) //TODO maybe not call this one for each parameter

    }
}

fun convertTypeInBlockStmt(
    body: BlockStmt,
    typeToReplace: Type?,
    newType: Type?
) {
    body.statements.forEach {
        if (it.isExpressionStmt) {
            var e = it.asExpressionStmt()
            if (e.expression.isVariableDeclarationExpr) {
                var v = e.expression.asVariableDeclarationExpr()
                v.variables.forEach { v ->
                    if (v.type.equals(typeToReplace)) {
                        v.type = newType
                        v.initializer.ifPresent({ i ->
                            i.toCastExpr().ifPresent({ c ->
                                c.type = newType
                            })
                        })
                    }

                }
            }
        }
    }
}