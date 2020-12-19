import akka.actor.ActorSystem
import akka.stream.alpakka.hdfs.scaladsl.HdfsFlow
import akka.stream.alpakka.hdfs._
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import org.apache.hadoop.io.compress.DefaultCodec
import io.circe.generic.auto._
import io.circe.syntax._
import org.apache.hadoop.fs.Path
import scala.util.Random
import kamon.prometheus.PrometheusReporter
import kamon.Kamon

object HdfsPersistance {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("QuickStart")
    implicit val ec = system.dispatcher
    import org.apache.hadoop.conf.Configuration
    import org.apache.hadoop.fs.FileSystem

      val conf = new Configuration()
      conf.addResource(new Path("/Users/pavel/hadoop/hadoop-3.3.0/etc/hadoop/core-site.xml"))
      conf.set("fs.defaultFS", "hdfs://zeta.avel.local:9000/")

      val pathGenerator = FilePathGenerator((rotationCount: Long, timestamp: Long) => s"/data/plain/$rotationCount-$timestamp")
      val settings =
        HdfsWritingSettings()
          .withOverwrite(true)
          .withNewLine(true)
          .withLineSeparator(System.getProperty("line.separator"))
          .withPathGenerator(pathGenerator)

      val fs: FileSystem = FileSystem.get(conf)

      case class SimpleTrade(id: String, date: String, value: String)

      //val counter = 2000000000
      val counter = 80000
      val source = Source
        .fromIterator(() => (0 to counter).iterator)

      val codec = new DefaultCodec()
      val fsConf = fs.getConf
      codec.setConf(fsConf)


    val result = source
        .map(id => {
          SimpleTrade(Random.nextInt(10).toString, "2020-10-20", s"$id and value")
        })
        .map { row =>
          //Kamon.counter("app.message.counter").withoutTags().increment()
          HdfsWriteMessage(ByteString(row.asJson.noSpaces))
        }
        .via(
          HdfsFlow.data(
            fs,
            SyncStrategy.none,
            RotationStrategy.size(100, FileUnit.MB),
            settings
          )
        )
        .runWith(Sink.ignore)

      result.onComplete {
        case _ => system.terminate()
      }
    }
}
