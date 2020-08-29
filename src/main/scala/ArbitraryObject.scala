
object ArbitraryObject {

  def main(args: Array[String]) : Unit = {

    import org.scalacheck._

    final case class Fruit(name: String, origin: String)
    val originGen = Gen.oneOf( List("apple", "banana", "orange", "strawberry") )
    val fruitNameGen = Gen.oneOf( List("USA", "Espagne", "France", "Sweden", "Mexico") )

    val fruits = {
      for {
        _ <- 0 to 10
        origin <- originGen.sample.take(1)
        fruit <- fruitNameGen.sample.take(1)
      } yield Fruit(fruit, origin)
    }

    println( fruits )

  }
}
