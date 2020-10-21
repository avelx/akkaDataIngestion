import akka.Done
import akka.actor.ActorSystem

import scala.concurrent.Future
import akka.stream.alpakka.cassandra.{CassandraSessionSettings, CassandraWriteSettings}
import akka.stream.alpakka.cassandra.scaladsl.{CassandraFlow, CassandraSession, CassandraSessionRegistry}
import akka.stream.scaladsl.{Sink, Source}
import cassandra.TradeWriterFlow
import generators.TradeKeyGen

import scala.util.{Failure, Success}

trait DummyService extends TradeWriterFlow with TradeKeyGen {
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher

  val sessionSettings = CassandraSessionSettings()
  implicit val cassandraSession: CassandraSession = CassandraSessionRegistry.get(system)
    .sessionFor(sessionSettings)

  def run() = {
    Source(jsonGen.sample.get.toList)
      .via(writerToCassandra)
      .runWith(Sink.ignore)
      .onComplete {
        case Success(value) => println(s"Success $value")
        case Failure(exception) => println(s"Failure: $exception")
      }
  }
}

object CassandraRunner {

  def main(args: Array[String]): Unit = {
    val service = new DummyService { }
    service.run()
  }
}
