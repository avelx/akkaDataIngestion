package generators

import model.TradeKey
import org.scalacheck.Gen

trait TradeKeyGen {
  val jsonGen = for {
    date <- Gen.oneOf( Seq("2000-01-05", "2006-02-04", "1956-04-29") )
    json <- Gen.alphaNumStr
    counterParty <- Gen.oneOf( Seq("AA", "AB", "CC", "DDD"))
    id <- 1 to 5000
  } yield TradeKey(id, date, counterParty, json)
}
