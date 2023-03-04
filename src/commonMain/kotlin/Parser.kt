package net.liplum.chourse

class ParserException(message: String?) : Exception(message) {
    constructor(token: Token, message: String? = null) : this(
        if (token.type == TokenType.Eof) {
            "[line ${token.line}] Error at end: $message"
        } else {
            "[line ${token.line}] Error at \"${token.lexeme}\": $message"
        }
    )
}

class Parser(private val tokens: List<Token>) {
    private var current = 0
    private val currentToken get() = tokens[current]
    private val restTokens get() = tokens.subList(current, tokens.size)
    private val operatorPrecedence = mapOf(
        TokenType.LParen to 0,
        TokenType.RParen to 0,
        TokenType.LIndex to 0,
        TokenType.RIndex to 0,
        TokenType.Dot to 0,
        TokenType.Plus to 2,
        TokenType.Minus to 2,
        TokenType.Tilde to 3,
        TokenType.Not to 3,
        TokenType.Times to 4,
        TokenType.Divide to 4,
        TokenType.Modulo to 4,
        TokenType.LShift to 5,
        TokenType.RShift to 5,
        TokenType.Lt to 6,
        TokenType.Lte to 6,
        TokenType.Gt to 6,
        TokenType.Gte to 6,
        TokenType.Eq to 7,
        TokenType.Neq to 7,
        TokenType.BitAnd to 8,
        TokenType.BitXor to 9,
        TokenType.BitOr to 10,
        TokenType.And to 11,
        TokenType.Or to 12,
        TokenType.Assign to 14,
        TokenType.PlusAssign to 14,
        TokenType.MinusAssign to 14,
        TokenType.TimesAssign to 14,
        TokenType.DivideAssign to 14,
        TokenType.ModuloAssign to 14,
        TokenType.BitAndAssign to 14,
        TokenType.BitOrAssign to 14,
        TokenType.BitXorAssign to 14,
        TokenType.LShiftAssign to 14,
        TokenType.RShiftAssign to 14,
        TokenType.Comma to 15,
    )

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                return true
            }
        }
        return false
    }

    private fun match(type: TokenType): Boolean {
        if (check(type)) {
            return true
        }
        return false
    }

    private fun match(types: List<TokenType>): Boolean {
        for (type in types) {
            if (check(type)) {
                return true
            }
        }
        return false
    }


    private fun consume(vararg types: TokenType, msg: (() -> String) = { "" }): Token {
        for (type in types) {
            if (check(type)) {
                return advance()
            }
        }
        throw ParserException(peek(), msg())
    }


    private fun consume(errorMessage: (() -> String) = { "" }): Token {
        val next = advance()
        if (next.type == TokenType.Eof) {
            throw ParserException(peek(), errorMessage())
        }
        return next
    }

    private fun consume(type: TokenType, errorMessage: (() -> String) = { "" }): Token {
        if (check(type)) {
            return advance()
        }
        throw ParserException(peek(), errorMessage())
    }

    private fun tryConsume(vararg types: TokenType): Token? {
        for (type in types) {
            if (check(type)) {
                return advance()
            }
        }
        return null
    }

    private fun tryConsume(type: TokenType): Token? {
        if (check(type)) {
            return advance()
        }
        return null
    }

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) {
            return false
        }
        return peek().type == type
    }

    private fun advance(): Token {
        if (!isAtEnd()) {
            current++
        }
        return previous()
    }

    private fun isAtEnd(): Boolean {
        return peek().type == TokenType.Eof
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }

    fun parseProgram(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!isAtEnd()) {
            val stmt = parseStatement()
            if (stmt != null) {
                statements.add(stmt)
            }
        }
        return statements
    }

    private fun parseStatement(): Stmt? {
        return when {
            match(TokenType.NewLine) -> {
                consume(TokenType.NewLine)
                null
            }

            match(TokenType.Fun) -> parseFunctionDeclaration()
            match(TokenType.Class) -> parseClassDeclaration()
            match(TokenType.If) -> parseIfStatement()
            match(TokenType.While) -> parseWhileStatement()
            match(TokenType.Val, TokenType.Var) -> parseVariableDeclaration()
            match(TokenType.LBrace) -> parseBlockStatement()
            match(TokenType.Return) -> parseReturn()
            match(TokenType.Break) -> parseBreak()
            match(TokenType.Continue) -> parseContinue()
            else -> parseExpressionStatement()
        }
    }

    private fun parseReturn(): Stmt {
        consume(TokenType.Return) {
            "Expect the \"return\" keyword."
        }
        val label = tryConsume(TokenType.Label)
        val returnValue = if (match(TokenType.NewLine)) null
        else parseExpression()
        consume(TokenType.NewLine) {
            if (returnValue == null)
                "Expect newline after \"return\" keyword."
            else
                "Expect newline after return value."
        }
        return ReturnStmt(label?.lexeme, returnValue)
    }

    private fun parseBreak(): Stmt {
        consume(TokenType.Break) {
            "Expect the \"break\" keyword."
        }
        val label = tryConsume(TokenType.Label)
        consume(TokenType.NewLine) {
            if (label == null)
                "Expect newline after \"break\" keyword."
            else
                "Expect newline after break value."
        }
        return BreakStmt(label?.lexeme)
    }

    private fun parseContinue(): Stmt {
        consume(TokenType.Continue) {
            "Expect the \"continue\" keyword."
        }
        val label = tryConsume(TokenType.Label)
        consume(TokenType.NewLine) {
            if (label == null)
                "Expect newline after \"continue\" keyword."
            else
                "Expect newline after continue value."
        }
        return ContinueStmt(label?.lexeme)
    }

    private fun parseFunctionDeclaration(): Stmt {
        consume(TokenType.Fun) {
            "Expect the \"function\" keyword."
        }
        val function = consume(TokenType.Identifier) {
            "Expect a function name."
        }
        consume(TokenType.LParen) {
            "Expect a '(' after function name."
        }
        val parameters = parseParameters()
        consume(TokenType.RParen) {
            "Expect a ')' after parameters."
        }
        val body = parseBlockStatement()
        return FuncDecl(function.lexeme, parameters, body)
    }

    private fun parseClassDeclaration(): Stmt {
        consume(TokenType.Class) {
            "Expect the \"class\" keyword."
        }
        val className = consume(TokenType.Identifier)
        val superclass = if (match(TokenType.Colon)) {
            consume(TokenType.Identifier).lexeme
        } else {
            null
        }
        val body = parseBlockStatement()
        return ClassDecl(className.lexeme, superclass, body)
    }

    private fun parseIfStatement(): Stmt {
        consume(TokenType.If)
        consume(TokenType.LParen)
        val condition = parseExpression()
        consume(TokenType.RParen)
        val thenBranch = parseStatement()!!
        val elseBranch = if (tryConsume(TokenType.Else) != null) {
            parseStatement()
        } else {
            null
        }
        return IfStmt(condition, thenBranch, elseBranch)
    }

    private fun parseWhileStatement(): Stmt {
        consume(TokenType.While)
        consume(TokenType.LParen)
        val condition = parseExpression()
        consume(TokenType.RParen)
        val body = parseStatement()!!
        return WhileStmt(condition, body)
    }

    private fun parseVariableDeclaration(): Stmt {
        val isImmutable = match(TokenType.Val)
        if (isImmutable) {
            consume(TokenType.Val)
        } else {
            consume(TokenType.Var)
        }
        val name = consume(TokenType.Identifier)
        val initializer = if (match(TokenType.Assign)) {
            consume(TokenType.Assign)
            parseExpression()
        } else {
            null
        }
        return VarDecl(name.lexeme, isImmutable, initializer)

    }

    private fun parseBlockStatement(): BlockStmt {
        consume(TokenType.LBrace)
        val stmts = mutableListOf<Stmt>()
        while (!match(TokenType.RBrace) && !isAtEnd()) {
            val stmt = parseStatement()
            if (stmt != null) {
                stmts.add(stmt)
            }
        }
        consume(TokenType.RBrace)
        return BlockStmt(stmts)
    }

    private fun parseExpressionStatement(): Stmt {
        val expression = parseExpression()
        consume(TokenType.NewLine) {
            "Expect a new line after expression statement."
        }
        return ExprStmt(expression)
    }

    private fun parseParameters(): List<ParameterDef> {
        val parameters = mutableListOf<ParameterDef>()
        if (!match(TokenType.RParen)) {
            do {
                val param = consume(TokenType.Identifier) {
                    "Expect a parameter name."
                }
                consume(TokenType.Colon) {
                    "Expect ':' between a parameter and its type."
                }
                // TODO: Resolve the type
                val type = consume(TokenType.Identifier) {
                    "Expect a type."
                }
                parameters.add(ParameterDef(param.lexeme, type.lexeme))
            } while (tryConsume(TokenType.Comma) != null)
        }
        return parameters
    }


    private fun parseExpression(): Expr {
        var left = parsePrimary()

        while (true) {
            val operator = peek()
            val precedence = operatorPrecedence[operator.type] ?: 0

            if (precedence == 0) break

            advance()

            var right = parsePrimary()

            while (true) {
                val curOperator = peek()
                val currentPrecedence = operatorPrecedence[curOperator.type] ?: 0

                if (precedence >= currentPrecedence) {
                    break
                }
                val nextOperator = peek()
                advance()
                right = BinaryExpr(right, nextOperator.lexeme, parsePrimary())
            }

            left = BinaryExpr(left, operator.lexeme, right)
        }
        return left
    }

    private fun parsePrimary(): Expr {
        return when {
            match(TokenType.Number) -> {
                val value = consume(TokenType.Number).lexeme.toInt()
                IntegerLiteral(value)
            }

            match(TokenType.Number) -> {
                val value = consume(TokenType.Number).lexeme.toDouble()
                DoubleLiteral(value)
            }

            match(TokenType.String) -> {
                val value = consume(TokenType.String).lexeme
                StringLiteral(value)
            }

            match(TokenType.Null) -> {
                consume(TokenType.Null)
                NullLiteralExpr
            }

            match(TokenType.Identifier) -> {
                val name = consume(TokenType.Identifier).lexeme
                if (match(TokenType.LParen)) {
                    parseCallExpression(name)
                } else {
                    VariableExpr(name)
                }
            }

            match(TokenType.LParen) -> {
                consume(TokenType.LParen)
                val expression = parseExpression()
                consume(TokenType.RParen)
                expression
            }

            else -> {
                throw ParserException(peek(), "No such primary.")
            }
        }
    }

    private fun parseCallExpression(name: String): Expr {
        consume(TokenType.LParen)
        val arguments = parseArgumentList()
        consume(TokenType.RParen)
        return CallExpr(name, arguments)
    }

    private fun parseArgumentList(): List<Expr> {
        val arguments = mutableListOf<Expr>()
        if (!match(TokenType.RParen)) {
            do {
                val arg = parseExpression()
                arguments.add(arg)
            } while (tryConsume(TokenType.Comma) != null)
        }
        return arguments
    }
}

