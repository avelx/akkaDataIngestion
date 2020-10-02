//object CloseGraphSpec {
import akka.Done
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.testkit.TestPublisher.Probe
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.FreeSpec

import scala.concurrent.Future

class ApiServiceSpec extends FreeSpec with AkkaSpec {

  val serviceUnderTest = new ApiExtractor {
    override def sinkA: Sink[String, Future[Seq[String]]] = Sink.seq[String]

    override def sinkB: Sink[String, Future[Seq[String]]] = Sink.seq[String]
  }

  "source testing" - {
    "should return expected number of elements" in {
      val elements = Seq("Data", "Test", "GetGet", "ThisATest")
      val source = Source( elements )
      val g = serviceUnderTest.graph(source)
      val (resA, resB) = g
        .run()

      whenReady(resA){ futureA =>
        assert(futureA == elements)
      }

      whenReady(resB){ futureB =>
        assert(futureB == elements)
      }

    }
  }
}
