package no.uib.ii

import com.github.javaparser.ast.body.MethodDeclaration

class AxiomDefinition (private val method : MethodDeclaration,
                       private var generic: Boolean = false,
                       private var qualifiedClassName: QualifiedClassName,
                       private var generate : Boolean = false){
    fun getMethod() : MethodDeclaration {
        return method;
    }

    fun getQualifiedClassName() : QualifiedClassName {
        return qualifiedClassName;
    }

    fun setQualifiedClassName(s: QualifiedClassName) {
        qualifiedClassName = s
    }

    fun isGeneric() : Boolean {
        return generic
    }

    fun setGeneric(b: Boolean) {
        generic = b
    }

    fun copy(): AxiomDefinition {
        return AxiomDefinition(method.clone(), generic, qualifiedClassName.copy(), generate)
    }


}