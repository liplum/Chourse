package net.liplum.chourse

sealed class Expr

data class BinaryExpr(val operator: String, val left: Expr, val right: Expr) : Expr()
data class UnaryExpr(val operator: String, val right: Expr) : Expr()
data class IntegerLiteral(val value: Int) : Expr()
data class DoubleLiteral(val value: Double) : Expr()
data class StringLiteral(val value: String) : Expr()
data class VariableExpr(val name: String) : Expr()
data class CallExpr(val name: String, val arguments: List<Expr>) : Expr()
data class AssignmentExpr(val address: String, val value: Expr) : Expr()
object NullLiteralExpr : Expr()

sealed interface Stmt
data class ExpressionStmt(val expr: Expr) : Stmt
data class BlockStmt(val stmts: List<Stmt>) : Stmt
data class IfStmt(
    val condition: Expr,
    val thenStmt: Stmt,
    val elseStmt: Stmt?,
) : Stmt

data class WhileStmt(
    val condition: Expr,
    val body: Stmt,
) : Stmt

data class BreakStmt(val label: String?) : Stmt
data class ContinueStmt(val label: String?) : Stmt
data class ReturnStmt(val expr: Expr?) : Stmt

data class VariableDecl(
    val name: String,
    val initializer: Expr?
) : Stmt

data class FunctionDecl(
    val name: String,
    val parameters: List<String>,
    val body: BlockStmt
) : Stmt

data class ClassDecl(
    val name: String,
    val superClass: String?,
    val body: BlockStmt
) : Stmt