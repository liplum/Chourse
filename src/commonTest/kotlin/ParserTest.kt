import net.liplum.chourse.Lexer
import net.liplum.chourse.Parser
import net.liplum.chourse.antiLexer
import net.liplum.chourse.transpiler.ToSourceVisitor
import kotlin.test.Test

class ParserTest {
    @Test
    fun `test single statement`() {
        val singleStatement = """
class A {
  fun testSingleStatement(){
    if(a>0) return 1
    else return 0
  }
}
""".trimIndent()
        val lexer = Lexer(singleStatement)
        val tokens = lexer.scanTokens()
        println(tokens)
        println("-----------")
        println(tokens.antiLexer())
        println("-----------")
        val parser = Parser(tokens)
        val result = parser.parseProgram()
        println(result)
        println("-----------")
        run {
            val source = StringBuilder()
            val toSource = ToSourceVisitor(source)
            for (stmt in result) {
                stmt.accept(toSource)
            }
            println(source)
        }
        println("-----------")
    }
}