package net.liplum.chourse

val keywords = mapOf(
    "fun" to TokenType.Fun,
    "class" to TokenType.Class,
    "if" to TokenType.If,
    "else" to TokenType.Else,
    "while" to TokenType.While,
    "for" to TokenType.For,
    "val" to TokenType.Val,
    "var" to TokenType.Var,
    "const" to TokenType.Const,
)

class Lexer(private val source: String) {
    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): List<Token> {
        val tokens = mutableListOf<Token>()
        while (!isAtEnd()) {
            start = current
            val token = scanToken()
            if (token != null) {
                tokens.add(token)
            }
        }
        tokens.add(TokenType.Eof())
        return tokens
    }

    operator fun TokenType.invoke(
        lexeme: String = this.lexeme ?: "",
        line: Int = this@Lexer.line
    ): Token {
        return Token(this, lexeme, line)
    }

    private fun scanToken(): Token? {
        return when (val c = advance()) {
            '+' -> {
                if (ifNext('=')) TokenType.PlugAssign()
                else TokenType.Plus()
            }

            '-' -> {
                if (ifNext('=')) TokenType.MinusAssign()
                else TokenType.Minus()
            }

            '*' -> {
                if (ifNext('=')) TokenType.TimesAssign()
                else TokenType.Times()
            }

            '/' -> {
                if (ifNext('=')) TokenType.DivideAssign()
                else TokenType.Divide()
            }

            '%' -> {
                if (ifNext('=')) TokenType.ModuloAssign()
                else TokenType.Modulo()
            }

            '&' -> {
                if (ifNext('&')) TokenType.And()
                else if (ifNext('=')) TokenType.BitAndAssign()
                else TokenType.BitAnd()
            }

            '|' -> {
                if (ifNext('|')) TokenType.Or()
                else if (ifNext('=')) TokenType.BitOrAssign()
                else TokenType.BitOr()
            }

            '^' -> {
                if (ifNext('=')) TokenType.BitXorAssign()
                else TokenType.BitXor()
            }

            '=' -> {
                if (ifNext('=')) TokenType.Eq()
                else TokenType.Assign()
            }

            '!' -> {
                if (ifNext('=')) TokenType.Neq()
                else TokenType.Not()
            }

            '<' -> {
                if (ifNext('=')) TokenType.Lte()
                else TokenType.Lt()
            }

            '>' -> {
                if (ifNext('=')) TokenType.Gte()
                else TokenType.Gt()
            }

            '(' -> TokenType.LParen()
            ')' -> TokenType.RParen()
            '{' -> TokenType.LBrace()
            '}' -> TokenType.RBrace()
            ',' -> TokenType.Comma()
            ':' -> TokenType.Colon()
            '.' -> TokenType.Dot()
            in '0'..'9' -> number()
            in 'a'..'z', in 'A'..'Z', '_' -> identifier()
            '"' -> string()
            '\n' -> {
                TokenType.NewLine(line = line++)
            }

            ' ', '\r', '\t' -> null
            else -> {
                // Handle unrecognized character
                println("Unrecognized character at line $line: $c")
                null
            }
        }
    }

    private fun number(): Token {
        while (peek().isDigit()) {
            advance()
        }
        if (peek() == '.' && peekNext().isDigit()) {
            advance()
            while (peek().isDigit()) {
                advance()
            }
        }
        return TokenType.Number(source.substring(start, current))
    }

    private fun identifier(): Token {
        while (peek().isLetterOrDigit() || peek() == '_') {
            advance()
        }
        val lexeme = source.substring(start, current)
        val type = keywords[lexeme] ?: TokenType.Identifier
        return type(lexeme)
    }


    private fun string(): Token? {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++
            }
            advance()
        }
        if (isAtEnd()) {
            println("Unterminated string at line $line")
            return null
        }
        // Consume closing quote
        advance()
        return TokenType.String(source.substring(start + 1, current - 1))
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun advance(): Char {
        current++
        return source[current - 1]
    }

    private fun peek(): Char {
        return if (isAtEnd()) '\u0000'
        else source[current]
    }

    private fun peekNext(): Char {
        return if (current + 1 >= source.length) '\u0000'
        else source[current + 1]
    }

    private fun ifNext(expected: Char): Boolean {
        if (isAtEnd() || peek() != expected) {
            return false
        }
        current++
        return true
    }
}