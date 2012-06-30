package scrabble.solver

import scrabble.solver.models.Model._

import com.yammer.metrics.scala._
//import scalaz._
//import scalaz.Scalaz._

object Solver extends Instrumented{
  private val findWordsTimer = metrics.timer("findWords")
  private val checkIfGotWord = metrics.timer("checkIfGotWords")
  private val checkIfGotPrefix = metrics.timer("checkIfGotPrefix")

  def solveBoard(dictionary:Dictionary, board:Board)={
     val boardWidth = board.size -1
     val boardHeight = board.head.size -1
     val entryStream = for{x <- (0.to(boardWidth).toList)
                           y <- (0.to(boardHeight).toList)
                          } yield (x, y);
     entryStream.foldLeft(Set[String]())((wordsFound, entry) =>{
        findWordsFrom(
          dictionary,
          board,
          entry,
          Set[Entry](),
          wordsFound,
          board(entry._1)(entry._2)
        )
     })
  }

  private def findWordsFrom(
    dictionary:Dictionary, 
    board:Board, 
    currentEntry:Entry, 
    visitedEntries:Set[Entry],
    wordsFound:Set[String],
    currentWord:String):Set[String]={
      println("visited Location(row="+currentEntry._1+", col="+currentEntry._2+")")
      findWordsTimer.time{
      val newVisitedEntries = visitedEntries+currentEntry
      if(shouldTerminate(newVisitedEntries, board) ||  !shouldProceed(currentWord, dictionary)){
        //println("Not proceeding")
        wordsFound
      }else{
        val newWordsFound = populateWordsFound(wordsFound, currentWord, dictionary)
        getNextMoves(board, currentEntry).
        foldLeft(newWordsFound)((wordsFoundAccumulator, entry)=> {
          //println("Entry: "+entry)
          if(isValidEntry(entry, board) && !newVisitedEntries.contains(entry)){
            findWordsFrom(
              dictionary, 
              board, 
              entry,
              newVisitedEntries,
              wordsFoundAccumulator,
              currentWord+board(entry._1)(entry._2)
            )
          }
          else{
            wordsFoundAccumulator
          }
        })
      }
    }
  }

  private def populateWordsFound(wordsFound:Set[String], currentWord:String, dictionary:Dictionary)={
    checkIfGotWord.time{
      //println("populateWordsFound Already Found "+currentWord+":"+wordsFound.contains(currentWord))
      //println("validWord "+currentWord+":"+dictionary.isValidWord(currentWord))

      //if(!wordsFound.contains(currentWord) && dictionary.isValidWord(currentWord)){
      if(dictionary.isValidWord(currentWord)){

        //println("Word found: "+currentWord)
        wordsFound+currentWord
      }
      else{
        wordsFound
      }
    }
  }

  private def shouldProceed(currentWord:String, dictionary:Dictionary)={
    checkIfGotPrefix.time{
      if(dictionary.hasPrefix(currentWord)){
        //println("Got prefixed word: "+currentWord)
        true
      }
      else{
        println("NO PREFIX: "+currentWord)
        //println("No Prefix word: "+currentWord)
        false
      }
    }
  }

  private def shouldTerminate(visitedEntries:Set[Entry], board:Board)={
    //println("Size: "+visitedEntries.size)
    if (visitedEntries.size==(board.size*board.head.size)){
      //println("No more things")
      true
    }
    else{
      //println("Got more")
      false
    }
  }

  private def isValidEntry(entry:Entry, board:Board)={
    val numRow = board.size -1
    val numColumn = board.head.size -1
    !((entry._1 > numRow) || (entry._2 > numColumn || entry._1 < 0 || entry._2 < 0 ))
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
    possibleMoves.map(move => move.apply(entry))
  }
}
