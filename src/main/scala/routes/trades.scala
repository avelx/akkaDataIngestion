package routes

import akka.http.scaladsl.server.Directives
import model.{Trade, JsonSupport}
import org.scalacheck.Gen

class TradeService extends Directives with JsonSupport {
  val route = {
    path("trades") {
      get {
        val name = Gen.oneOf("Alex", "Fox", "Mox", "Dog")
        val id = Gen.oneOf(0 to 1000)
        complete( Trade(id.sample.get, name.sample.get) )
      }
    }
  }
}