package net.liplum.chourse

class ParserException(message: String? = null) : Exception(message)
class Parser(private val tokens: List<Token>) {
    private var current = 0
    private val currentToken get() = tokens[current]
    private val restTokens get() = tokens.subList(current, tokens.size)
    private val operatorPrecedence = mapOf(
        "||" to 1, "&&" to 2,
        "|" to 3, "&" to 4,
        "==" to 5, "!=" to 5,
        "<" to 6, ">" to 6, "<=" to 6, ">=" to 6,
        "<<" to 7, ">>" to 7,
        "+" to 8, "-" to 8,
        "*" to 9, "/" to 9
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

    private fun match(cat: TokenTypeCat): Boolean {
        if (check(cat)) {
            return true
        }
        return false
    }

    private fun consume(vararg types: TokenType, msg: (() -> String) = { "" }): Token {
        for (type in types) {
            if (check(type)) {
                return advance()
            }
        }
        error(peek(), msg())
        throw ParserException()
    }

    private fun consume(msg: (() -> String) = { "" }): Token {
        return advance()
    }

    private fun consume(type: TokenType, msg: (() -> String) = { "" }): Token {
        if (check(type)) {
            return advance()
        }
        error(peek(), msg())
        throw ParserException()
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

    private fun check(cat: TokenTypeCat): Boolean {
        if (isAtEnd()) {
            return false
        }
        return peek().type.cat == cat
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

    private fun error(token: Token, message: String? = null) {
        if (token.type == TokenType.Eof) {
            println("[line ${token.line}] Error at end: $message")
        } else {
            println("[line ${token.line}] Error at end: $message")
            println("[line ${token.line}] Error${token.lexeme}: $message")
        }
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
        consume(TokenType.NewLine)
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
        return parseAssignment()
    }

    private fun parseAssignment(): Expr {
        val left = parseLogicalOr()
        if (match(TokenTypeCat.Assign)) {
            consume()
            val right = parseAssignment()
            return if (left is VariableExpr) {
                AssignmentExpr(left.name, right)
            } else {
                throw ParserException("Invalid assignment target")
            }
        }
        return left
    }

    private fun parseLogicalOr(): Expr {
        var left = parseLogicalAnd()
        while (match(TokenType.Or)) {
            val operator = consume(TokenType.Or).lexeme
            val right = parseLogicalAnd()
            left = BinaryExpr(operator, left, right)
        }
        return left
    }

    private fun parseLogicalAnd(): Expr {
        var left = parseBitwiseOr()
        while (match(TokenType.And)) {
            val operator = consume(TokenType.And).lexeme
            val right = parseBitwiseOr()
            left = BinaryExpr(operator, left, right)
        }
        return left
    }

    private fun parseBitwiseOr(): Expr {
        var left = parseBitwiseXor()
        while (match(TokenType.Or)) {
            val operator = consume(TokenType.Or).lexeme
            val right = parseBitwiseXor()
            left = BinaryExpr(operator, left, right)
        }
        return left
    }

    private fun parseBitwiseXor(): Expr {
        var left = parseBitwiseAnd()
        while (match(TokenType.BitXor)) {
            val operator = consume(TokenType.BitXor).lexeme
            val right = parseBitwiseAnd()
            left = BinaryExpr(operator, left, right)
        }
        return left
    }

    private fun parseBitwiseAnd(): Expr {
        var left = parseEquality()
        while (match(TokenType.BitAnd)) {
            val operator = consume(TokenType.BitAnd).lexeme
            val right = parseEquality()
            left = BinaryExpr(operator, left, right)
        }
        return left
    }

    private fun parseEquality(): Expr {
        var left = parseComparison()
        while (match(TokenType.Eq) || match(TokenType.Neq)) {
            val operator = consume().lexeme
            val right = parseComparison()
            left = BinaryExpr(operator, left, right)
        }
        return left
    }

    private fun parseComparison(): Expr {
        var left = parseShift()
        while (match(TokenType.Lt) || match(TokenType.Lte) || match(TokenType.Gt) || match(TokenType.Gte)) {
            val operator = consume().lexeme
            val right = parseShift()
            left = BinaryExpr(operator, left, right)
        }
        return left
    }

    private fun parseShift(): Expr {
        var left = parseTerm()
        while (match(TokenType.LShift) || match(TokenType.RShift)) {
            val operator = consume().lexeme
            val right = parseTerm()
            left = BinaryExpr(operator, left, right)
        }
        return left
    }

    private fun parseTerm(): Expr {
        var left = parseFactor()
        while (match(TokenType.Plus) || match(TokenType.Minus)) {
            val operator = consume().lexeme
            val right = parseFactor()
            left = BinaryExpr(operator, left, right)
        }
        return left
    }

    private fun parseFactor(): Expr {
        var left = parseUnary()
        while (match(TokenType.Times) || match(TokenType.Divide)) {
            val operator = consume().lexeme
            val right = parseUnary()
            left = BinaryExpr(operator, left, right)
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
                throw ParserException()
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

