package net.liplum.chourse

interface ExprVisitor<R> {
    fun visitLiteral(literal: Literal): R
    fun visitUnary(expr: UnaryExpr): R
    fun visitBinary(expr: BinaryExpr): R
    fun visitVar(expr: VariableExpr): R
    fun visitCall(expr: CallExpr): R
}

interface StmtVisitor<R> {
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
}