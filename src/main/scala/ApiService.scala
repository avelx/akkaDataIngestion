import akka.Done
import akka.stream.scaladsl.Sink
import scala.concurrent._

object ApiService extends ApiExtractor {
  import ExecutionContext.Implicits.global

  override def sinkA: Sink[String, Future[_]] = Sink.ignore

  override def sinkB: Sink[String, Future[_]] = Sink.ignore
}
