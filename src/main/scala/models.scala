import spray.json.{DefaultJsonProtocol, JsArray, JsObject, JsString, JsValue, RootJsonFormat, deserializationError}

package object models {

  object JsonHelper {
    implicit class JsValueToString(t: JsValue) {
      def as : String = t match {
        case JsString(v) => v
        case _ => ""
      }
    }
  }

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

      def read(value: JsValue) = value match {
        case JsObject( t ) =>
          import JsonHelper.JsValueToString
          Trade(
            t("a").as, t("b").as, t("c").as, t("d").as, t("e").as,  t("f").as,   t("g").as,
            t("h").as,  t("i").as,  t("k").as, t("l").as, t("m").as, t("n").as, t("o").as, t("p").as, t("q").as, t("r").as, t("s").as, t("t").as, t("v").as,
              t("x").as, t("y").as, t("z").as
          )
        case _ =>
          deserializationError("Trade object expected")
      }
    }
  }

}