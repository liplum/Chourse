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
    "return" to TokenType.Return,
    "break" to TokenType.Break,
    "continue" to TokenType.Continue,
)
fun Char.isValidIdentifier() = isLetterOrDigit() || this == '_'

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
                if (tryConsume('=')) TokenType.PlusAssign()
                else TokenType.Plus()
            }

            '-' -> {
                if (tryConsume('=')) TokenType.MinusAssign()
                else TokenType.Minus()
            }

            '*' -> {
                if (tryConsume('=')) TokenType.TimesAssign()
                else TokenType.Times()
            }

            '/' -> {
                if (tryConsume('/')) {
                    skipComment()
                    null
                } else if (tryConsume('=')) TokenType.DivideAssign()
                else TokenType.Divide()
            }

            '%' -> {
                if (tryConsume('=')) TokenType.ModuloAssign()
                else TokenType.Modulo()
            }

            '&' -> {
                if (tryConsume('&')) TokenType.And()
                else if (tryConsume('=')) TokenType.BitAndAssign()
                else TokenType.BitAnd()
            }

            '|' -> {
                if (tryConsume('|')) TokenType.Or()
                else if (tryConsume('=')) TokenType.BitOrAssign()
                else TokenType.BitOr()
            }

            '^' -> {
                if (tryConsume('=')) TokenType.BitXorAssign()
                else TokenType.BitXor()
            }

            '~' -> TokenType.Tilde()

            '=' -> {
                if (tryConsume('=')) TokenType.Eq()
                else TokenType.Assign()
            }

            '!' -> {
                if (tryConsume('=')) TokenType.Neq()
                else TokenType.Not()
            }


            '<' -> {
                if (tryConsume('>'))
                    if (tryConsume('='))
                        TokenType.LShiftAssign()
                    else
                        TokenType.LShift()
                else if (tryConsume('=')) TokenType.Lte()
                else TokenType.Lt()
            }

            '>' -> {
                if (tryConsume('>'))
                    if (tryConsume('='))
                        TokenType.RShiftAssign()
                    else
                        TokenType.RShift()
                else if (tryConsume('=')) TokenType.Gte()
                else TokenType.Gt()
            }

            '(' -> TokenType.LParen()
            ')' -> TokenType.RParen()
            '{' -> TokenType.LBrace()
            '}' -> TokenType.RBrace()
            '[' -> TokenType.LIndex()
            ']' -> TokenType.RIndex()
            ',' -> TokenType.Comma()
            ':' -> TokenType.Colon()
            '.' -> TokenType.Dot()
            '@' -> label()
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
        while (peek().isValidIdentifier()) {
            advance()
        }
        val lexeme = source.substring(start, current)
        val type = keywords[lexeme] ?: TokenType.Identifier
        return type(lexeme)
    }

    private fun label(): Token {
        tryConsume('@')
        while (peek().isValidIdentifier()) {
            advance()
        }
        val lexeme = source.substring(start, current)
        val type = keywords[lexeme] ?: TokenType.Identifier
        return type(lexeme)
    }

    private fun skipComment() {
        while (!tryConsume('\n')) {
            advance()
        }
        line++
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

    private fun tryConsume(expected: Char): Boolean {
        if (isAtEnd() || peek() != expected) {
            return false
        }
        current++
        return true
    }
}

