package net.liplum.chourse.transpiler

import net.liplum.chourse.*

class ToSourceVisitor : Visitor<String> {

    override fun visitLiteral(literal: Literal): String {
        return when (literal) {
            is StringLiteral -> {
                val b = StringBuilder()
                b.append('\"')
                escapeString(b, literal.value)
                b.append('\"')
                b.toString()
            }

            else -> literal.value.toString()
        }
    }

    override fun visitUnary(expr: UnaryExpr): String {
        return "${expr.operator}${expr.right.accept(this)}"
    }

    override fun visitBinary(expr: BinaryExpr): String {
        return "${expr.left.accept(this)} ${expr.operator} ${expr.right.accept(this)}"
    }

    override fun visitVar(expr: VariableExpr): String {
        return expr.name
    }

    override fun visitCall(expr: CallExpr): String {
        return "${expr.name}(${expr.arguments.joinToString(separator = ",") { it.accept(this) }})"
    }

    override fun visitBlock(stmt: BlockStmt): String {
        return "{\n${stmt.stmts.joinToString("\n") { it.accept(this) }}\n}"
    }

    override fun visitExpr(stmt: ExprStmt): String {
        return stmt.expr.accept(this)
    }

    override fun visitIf(stmt: IfStmt): String {
        return if (stmt.elseStmt != null) {
            "if(${stmt.condition.accept(this)})${stmt.thenStmt.accept(this)}else${stmt.elseStmt.accept(this)}"
        } else {
            "if(${stmt.condition.accept(this)})${stmt.thenStmt.accept(this)}"
        }
    }

    override fun visitVarDecl(stmt: VarDecl): String {
        val b = StringBuilder()
        b.append(if (stmt.mutable) "var" else "val")
        b.append(' ')
        b.append(stmt.name)
        if (stmt.initializer != null) {
            b.append("=")
            b.append(stmt.initializer.accept(this))
        }
        return b.toString()
    }

    override fun visitFuncDecl(stmt: FuncDecl): String {
        return "fun ${stmt.name}(${stmt.parameters.joinToString(",") { it.accept(this) }})${stmt.body.accept(this)}"
    }

    override fun visitClassDecl(stmt: ClassDecl): String {
        return "class ${stmt.name}${stmt.body.accept(this)}"
    }

    override fun visitWhile(stmt: WhileStmt): String {
        return "while(${stmt.condition}){\n${stmt.body.accept(this)}}"
    }

    override fun visitContinue(stmt: ContinueStmt): String {
        return if (stmt.label != null) {
            "continue ${stmt.label}"
        } else {
            "continue"
        }
    }

    override fun visitBreak(stmt: BreakStmt): String {
        return if (stmt.label != null) {
            "break ${stmt.label}"
        } else {
            "break"
        }
    }

    override fun visitReturn(stmt: ReturnStmt): String {
        return if (stmt.expr != null) {
            "return ${stmt.expr.accept(this)}"
        } else {
            "return"
        }
    }

    override fun visitParameterDef(def: ParameterDef): String {
        return "${def.name}:${def.type}"
    }
}

fun escapeString(builder: StringBuilder, str: CharSequence): StringBuilder {

    for (char in str) {
        when (char) {
            '\\' -> builder.append("\\\\")
            '\"' -> builder.append("\\\"")
            '\'' -> builder.append("\\\'")
            '\n' -> builder.append("\\n")
            '\r' -> builder.append("\\r")
            '\t' -> builder.append("\\t")
            '\b' -> builder.append("\\b")
            '\u000C' -> builder.append("\\f")
            else -> builder.append(char)
        }
    }

    return builder
}