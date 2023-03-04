package net.liplum.chourse

interface Visitor<R> {
    fun visitLiteralExpr(literal: Literal): R
    fun visitUnaryExpr(expr: UnaryExpr): R
    fun visitBinaryExpr(expr: BinaryExpr): R
    fun visitVarExpr(expr: VariableExpr): R
    fun visitCallExpr(expr: CallExpr): R
    fun visitBlockStmt(stmt: BlockStmt): R
    fun visitExprStmt(stmt: ExprStmt): R
    fun visitIfStmt(stmt: IfStmt): R
    fun visitVarDecl(stmt: VarDecl): R
    fun visitFunDecl(stmt: FuncDecl): R
    fun visitClassDecl(stmt: ClassDecl): R
    fun visitWhileStmt(stmt: WhileStmt): R
    fun visitContinueStmt(stmt: ContinueStmt): R
    fun visitBreakStmt(stmt: BreakStmt): R
    fun visitReturnStmt(stmt: ReturnStmt): R
    fun visitParameterDef(def: ParameterDef): R
}