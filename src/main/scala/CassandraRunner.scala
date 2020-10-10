import akka.actor.ActorSystem

import scala.concurrent.Future
import akka.stream.alpakka.cassandra.{CassandraSessionSettings, CassandraWriteSettings}
import akka.stream.alpakka.cassandra.scaladsl.{CassandraFlow, CassandraSession, CassandraSessionRegistry}
import akka.stream.scaladsl.{Sink, Source}
import com.datastax.oss.driver.api.core.cql.{BoundStatement, PreparedStatement}

import scala.collection.immutable
import scala.util.{Failure, Success}

object CassandraRunner {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val ec = system.dispatcher

    val sessionSettings = CassandraSessionSettings()
    implicit val cassandraSession: CassandraSession = CassandraSessionRegistry.get(system)
        .sessionFor(sessionSettings)

//    val contactPoints = Collections
//      .singletonList(InetSocketAddress.createUnresolved("cassandra.us-east-1.amazonaws.com", 9142))

//    val session = CqlSession.builder()
//      .addContactPoints(contactPoints)
//      .withSslContext(SSLContext.getDefault())
//      .withLocalDatacenter("us-east-1")
//      .withAuthProvider(new SigV4AuthProvider("us-east-1"))
//      .build()
//
//    session.wait(10000)

    case class JsonDump(id: Int, date: String, json: String)

    val jsons =
      immutable.Seq( JsonDump(7, "Data", "Test"), JsonDump(7, "Data", "Test"), JsonDump(8, "Data", "Test") )

    val statementBinder: (JsonDump, PreparedStatement) => BoundStatement =
      (json, preparedStatement) => preparedStatement.bind(Int.box(json.id), json.date, json.json)

    val result: Future[Seq[JsonDump]]= Source(jsons)
      .via(
        CassandraFlow.create(CassandraWriteSettings.defaults,
          s"INSERT INTO sand_box.json_dump(id, name, city) VALUES (?, ?, ?)",
          statementBinder)
      )
      .runWith(Sink.seq)

    result onComplete {
      case Success(value) => println(s"Success $value")
      case Failure(exception) => println(s"Failure: $exception")
    }

//    val version: Future[String] =
//      cassandraSession
//        .select("SELECT release_version FROM system.local;")
//        .map(_.getString("release_version"))
//        .runWith(Sink.head)
  }
}
