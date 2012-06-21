package scrabble.solver

import scrabble.solver.models.Model._

//import scalaz._
//import scalaz.Scalaz._

object Solver extends Application{

  def solveBoard(dictionary:Dictionary, board:Board)={
     val boardWidth = board.size -1
     val boardHeight = board.head.size -1
     val entryStream = for{x <- (0.to(boardWidth).toList)
                           y <- (0.to(boardHeight).toList)
                          } yield (x, y);
     entryStream.foldLeft(List[String]())((wordsFound, entry) =>{
       //println("starto: "+entry)
        findWordsFrom(
          dictionary,
          board,
          entry,
          List[Entry](),
          wordsFound,
          board(entry._1)(entry._2)
        )
     })
  }

  private def findWordsFrom(
    dictionary:Dictionary, 
    board:Board, 
    currentEntry:Entry, 
    visitedEntries:List[Entry],
    wordsFound:List[String],
    currentWord:String):List[String]={
    //println("current word: "+currentWord)
    val newWordsFound = populateWordsFound(wordsFound, currentWord, dictionary)
    val newVisitedEntries = visitedEntries:+currentEntry
    if(!shouldTerminate(newVisitedEntries, board) &&  shouldProceed(currentWord, dictionary)){
      getNextMoves(board, currentEntry).par.filter(m => !newVisitedEntries.contains(m)).par.
      foldLeft(newWordsFound)((wordsFoundAccumulator, entry)=> {
        //println("Entry: "+entry)
        findWordsFrom(
          dictionary, 
          board, 
          entry,
          newVisitedEntries,
          wordsFoundAccumulator,
          currentWord+board(entry._1)(entry._2)
        )
      })
    }
    else{
      newWordsFound
    }
  }

  private def populateWordsFound(wordsFound:List[String], currentWord:String, dictionary:Dictionary)={
    if(dictionary.isValidWord(currentWord) && !wordsFound.contains(currentWord)){
      wordsFound:+currentWord
    }
    else{
      wordsFound
    }
  }

  private def shouldProceed(currentWord:String, dictionary:Dictionary)={
    if(dictionary.hasPrefix(currentWord)){
      true
    }
    else{
      false
    }
  }

  private def shouldTerminate(visitedEntries:List[Entry], board:Board)={
    //println("Size: "+visitedEntries.size)
    if (visitedEntries.size==(board.size*board.head.size)){
      true
    }
    else{
      false
    }
  }

  private def getNextMoves(board:Board, entry:Entry)={
    val numRow = board.size -1
    val numColumn = board.head.size -1
    val north = (location:Entry) => (location._1-1, location._2):Entry
    val northWest = (location:Entry) => (location._1-1, location._2-1):Entry
    val northEast = (location:Entry) => (location._1-1, location._2+1):Entry
    val west = (location:Entry) => (location._1, location._2-1):Entry
    val east = (location:Entry) => (location._1, location._2+1):Entry
    val south = (location:Entry) => (location._1+1, location._2):Entry
    val southWest =(location:Entry) => (location._1+1, location._2-1):Entry
    val southEast =(location:Entry) => (location._1+1, location._2+1):Entry
    val possibleMoves = List(north, northWest, northEast, west, east, south, southWest, southEast)
    possibleMoves.par.map(move => move.apply(entry)).par.filterNot(e => { (e._1 > numRow) || (e._2 > numColumn || e._1 < 0 || e._2 < 0 )})
  }
}
