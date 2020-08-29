import spray.json.DefaultJsonProtocol

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
    y : String
    //z : String
  )

  object TradeProtocol extends DefaultJsonProtocol {
    implicit val tradeFormat = jsonFormat22(Trade)
  }

}