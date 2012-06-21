package scrabble.solver.models

import scala.collection.immutable.SortedSet

object Model{
  type Board = List[List[String]]
  type Entry = (Int, Int)

  case class Dictionary(indices:SortedSet[String]){
    def isValidWord(word:String)={
      !indices.range(word, word).isEmpty
    }
    def hasPrefix(word:String)={
      !indices.from(word).takeWhile(w => w.startsWith(word)).isEmpty
    }
  }
}
