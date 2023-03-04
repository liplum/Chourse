import net.liplum.chourse.Lexer
import net.liplum.chourse.Parser
import net.liplum.chourse.antiLexer
import net.liplum.chourse.transpiler.ToC89Visitor
import net.liplum.chourse.transpiler.ToSourceVisitor
import kotlin.test.Test

class Test {
    @Test
    fun `test lexer`() {
        val input = """
        class A {
          fun test(b: Int){
            if (b > 0) {
              print("b is positive")
            } else {
              print("b is negative")
            }
            var a = 10
            a += 10
            a -= 5
            print(a==b)
            print(add(a,b))
          }
          
          fun add(a:Int,b:Int){
            return a+b
          }
        }
    """.trimIndent()
        val lexer = Lexer(input)
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
                source.append("\n")
            }
            println(source)
        }
        println("-----------")
        run {
            val source = StringBuilder()
            val toSource = ToC89Visitor(source)
            for (stmt in result) {
                stmt.accept(toSource)
                source.append("\n")
            }
            println(source)
        }
    }
}
