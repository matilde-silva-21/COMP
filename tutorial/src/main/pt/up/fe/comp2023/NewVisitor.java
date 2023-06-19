package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;

import java.util.HashMap;
import java.util.Map;

public class NewVisitor extends AJmmVisitor<String, String> {
    private String className;
    private Map<String, Integer> writes;
    private Map<String, Integer> reads;

    public NewVisitor(String className) {
        this.className = className;
        this.writes = new HashMap<>();
        this.reads = new HashMap<>();
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

        System.out.println("Writes:");
        for (Map.Entry<String, Integer> entry : writes.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        System.out.println("Reads:");
        for (Map.Entry<String, Integer> entry : reads.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        return ret;
    }

    private String dealWithAssignment(JmmNode jmmNode, String s) {
        if (writes.containsKey(jmmNode.get("var"))){
            writes.put(jmmNode.get("var"), writes.get(jmmNode.get("var")) + 1);
        } else{
            writes.put(jmmNode.get("var"), 1);
        }
        return s + "int " + jmmNode.get("var") + " = " + visit(jmmNode.getJmmChild(0), "") + ";";
    }

    private String dealWithLiteral(JmmNode jmmNode, String s) {
        if (jmmNode.getKind().equals("Identifier")){
            if (reads.containsKey(jmmNode.get("value"))){
                reads.put(jmmNode.get("value"), reads.get(jmmNode.get("value")) + 1);
            } else{
                reads.put(jmmNode.get("value"), 1);
            }
        }
        return s + jmmNode.get("value");
    }
}