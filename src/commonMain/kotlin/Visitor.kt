package net.liplum.chourse

interface Visitor<R> {
    fun visitLiteral(literal: Literal): R
    fun visitUnary(expr: UnaryExpr): R
    fun visitBinary(expr: BinaryExpr): R
    fun visitVar(expr: VariableExpr): R
    fun visitCall(expr: CallExpr): R
    fun visitBlock(stmt: BlockStmt): R
    fun visitExpr(stmt: ExprStmt): R
    fun visitIf(stmt: IfStmt): R
    fun visitVarDecl(stmt: VarDecl): R
    fun visitFuncDecl(stmt: FuncDecl): R
    fun visitClassDecl(stmt: ClassDecl): R
    fun visitWhile(stmt: WhileStmt): R
    fun visitContinue(stmt: ContinueStmt): R
    fun visitBreak(stmt: BreakStmt): R
    fun visitReturn(stmt: ReturnStmt): R
    fun visitParameterDef(def: ParameterDef): R
}