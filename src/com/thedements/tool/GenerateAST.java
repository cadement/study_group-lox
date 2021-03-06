package com.thedements.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAST {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(1);
        }
        
        String outputDir = args[0];
        
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"
        ));
    }
    
    private static void defineAst(String outputDir,
                                  String baseName,
                                  List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        
        writer.println("package com.thedements.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");
        
        defineVisitor(writer, baseName, types);
        
        // The base accept() method.
        writer.println("\t");
        writer.println("\tabstract <R> R accept(Visitor<R> visitor);");
        
        // The AST classes.
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }
        
        writer.println("}");
        writer.close();
    }
    
    private static void defineVisitor(PrintWriter writer,
                                      String baseName,
                                      List<String> types) {
        writer.println("\t");
        writer.println("\tinterface Visitor<R> {");
        
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("\t\tR visit(" + typeName + " " + baseName.toLowerCase() + ");");
        }
        
        writer.println("\t}");
    }
    
    private static void defineType(PrintWriter writer,
                                   String baseName,
                                   String className,
                                   String fieldList) {
        writer.println("\t");
        writer.println("\tstatic class " + className + " extends " + baseName + " {");
        
        
        String[] fields = fieldList.split(", ");
        
        // Fields.
        for (String field : fields) {
            writer.println("\t\tfinal " + field + ";");
        }
        writer.println();
        // Constructor.
        writer.println("\t\t" + className + "(" + fieldList + ") {");
        
        // Store parameters in fields.
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("\t\t\tthis." + name + " = " + name + ";");
        }
        
        writer.println("\t\t}");
        
        // Visitor pattern.
        writer.println("\t\t");
        writer.println("\t\t<R> R accept(Visitor<R> visitor) {");
        writer.println("\t\t\treturn visitor.visit(this);");
        writer.println("\t\t}");
        
        writer.println("\t}");
    }
}
