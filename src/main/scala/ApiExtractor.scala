import akka.{Done, NotUsed}
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, RunnableGraph, Sink, Source}

import scala.concurrent.Future

trait ApiExtractor {
  def sinkA: Sink[String, Future[_]]
  def sinkB: Sink[String, Future[_]]

  def processing : Flow[String, String, NotUsed] = Flow[String]

  def graph(source: Source[String, NotUsed]) =
    RunnableGraph
      .fromGraph(GraphDSL.create(sinkA, sinkB)(Tuple2.apply) {
        implicit builder => (a, b) =>
          import GraphDSL.Implicits._
          val broadcast = builder.add(Broadcast[String](2))
          source ~> broadcast
          broadcast.out(0) ~> processing ~> a
          broadcast.out(1) ~> b
          ClosedShape
      })
}
