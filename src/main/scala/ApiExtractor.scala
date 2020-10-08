import akka.{Done, NotUsed}
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, RunnableGraph, Sink, Source}

import scala.collection.immutable
import scala.concurrent.Future
import scala.concurrent.duration._

trait ApiExtractor {
  def sinkA: Sink[String, Future[_]]
  def sinkB: Sink[String, Future[_]]

  def reduceRate : Flow[String, String, NotUsed] =
    Flow[String]
      .throttle(1000, per = 500.milliseconds)
      .map( Seq(_) )
      .conflate((acc: Seq[String], el: Seq[String]) => acc ++ el)
      .throttle(500, 500.milliseconds)
      .mapConcat(identity)
//
  def slowProcessing : Flow[String, String, NotUsed] = Flow[String]
    .map(s => {
      Thread.sleep(10)
      s
    })

  def fastProcessing : Flow[String, String, NotUsed] = Flow[String].map(_ + "")

  def createGraph(source: Source[String, NotUsed], reduceSpeed: Boolean = true) =
    RunnableGraph
      .fromGraph(GraphDSL.create(sinkA, sinkB)(Tuple2.apply) {
        implicit builder => (a, b) =>
          import GraphDSL.Implicits._
          val broadcast = builder.add(Broadcast[String](2))
          if (reduceSpeed)
            source.via(reduceRate) ~> broadcast
          else
            source ~> broadcast
          broadcast.out(0) ~> fastProcessing ~> a
          broadcast.out(1) ~> slowProcessing ~> b
          ClosedShape
      })
}
