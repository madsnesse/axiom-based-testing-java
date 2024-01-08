package no.uib.ii

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration

class AxiomDefinition (private val method : MethodDeclaration){
    fun getMethod() : MethodDeclaration {
        return method;
    }
}