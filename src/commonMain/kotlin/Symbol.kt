package net.liplum.chourse

sealed class Expression

data class BinaryExpression(val operator: String, val left: Expression, val right: Expression) : Expression()
data class UnaryExpression(val operator: String, val right: Expression) : Expression()
data class IntegerLiteralExpression(val value: Int) : Expression()
data class DoubleLiteralExpression(val value: Double) : Expression()
data class StringLiteralExpression(val value: String) : Expression()
data class VariableExpression(val name: String) : Expression()
data class CallExpression(val name: String, val arguments: List<Expression>) : Expression()
object NullLiteralExpression : Expression()

sealed class Statement
data class ExpressionStatement(val expression: Expression) : Statement()
data class PrintStatement(val expression: Expression) : Statement()
data class BlockStatement(val statements: List<Statement>) : Statement()
data class IfStatement(val condition: Expression, val thenStatement: Statement, val elseStatement: Statement?) :
    Statement()

data class WhileStatement(val condition: Expression, val body: Statement) : Statement()
data class ForStatement(
    val initializer: Statement?,
    val condition: Expression?,
    val increment: Expression?,
    val body: Statement
) : Statement()

data class BreakStatement(val label: String?) : Statement()
data class ContinueStatement(val label: String?) : Statement()
data class ReturnStatement(val expression: Expression?) : Statement()

sealed class Declaration
data class VariableDeclaration(val name: String, val expression: Expression?) : Declaration()
data class FunctionDeclaration(val name: String, val parameters: List<String>, val body: BlockStatement) :
    Declaration()

data class ClassDeclaration(val name: String, val superClass: String?, val methods: List<FunctionDeclaration>) :
    Declaration()