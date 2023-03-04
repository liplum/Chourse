package net.liplum.chourse.transpiler

import net.liplum.chourse.*

class ToSourceVisitor(
    val builder: StringBuilder = StringBuilder()
) : Visitor<Unit> {

    override fun visitLiteral(literal: Literal) {
        when (literal) {
            is StringLiteral -> {
                builder.append('\"')
                escapeString(builder, literal.value)
                builder.append('\"')
                builder.toString()
            }

            else -> builder.append(literal.value)
        }
    }

    override fun visitUnary(expr: UnaryExpr) {
        builder.append(expr.operator)
        expr.right.accept(this)
    }

    override fun visitBinary(expr: BinaryExpr) {
        expr.left.accept(this)
        builder.append(expr.operator)
        expr.right.accept(this)
    }

    override fun visitVar(expr: VariableExpr) {
        builder.append(expr.name)
    }

    override fun visitCall(expr: CallExpr) {
        builder.append(expr.name)
        builder.append('(')
        for ((i, arg) in expr.arguments.withIndex()) {
            arg.accept(this)
            if (i < expr.arguments.size - 1) {
                builder.append(',')
            }
        }
        builder.append(')')
    }

    override fun visitBlock(stmt: BlockStmt) {
        builder.append("{\n")
        for ((i, sub) in stmt.stmts.withIndex()) {
            sub.accept(this)
            if (i < stmt.stmts.size - 1) {
                builder.append('\n')
            }
        }
        builder.append("\n}")
    }

    override fun visitExpr(stmt: ExprStmt) {
        stmt.expr.accept(this)
    }

    override fun visitIf(stmt: IfStmt) {
        builder.append("if(")
        stmt.condition.accept(this)
        builder.append(")")
        stmt.thenStmt.accept(this)
        if (stmt.elseStmt != null) {
            builder.append("else")
            stmt.elseStmt.accept(this)
        }
    }

    override fun visitVarDecl(stmt: VarDecl) {
        builder.append(if (stmt.mutable) "var" else "val")
        builder.append(' ')
        builder.append(stmt.name)
        if (stmt.initializer != null) {
            builder.append("=")
            stmt.initializer.accept(this)
        }
    }

    override fun visitFunDecl(stmt: FuncDecl) {
        builder.append("fun ")
        builder.append(stmt.name)
        builder.append('(')
        for ((i, param) in stmt.parameters.withIndex()) {
            param.accept(this)
            if (i < stmt.parameters.size - 1) {
                builder.append(',')
            }
        }
        builder.append(')')
        stmt.body.accept(this)
    }

    override fun visitClassDecl(stmt: ClassDecl) {
        builder.append("class ")
        builder.append(stmt.name)
        stmt.body.accept(this)
    }

    override fun visitWhile(stmt: WhileStmt) {
        builder.append("while(")
        stmt.condition.accept(this)
        builder.append(')')
        stmt.body.accept(this)
    }

    override fun visitContinue(stmt: ContinueStmt) {
        builder.append("continue")
    }

    override fun visitBreak(stmt: BreakStmt) {
        builder.append("break")
    }

    override fun visitReturn(stmt: ReturnStmt) {
        builder.append("return")
        if (stmt.expr != null) {
            builder.append(' ')
            stmt.expr.accept(this)
        }
    }

    override fun visitParameterDef(def: ParameterDef) {
        builder.append(def.name)
        builder.append(':')
        builder.append(def.type)
    }
}

fun escapeString(builder: StringBuilder, str: CharSequence) {

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
}