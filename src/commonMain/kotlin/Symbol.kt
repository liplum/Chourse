package net.liplum.chourse

sealed interface Symbol {
    fun <R> accept(visitor: Visitor<R>): R
}

sealed interface Expr : Symbol

sealed interface Literal : Expr {
    val value: Any?
}

data class BinaryExpr(val left: Expr, val operator: String, val right: Expr) : Expr {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitBinaryExpr(this)
    }
}

data class UnaryExpr(val operator: String, val right: Expr) : Expr {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitUnaryExpr(this)
    }
}

data class IntegerLiteral(override val value: Int) : Literal {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitLiteralExpr(this)
    }
}

data class DoubleLiteral(override val value: Double) : Literal {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitLiteralExpr(this)
    }
}

data class StringLiteral(override val value: String) : Literal {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitLiteralExpr(this)
    }
}


object NullLiteralExpr : Literal {
    override val value = null

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitLiteralExpr(this)
    }
}


data class VariableExpr(val name: String) : Expr {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitVarExpr(this)
    }
}

data class CallExpr(val name: String, val arguments: List<Expr>) : Expr {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitCallExpr(this)
    }
}


sealed interface Stmt : Symbol

data class ExprStmt(val expr: Expr) : Stmt {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitExprStmt(this)
    }
}

data class BlockStmt(val stmts: List<Stmt>) : Stmt {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitBlockStmt(this)
    }
}

data class IfStmt(
    val condition: Expr,
    val thenStmt: Stmt,
    val elseStmt: Stmt?,
) : Stmt {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitIfStmt(this)
    }
}

data class WhileStmt(
    val condition: Expr,
    val body: Stmt,
) : Stmt {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitWhileStmt(this)
    }
}

data class BreakStmt(val label: String?) : Stmt {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitBreakStmt(this)
    }
}

data class ContinueStmt(val label: String?) : Stmt {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitContinueStmt(this)
    }
}

data class ReturnStmt(val label: String?, val expr: Expr?) : Stmt {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitReturnStmt(this)
    }
}

data class VarDecl(
    val name: String,
    val mutable: Boolean,
    val initializer: Expr?
) : Stmt {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitVarDecl(this)
    }
}

data class ParameterDef(
    val name: String,
    val type: String,
) : Symbol {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitParameterDef(this)
    }

}

data class FuncDecl(
    val name: String,
    val parameters: List<ParameterDef>,
    val body: BlockStmt
) : Stmt {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitFunDecl(this)
    }
}

data class ClassDecl(
    val name: String,
    val superClass: String?,
    val body: BlockStmt
) : Stmt {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitClassDecl(this)
    }
}
