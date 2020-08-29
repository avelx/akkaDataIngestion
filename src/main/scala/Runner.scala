import spray.json._
import models._

object Runner {

  import models.TradeProtocol.tradeFormat

  def main(args: Array[String]) : Unit = {

    val source = """{ "a": "JSON source" }"""

    val jsonAst = source.parseJson

    val trade = jsonAst.convertTo[Trade]

    println(trade)
  }

}
