package net.liplum.chourse

class ParserException(message: String?) : Exception(message) {
    constructor(token: Token, message: String? = null) : this(
        if (token.type == TokenType.Eof) {
            "[line ${token.line}] Error at end: $message"
        } else {
            val lexeme = if (token.lexeme == "\n") "\\n" else token.lexeme
            "[line ${token.line}] Error at \"${lexeme}\": $message"
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
        TokenType.Not to 1, // logical not
        TokenType.Eq to 1,
        TokenType.Neq to 1,
        TokenType.Lt to 2,
        TokenType.Lte to 2,
        TokenType.Gt to 2,
        TokenType.Gte to 2,
        TokenType.Plus to 3,
        TokenType.Minus to 3,
        TokenType.Times to 4,
        TokenType.Divide to 4,
        TokenType.And to 5,
        TokenType.Or to 6,
        TokenType.MinusAssign to 7,
        TokenType.PlusAssign to 7,
        TokenType.TimesAssign to 7,
        TokenType.DivideAssign to 7,
        TokenType.MinusAssign to 7,
        TokenType.BitXorAssign to 7,
        TokenType.BitOrAssign to 7,
        TokenType.BitAndAssign to 7,
        TokenType.Assign to 7,
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

    private fun consume(types: List<TokenType>, msg: (() -> String) = { "" }): Token {
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

    private fun tryConsume(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun tryConsume(type: TokenType): Boolean {
        if (check(type)) {
            advance()
            return true
        }
        return false
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
            else -> parseExpressionStatement()
        }
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
        return FunctionDecl(function.lexeme, parameters, body)
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
        val elseBranch = if (tryConsume(TokenType.Else)) {
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
        return VariableDecl(name.lexeme, initializer)

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
        return ExpressionStmt(expression)
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
            } while (tryConsume(TokenType.Comma))
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

    private fun parseAssignment(): Expr {
        val left = parseLogicalOr()
        if (match(TokenType.assigns)) {
            consume()
            val right = parseAssignment()
            return if (left is VariableExpr) {
                AssignmentExpr(left.name, right)
            } else {
                throw ParserException(peek(), "Invalid assignment target")
            }
        }
        return left
    }

    private fun parseLogicalOr(): Expr {
        var left = parseLogicalAnd()
        while (match(TokenType.Or)) {
            val operator = consume(TokenType.Or).lexeme
            val right = parseLogicalAnd()
            left = BinaryExpr(left, operator, right)
        }
        return left
    }

    private fun parseLogicalAnd(): Expr {
        var left = parseBitwiseOr()
        while (match(TokenType.And)) {
            val operator = consume(TokenType.And).lexeme
            val right = parseBitwiseOr()
            left = BinaryExpr(left, operator, right)
        }
        return left
    }

    private fun parseBitwiseOr(): Expr {
        var left = parseBitwiseXor()
        while (match(TokenType.Or)) {
            val operator = consume(TokenType.Or).lexeme
            val right = parseBitwiseXor()
            left = BinaryExpr(left, operator, right)
        }
        return left
    }

    private fun parseBitwiseXor(): Expr {
        var left = parseBitwiseAnd()
        while (match(TokenType.BitXor)) {
            val operator = consume(TokenType.BitXor).lexeme
            val right = parseBitwiseAnd()
            left = BinaryExpr(left, operator, right)
        }
        return left
    }

    private fun parseBitwiseAnd(): Expr {
        var left = parseEquality()
        while (match(TokenType.BitAnd)) {
            val operator = consume(TokenType.BitAnd).lexeme
            val right = parseEquality()
            left = BinaryExpr(left, operator, right)
        }
        return left
    }

    private fun parseEquality(): Expr {
        var left = parseComparison()
        while (match(TokenType.Eq) || match(TokenType.Neq)) {
            val operator = consume().lexeme
            val right = parseComparison()
            left = BinaryExpr(left, operator, right)
        }
        return left
    }

    private fun parseComparison(): Expr {
        var left = parseShift()
        while (match(TokenType.Lt) || match(TokenType.Lte) || match(TokenType.Gt) || match(TokenType.Gte)) {
            val operator = consume().lexeme
            val right = parseShift()
            left = BinaryExpr(left, operator, right)
        }
        return left
    }

    private fun parseShift(): Expr {
        var left = parseTerm()
        while (match(TokenType.LShift) || match(TokenType.RShift)) {
            val operator = consume().lexeme
            val right = parseTerm()
            left = BinaryExpr(left, operator, right)
        }
        return left
    }

    private fun parseTerm(): Expr {
        var left = parseFactor()
        while (match(TokenType.Plus) || match(TokenType.Minus)) {
            val operator = consume().lexeme
            val right = parseFactor()
            left = BinaryExpr(left, operator, right)
        }
        return left
    }

    private fun parseFactor(): Expr {
        var left = parseUnary()
        while (match(TokenType.Times) || match(TokenType.Divide)) {
            val operator = consume().lexeme
            val right = parseUnary()
            left = BinaryExpr(left, operator, right)
        }
        return left
    }

    private fun parseUnary(): Expr {
        if (match(TokenType.Minus) || match(TokenType.Not)) {
            val operator = consume().lexeme
            val right = parseUnary()
            return UnaryExpr(operator, right)
        }
        return parsePrimary()
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
            } while (tryConsume(TokenType.Comma))
        }
        return arguments
    }
}

