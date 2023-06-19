package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;

public class JavaCalcGenerator extends AJmmVisitor<String, String> {
    private String className;

    public JavaCalcGenerator(String className) {
        this.className = className;
    }

    protected void buildVisitor() {
        addVisit("Program", this::dealWithProgram);
        addVisit("Assignment", this::dealWithAssignment);
        addVisit("Integer", this::dealWithLiteral);
        addVisit("Identifier", this::dealWithLiteral);
        addVisit("ExprStmt", this::dealWithExprStmt);
        addVisit("BinaryOp", this::dealWithBinaryOp);
    }

    private String dealWithBinaryOp(JmmNode jmmNode, String s) {
        JmmNode lhs = jmmNode.getJmmChild(0);
        JmmNode rhs = jmmNode.getJmmChild(1);

        String lres = visit(lhs, "");
        String rres = visit(rhs, "");

        String operator = jmmNode.get("op").replace("'", "");
        if (jmmNode.hasAttribute("open")){
            return "(" + lres + " " + operator + " " + rres + ")";
        } else{
            return lres + " " + operator + " " + rres;
        }

    }

    private String dealWithExprStmt(JmmNode jmmNode, String s) {
        return s + "System.out.println(" + visit(jmmNode.getJmmChild(0), "") + ");\n";
    }

    private String dealWithProgram(JmmNode jmmNode, String s) {
        s = (s != null ? s : "");
        String ret = s + "public class " + this.className + " {\n";
        String s2 = s + "\t";
        ret += s2 + "public static void main(String[] args) {\n";

        for (JmmNode child : jmmNode.getChildren()) {
            ret += visit(child, s2 + "\t");
            ret += "\n";
        }

        ret += s2 + "}\n";
        ret += s + "}\n";
        return ret;
    }

    private String dealWithAssignment(JmmNode jmmNode, String s) {
        return s + "int " + jmmNode.get("var") + " = " + visit(jmmNode.getJmmChild(0), "") + ";";
    }

    private String dealWithLiteral(JmmNode jmmNode, String s) {
        return s + jmmNode.get("value");
    }
}