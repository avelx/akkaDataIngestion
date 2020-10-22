import akka.Done

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Random, Success}

object FuturesPlayGround {
  implicit val ec = ExecutionContext.global

  def extraction(params: String): Future[Done] = {
   Future {
     Thread.sleep(5000)
     println(s"Run: $params")

     //     Thread.sleep( Random.nextInt(200))
     val seed = Random.nextInt(100)
     if  (seed > 80)
       throw new RuntimeException(s"Some error: $seed")
     else
      Done
   }
  }

  def main(args: Array[String]) : Unit = {
    val sq = List("5", "6", "7", "8", "45", "45", "45", "32453")
    val res = sq
      .foldLeft( Future[Done](Done) ){ (op, elem) =>
        op.flatMap{ _ => extraction(elem) }
      }
    res onComplete {
      case Success(v) => println(v)
      case Failure(ex) => println(ex)
    }

    Thread.sleep(50000)
  }
}
