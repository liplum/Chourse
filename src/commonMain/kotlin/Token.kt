package net.liplum.chourse

enum class TokenType {
    // Keywords
    Fun, Class, If, Else, While, For,

    // Operators
    Plus, Minus, Times, Divide, Assign, Eq, Neq, Lt, Gt, Lte, Gte,
    LParen, RParen, LBrace, RBrace, Comma,

    // Identifiers and literals
    Identifier, Number, String,
    NewLine, Eof
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val line: Int
)

fun List<Token>.antiLexer(): String {
    val b = StringBuilder()
    for (token in this) {
        when (token.type) {
            TokenType.String -> {
                b.append('"')
                b.append(token.lexeme)
                b.append('"')
            }
            else -> b.append(token.lexeme)
        }
        b.append(" ")
    }
    return b.toString()
}