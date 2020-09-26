import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import model.{JsonSupport, Trade}
import spray.json._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object HttpStreamerClient extends  JsonSupport{

  def main(args: Array[String]) : Unit = {
    implicit val system = ActorSystem()
    implicit val dispatcher = system.dispatcher

    def parse(line: ByteString): Option[Trade] =
      Try {
        line
          .utf8String
          .parseJson
          .convertTo[Trade]
      }.toOption

    val processorFlow: Flow[Option[Trade], Option[Trade], NotUsed] =
      Flow[Option[Trade]]
        .map ( ot => {
          println(ot)
          ot
        })

    val requests: Source[HttpRequest, NotUsed] = Source
      .fromIterator(() =>
        Range(0, 10).map(_ => HttpRequest(uri = Uri(s"http://localhost:8080/trades"))).iterator
      )

    def runRequest(req: HttpRequest): Future[Option[Trade]] =
      Http()
        .singleRequest(req)
        .flatMap { response =>
          response.entity.dataBytes
            .runReduce(_ ++ _)
            .map(parse)
        }

    requests
      .mapAsync(2)(runRequest)
      .via(processorFlow)
      .runWith(Sink.ignore)

  }
}