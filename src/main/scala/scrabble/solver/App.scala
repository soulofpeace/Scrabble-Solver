package scrabble.solver

import scrabble.solver.models.Model._
import scrabble.solver.dictionary.DictionaryReader

import java.io._
import java.util.concurrent.TimeUnit

import com.yammer.metrics.reporting.ConsoleReporter

object App{

  def main(arg:Array[String])={

    //ConsoleReporter.enable(60, TimeUnit.SECONDS)
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
    printToFile(new File("solution.txt"))(p=> {solutions.foreach(p.println)})

  }

  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
      val p = new java.io.PrintWriter(f)
        try { op(p) } finally { p.close() }
  }

}
