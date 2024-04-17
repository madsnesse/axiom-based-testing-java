package no.uib.ii.processors

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

class Visitor : VoidVisitorAdapter<Void>() {

    override fun visit(md: ClassOrInterfaceDeclaration, arg: Void) {
        super.visit(md, arg);
        println(md.name);
    }
}