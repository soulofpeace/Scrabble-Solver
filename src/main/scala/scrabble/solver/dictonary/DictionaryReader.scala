package scrabble.solver.dictionary

import scala.io.Source
import scala.collection.immutable.SortedSet

import scrabble.solver.models.Model._

object DictionaryReader{
  def apply(dictionaryFile:String):Dictionary={
    Dictionary(SortedSet(Source.fromFile(dictionaryFile).getLines.toList:_*))
  }
}
