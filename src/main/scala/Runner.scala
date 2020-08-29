import spray.json._
import models._

object Runner {

  import models.TradeProtocol.TradeJsonFormat

  def main(args: Array[String]) : Unit = {

    val source =
      """{
        |   "a" : "Alpha",
        |   "b" : "Betta",
        |   "c" : "CCCC",
        |   "d" : "Delta",
        |   "e" : "Elephan",
        |   "f" : "Finite",
        |   "g" : "Ge",
        |   "h" : "Hero",
        |   "i" : "Internet",
        |   "k" : "Kilo",
        |   "l" : "Limo",
        |   "m" : "Milk",
        |   "n" : "Nero",
        |   "o" : "Opera",
        |   "p" : "Petra",
        |   "q" : "Quatra",
        |   "r" : "Roller",
        |   "s" : "Seven",
        |   "t" : "Tactic",
        |   "v" : "Vello",
        |   "x" : "Xerox",
        |   "y" : "Yellow",
        |   "z" : "Zeta"
        |}""".stripMargin

    val jsonAst = source.parseJson

    val trade = jsonAst.convertTo[Trade]

    println(trade)
  }

}
