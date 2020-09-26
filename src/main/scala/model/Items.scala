package model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

final case class Trade(id: Long, name: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val tradeFormat = jsonFormat2(Trade)
}