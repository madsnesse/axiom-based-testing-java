package no.uib.ii.processors

import com.github.javaparser.ast.type.Type
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver

class Resolver {

    companion object {
        private val typeSolver: JavaParserTypeSolver = JavaParserTypeSolver("src/main/java")

        fun resolveType(type: Type): String {
            var t = typeSolver.tryToSolveType(type.asString())
            return "";
        }
    }

}
