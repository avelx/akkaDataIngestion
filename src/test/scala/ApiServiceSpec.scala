import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import org.scalatest.FreeSpec
import org.scalatest.matchers.must.Matchers._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt


class ApiServiceSpec extends FreeSpec with AkkaSpec {


  val extractorUnderTest = new ApiExtractor {
    override def sinkA: Sink[String, Future[Seq[String]]] = Sink.seq[String]

    override def sinkB: Sink[String, Future[Seq[String]]] = Sink.seq[String]
  }

  "source testing" - {
    "should fail as speed is different" in {
        val elements = List.fill(100000)("SomeString")
        val source = Source(elements)
        val closeShapeGraph = extractorUnderTest.createGraph(source, false)
        val (matLeft, matRight) = closeShapeGraph.run()
        val result = Future.sequence(Seq(matLeft, matRight))
        assert(result.isReadyWithin(10.seconds) == false)
    }
  }

  "source testing" - {
    "should return expected number of elements" in {
      val elements = List.fill(1000)("SomeString")
      val source = Source(elements)
      val closeShapeGraph = extractorUnderTest.createGraph(source)
      val (matLeft, matRight) = closeShapeGraph.run()
      val result = Future.sequence(Seq(matLeft, matRight))

      assert(result.isReadyWithin(25.seconds))
      result.futureValue match {
        case s: List[Vector[String]] =>
          s.flatMap(_.toList) should contain theSameElementsAs elements
        case _ =>
          println("Data")
          fail("Wrong result")
      }
      assert(result.isCompleted, true)
    }
  }

}
