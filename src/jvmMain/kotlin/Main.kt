import net.liplum.chourse.Lexer
import net.liplum.chourse.antiLexer

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
    val input = """
        class A {
          fun test(){
            val a = 10
            if (x > 0) {
              print("x is positive")
            } else {
              print("x is not positive")
            }
            print(a)
          }
        }
    """.trimIndent()

    val lexer = Lexer(input)
    val tokens = lexer.scanTokens()
    println(tokens)
    println("-----------")
    println(tokens.antiLexer())
}