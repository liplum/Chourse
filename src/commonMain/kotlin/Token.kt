package net.liplum.chourse

enum class TokenTypeCat {
    Assign
}

enum class TokenType(
    val lexeme: kotlin.String? = null,
    val cat: TokenTypeCat? = null,
) {
    // Keywords
    Fun("fun"), Class("class"), If("if"), Else("else"), While("while"), For("for"), Val("val"), Var("var"),

    // Operators
    Plus("+"), Minus("-"), Times("*"), Divide("/"), Modulo("%"),

    // Bitwise
    BitAnd("&"), BitOr("|"), BitXor("^"),
    LShift("<<"), RShift(">>"),

    // Assignments
    Assign("=", TokenTypeCat.Assign), PlugAssign("+=", TokenTypeCat.Assign),
    MinusAssign("-=", TokenTypeCat.Assign), TimesAssign("*=", TokenTypeCat.Assign),
    DivideAssign("/=", TokenTypeCat.Assign), ModuloAssign("%=", TokenTypeCat.Assign),
    BitAndAssign("&=", TokenTypeCat.Assign), BitOrAssign("|=", TokenTypeCat.Assign),
    BitXorAssign("^=", TokenTypeCat.Assign),

    // Comparison
    Lt("<"), Gt(">"), Lte("<="), Gte(">="), Eq("=="), Neq("!="),
    LParen("("), RParen(")"), LBrace("{"), RBrace("}"), Comma(","),
    Dot("."), Colon(":"),

    // Logic
    Not("!"), And("&&"), Or("||"),

    // Identifiers and literals
    Identifier, Number, String, Character, Null,
    NewLine("\n"), Eof
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val line: Int
) {
    override fun toString(): String {
        val lexeme = when (this.lexeme) {
            "\n" -> "\\n"
            else -> this.lexeme
        }
        return "[$type-$line]\"$lexeme\""
    }
}

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