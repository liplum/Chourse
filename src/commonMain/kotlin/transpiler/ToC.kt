package net.liplum.chourse.transpiler

import net.liplum.chourse.*

class ToCVisitor : Visitor<StringBuilder> {
    override fun visitLiteral(literal: Literal): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitUnary(expr: UnaryExpr): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitBinary(expr: BinaryExpr): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitVar(expr: VariableExpr): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitCall(expr: CallExpr): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitBlock(stmt: BlockStmt): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitExpr(stmt: ExprStmt): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitIf(stmt: IfStmt): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitVarDecl(stmt: VarDecl): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitFunDecl(stmt: FuncDecl): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitClassDecl(stmt: ClassDecl): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitWhile(stmt: WhileStmt): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitContinue(stmt: ContinueStmt): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitBreak(stmt: BreakStmt): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitReturn(stmt: ReturnStmt): StringBuilder {
        TODO("Not yet implemented")
    }

    override fun visitParameterDef(def: ParameterDef): StringBuilder {
        TODO("Not yet implemented")
    }

}