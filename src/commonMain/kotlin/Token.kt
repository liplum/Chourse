package net.liplum.chourse


enum class TokenType(
    val lexeme: kotlin.String? = null,
) {
    // Keywords
    Fun("fun"), Class("class"), If("if"), Else("else"), While("while"), For("for"), Val("val"), Var("var"),
    /// Clause
    Return("return"), Break("break"), Continue("continue"),
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
    Identifier, Number, String, Character, Null, Label,
    NewLine("\\n"), Eof;
}


data class Token(
    val type: TokenType,
    val lexeme: String,
    val line: Int
) {
    override fun toString(): String =
        "[$type-$line]\"${this.lexeme}\""
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