import net.liplum.chourse.Lexer
import net.liplum.chourse.Parser
import net.liplum.chourse.antiLexer
import net.liplum.chourse.transpiler.ToC89Visitor
import net.liplum.chourse.transpiler.ToSourceVisitor

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
    val input = """
        class A {
          fun test(b: Int){
            if (x > 0) {
              print("x is positive")
            } else {
              print("x is not positive")
            }
            var a = 10
            a += 10
            a -= 5
            print(a==b)
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