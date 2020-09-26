import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import routes.TradeService
import scala.io.StdIn

object HttpServerRoutingMinimal {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext = system.executionContext

    val bindingFuture = Http()
      .newServerAt("localhost", 8080)
      .bind(new TradeService().route)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

    StdIn.readLine()
      bindingFuture
        .flatMap(_.unbind())
          .onComplete(_ => system.terminate())

  }
}