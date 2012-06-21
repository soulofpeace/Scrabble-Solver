package scrabble.solver

import scrabble.solver.models.Model._
import scrabble.solver.dictionary.DictionaryReader

object App{

  def main(arg:Array[String])={
    val start = System.currentTimeMillis
    val board:Board=List(
      List("l", "qu", "r", "e"),
      List("s", "l", "u", "s"),
      List("a", "t", "i", "c"),
      List("n", "r", "e", "n")
      )
    val dictionary = DictionaryReader("/usr/share/dict/words")
    val solutions = Solver.solveBoard(dictionary, board)
    println("Time Taken: "+(System.currentTimeMillis-start))
    println(solutions.size + " Solutions :")
    solutions.foreach(println)

  }
}
