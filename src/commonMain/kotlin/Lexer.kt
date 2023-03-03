package net.liplum.chourse

val keywords = mapOf(
    "fun" to TokenType.Fun,
    "class" to TokenType.Class,
    "if" to TokenType.If,
    "else" to TokenType.Else,
    "while" to TokenType.While,
    "for" to TokenType.For,
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
        tokens.add(Token(TokenType.Eof, "", line))
        return tokens
    }

    private fun scanToken(): Token? {
        return when (val c = advance()) {
            '+' -> Token(TokenType.Plus, "+", line)
            '-' -> Token(TokenType.Minus, "-", line)
            '*' -> Token(TokenType.Times, "*", line)
            '/' -> Token(TokenType.Divide, "/", line)
            '=' -> {
                if (match('=')) Token(TokenType.Eq, "==", line)
                else Token(TokenType.Assign, "=", line)
            }

            '!' -> {
                if (match('=')) Token(TokenType.Neq, "!=", line)
                else null
            }

            '<' -> {
                if (match('=')) Token(TokenType.Lte, "<=", line)
                else Token(TokenType.Lt, "<", line)
            }

            '>' -> {
                if (match('=')) Token(TokenType.Gte, ">=", line)
                else Token(TokenType.Gt, ">", line)
            }

            '(' -> Token(TokenType.LParen, "(", line)
            ')' -> Token(TokenType.RParen, ")", line)
            '{' -> Token(TokenType.LBrace, "{", line)
            '}' -> Token(TokenType.RBrace, "}", line)
            ',' -> Token(TokenType.Comma, ",", line)
            in '0'..'9' -> number()
            in 'a'..'z', in 'A'..'Z', '_' -> identifier()
            '"' -> string()
            '\n' -> {
                Token(TokenType.NewLine, "\n", line++)
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
        return Token(TokenType.Number, source.substring(start, current), line)
    }

    private fun identifier(): Token {
        while (peek().isLetterOrDigit() || peek() == '_') {
            advance()
        }
        val lexeme = source.substring(start, current)
        val type = keywords[lexeme] ?: TokenType.Identifier
        return Token(type, lexeme, line)
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
        return Token(TokenType.String, source.substring(start + 1, current - 1), line)
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

    private fun match(expected: Char): Boolean {
        if (isAtEnd() || source[current] != expected) {
            return false
        }
        current++
        return true
    }
}