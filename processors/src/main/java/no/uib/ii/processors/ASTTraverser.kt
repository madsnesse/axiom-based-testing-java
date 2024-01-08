package no.uib.ii.processors

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.resolution.TypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import java.util.Objects

class ASTTraverser {

    val parser = JavaParser();

    fun loadClassFromSource(source: String) : Pair<CompilationUnit, ClassOrInterfaceDeclaration> {
        var result: ClassOrInterfaceDeclaration? = null;
        var cu : CompilationUnit? = null;
        println(source)
        var parseResult = parser.parse(source)
        parseResult.ifSuccessful(fun (c : CompilationUnit) {
            cu = c;
            result = c.findFirst(ClassOrInterfaceDeclaration::class.java).orElse(null);
        })

        if (result == null || cu == null) {
            parseResult.problems.forEach { problem ->
                println(problem)
            }
            throw Exception("Could not parse source")
        }
        return Pair(cu!!, result!!)
    }

    fun findAncestor(cu: ClassOrInterfaceType, predicate: (ClassOrInterfaceType) -> Boolean) : ClassOrInterfaceType? {
        var current = cu;
        while (current != null) {
            if (predicate(current)) {
                return current;
            }
            current = current.findAncestor(ClassOrInterfaceType::class.java).orElse(null);
        }
        return null;
    }

    /*
    * get the path from the current compilation unit to Java.lang.Object
    * */
    fun getPathToObject(cu: CompilationUnit, cd: ClassOrInterfaceDeclaration) : List<ClassOrInterfaceDeclaration> {
        var result = mutableListOf<ClassOrInterfaceDeclaration>();

        var current : ClassOrInterfaceDeclaration? = cd;
        cd.extendedTypes.forEach(fun (type: ClassOrInterfaceType) {
            //TODO parse custom classes that this one extends
            // check if the class that is extended is a custom class which we only have source code for, or part of a library
            // which we only have the class file for

            var name = type.nameWithScope;
            // prepend the package name by retrieving from imports of cd

            name = cd.findCompilationUnit().orElseThrow().imports.stream()
                .filter { import -> import.nameAsString.endsWith(name) }
                .findFirst()
                .map { import -> import.nameAsString }
                .orElse(name);

            var clazz = Class.forName(name);



            println(type.name)
        })

        return result;
    }



}