import akka.Done

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Random, Success}

object FuturesPlayGround {
  implicit val ec = ExecutionContext.global

  def extraction(params: String): Future[Done] = {
   Future {
     Thread.sleep( Random.nextInt(200))
     val seed = Random.nextInt(100)
     if (seed > 80)
       throw new RuntimeException(s"Some error: $seed")
     else
      Done
   }
  }

  def main(args: Array[String]) : Unit = {
    val sq = List("5", "6", "7", "8", "45", "45", "45", "32453")
    val res = Future.sequence( sq.map(extraction ) )
    val d = res.map(result => if (result.forall(_ == Done))  Done
      else throw new RuntimeException("Error occurred"))
    d onComplete{
      case Success(value) => println(value)
      case Failure(exception) => println(exception)
    }
    Thread.sleep(500)
  }
}
