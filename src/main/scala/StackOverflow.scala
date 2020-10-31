import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.alpakka.hdfs._
import akka.stream.alpakka.hdfs.scaladsl.HdfsFlow
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import org.apache.hadoop.io.compress.GzipCodec

object StackOverflow {
  def main(args: Array[String]): Unit = {

    import org.apache.hadoop.conf.Configuration
    import org.apache.hadoop.fs.FileSystem
    implicit val system = ActorSystem("QuickStart")

    val file = Paths.get("/Users/pavel/devcore/data/passthrough.txt")

    val conf = new Configuration()
    conf.set("fs.defaultFS", "hdfs://192.168.0.8:9000/")

    val pathGenerator = FilePathGenerator( (rotationCount: Long, timestamp: Long) => s"/data/$rotationCount-$timestamp")
    val settings =
      HdfsWritingSettings()
        .withOverwrite(true)
        .withNewLine(true)
        .withLineSeparator(System.getProperty("line.separator"))
        .withPathGenerator(pathGenerator)

    val fs: FileSystem = FileSystem.get(conf)

    val elements = (0 to 100000).map(s => s"$s\n")
    val source = Source(elements)

    val codec = new GzipCodec()
    codec.setConf(fs.getConf)

    val result = source
      .map { json =>
        HdfsWriteMessage(ByteString(json))
      }
      .via(
        HdfsFlow.compressed(
          fs,
          SyncStrategy.count(1000),
          RotationStrategy.count(50000),
          codec,
          settings
        )
      )
      .runWith(Sink.ignore)

    implicit val ec = system.dispatcher
    result.onComplete {
      case _ => system.terminate()
    }
  }
}
