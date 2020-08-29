import spray.json.DefaultJsonProtocol

package object models {

  final case class Trade (
    a : String
  )

  object TradeProtocol extends DefaultJsonProtocol {
    implicit val tradeFormat = jsonFormat1(Trade)
  }

}