package net.liplum.chourse


enum class TokenType(
    val lexeme: kotlin.String? = null,
) {
    // Keywords
    Fun("fun"), Class("class"), If("if"), Else("else"), While("while"), For("for"), Val("val"), Var("var"),

    // Operators
    Plus("+"), Minus("-"), Times("*"), Divide("/"), Modulo("%"),

    // Bitwise
    BitAnd("&"), BitOr("|"), BitXor("^"),
    LShift("<<"), RShift(">>"), Tilde("~"),

    // Assignments
    Assign("="), PlusAssign("+="), MinusAssign("-="), TimesAssign("*="), DivideAssign("/="), ModuloAssign("%="),
    BitAndAssign("&="), BitOrAssign("|="), BitXorAssign("^="),
    LShiftAssign("<<="), RShiftAssign(">>="),

    // Comparison
    Lt("<"), Gt(">"), Lte("<="), Gte(">="), Eq("=="), Neq("!="),
    LParen("("), RParen(")"), LBrace("{"), RBrace("}"), Comma(","),

    // Special
    Dot("."), Colon(":"), LIndex("["), RIndex("]"),

    // Logic
    Not("!"), And("&&"), Or("||"),

    // Identifiers and literals
    Identifier, Number, String, Character, Null,
    NewLine("\n"), Eof;

    companion object {
        val assigns = listOf(
            Assign, PlusAssign, MinusAssign, TimesAssign, DivideAssign, BitAndAssign, BitOrAssign, BitXorAssign
        )
    }
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