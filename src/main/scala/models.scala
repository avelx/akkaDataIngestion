import spray.json.{DefaultJsonProtocol, JsArray, JsObject, JsString, JsValue, RootJsonFormat, deserializationError}

import scala.util.Try

package object models {

  final case class Trade (
    a : String,
    b : String,
    c : String,
    d : String,
    e : String,
    f : String,
    g : String,
    h : String,
    i : String,
    k : String,
    l : String,
    m : String,    // 12
    n : String,
    o : String,
    p : String,
    q : String,
    r : String,
    s : String,   // 18
    t : String,
    v : String,
    x : String,
    y : String,
    z : String   // 23
  )

  object TradeProtocol extends DefaultJsonProtocol {
    //implicit val tradeFormat = jsonFormat22(Trade)
    implicit object TradeJsonFormat extends RootJsonFormat[Trade] {
      def write(t: Trade) =
        JsArray(
          JsString(t.a),
          JsString(t.b),
          JsString(t.c),
          JsString(t.d),
          JsString(t.e),
          JsString(t.f),
          JsString(t.g),
          JsString(t.h),
          JsString(t.i),
          JsString(t.k),
          JsString(t.l),
          JsString(t.m),
          JsString(t.n),
          JsString(t.o),
          JsString(t.p),
          JsString(t.q),
          JsString(t.r),
          JsString(t.s),
          JsString(t.t),
          JsString(t.v),
          JsString(t.x),
          JsString(t.y),
          JsString(t.z)
        )

      private def toString(v: JsValue ) : String = {
        Try {
          v.asInstanceOf[JsString].value
        }.getOrElse("")
      }

      def read(value: JsValue) = value match {
        case JsObject( t ) =>

          Trade(
            toString( t("a") ), toString( t("b") ), toString( t("c") ), toString( t("d") ), toString( t("e") ),
            toString( t("f") ), toString( t("g") ), toString( t("h") ), toString( t("i") ), toString( t("k") ),
            toString( t("l") ), toString( t("m") ), toString( t("n") ), toString( t("o") ), toString( t("p") ),
            toString( t("q") ), toString( t("r") ), toString( t("s") ), toString( t("t") ), toString( t("v") ),
            toString( t("x") ), toString( t("y") ), toString( t("z") )
          )
        case _ =>
          deserializationError("Trade object expected")
      }
    }
  }

}