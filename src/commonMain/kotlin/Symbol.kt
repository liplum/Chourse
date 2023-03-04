package net.liplum.chourse

sealed interface Expr {
    fun <R> accept(visitor: ExprVisitor<R>): R
}

sealed interface Literal : Expr
data class BinaryExpr(val left: Expr, val operator: String, val right: Expr) : Expr {
    override fun <R> accept(visitor: ExprVisitor<R>): R {
        return visitor.visitBinary(this)
    }
}

data class UnaryExpr(val operator: String, val right: Expr) : Expr {
    override fun <R> accept(visitor: ExprVisitor<R>): R {
        return visitor.visitUnary(this)
    }
}

data class IntegerLiteral(val value: Int) : Literal {
    override fun <R> accept(visitor: ExprVisitor<R>): R {
        return visitor.visitLiteral(this)
    }
}

data class DoubleLiteral(val value: Double) : Literal {
    override fun <R> accept(visitor: ExprVisitor<R>): R {
        return visitor.visitLiteral(this)
    }
}

data class StringLiteral(val value: String) : Literal {
    override fun <R> accept(visitor: ExprVisitor<R>): R {
        return visitor.visitLiteral(this)
    }
}

object NullLiteralExpr : Literal {
    override fun <R> accept(visitor: ExprVisitor<R>): R {
        return visitor.visitLiteral(this)
    }
}


data class VariableExpr(val name: String) : Expr {
    override fun <R> accept(visitor: ExprVisitor<R>): R {
        return visitor.visitVar(this)
    }
}

data class CallExpr(val name: String, val arguments: List<Expr>) : Expr {
    override fun <R> accept(visitor: ExprVisitor<R>): R {
        return visitor.visitCall(this)
    }
}


sealed interface Stmt {
    fun <R> accept(visitor: StmtVisitor<R>): R
}

data class ExprStmt(val expr: Expr) : Stmt {
    override fun <R> accept(visitor: StmtVisitor<R>): R {
        return visitor.visitExpr(this)
    }
}

data class BlockStmt(val stmts: List<Stmt>) : Stmt {
    override fun <R> accept(visitor: StmtVisitor<R>): R {
        return visitor.visitBlock(this)
    }
}

data class IfStmt(
    val condition: Expr,
    val thenStmt: Stmt,
    val elseStmt: Stmt?,
) : Stmt {
    override fun <R> accept(visitor: StmtVisitor<R>): R {
        return visitor.visitIf(this)
    }
}

data class WhileStmt(
    val condition: Expr,
    val body: Stmt,
) : Stmt {
    override fun <R> accept(visitor: StmtVisitor<R>): R {
        return visitor.visitWhile(this)
    }
}

data class BreakStmt(val label: String?) : Stmt {
    override fun <R> accept(visitor: StmtVisitor<R>): R {
        return visitor.visitBreak(this)
    }
}

data class ContinueStmt(val label: String?) : Stmt {
    override fun <R> accept(visitor: StmtVisitor<R>): R {
        return visitor.visitContinue(this)
    }
}

data class ReturnStmt(val expr: Expr?) : Stmt {
    override fun <R> accept(visitor: StmtVisitor<R>): R {
        return visitor.visitReturn(this)
    }
}

data class VarDecl(
    val name: String,
    val initializer: Expr?
) : Stmt {
    override fun <R> accept(visitor: StmtVisitor<R>): R {
        return visitor.visitVarDecl(this)
    }
}

data class ParameterDef(
    val name: String,
    val type: String,
)

data class FuncDecl(
    val name: String,
    val parameters: List<ParameterDef>,
    val body: BlockStmt
) : Stmt {
    override fun <R> accept(visitor: StmtVisitor<R>): R {
        return visitor.visitFuncDecl(this)
    }
}

data class ClassDecl(
    val name: String,
    val superClass: String?,
    val body: BlockStmt
) : Stmt {
    override fun <R> accept(visitor: StmtVisitor<R>): R {
        return visitor.visitClassDecl(this)
    }
}
