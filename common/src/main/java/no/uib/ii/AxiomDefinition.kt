package no.uib.ii

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration

class AxiomDefinition (private val method : MethodDeclaration,
                       private var generic: Boolean = false,
                       private var generate : Boolean = false){
    fun getMethod() : MethodDeclaration {
        return method;
    }

    fun isGeneric() : Boolean {
        return generic
    }

    fun setGeneric(b: Boolean) {
        generic = b
    }


}