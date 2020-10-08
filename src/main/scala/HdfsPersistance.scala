import java.nio.file.Paths

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.alpakka.hdfs.scaladsl.HdfsFlow
import akka.stream.alpakka.hdfs.{FilePathGenerator, FileUnit, HdfsWriteMessage, HdfsWritingSettings, OutgoingMessage, RotationMessage, RotationStrategy, SyncStrategy, WrittenMessage}
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source}
import akka.util.ByteString

object HdfsPersistance {
  def main(args: Array[String]): Unit = {

    import org.apache.hadoop.conf.Configuration
    import org.apache.hadoop.fs.FileSystem
    implicit val system = ActorSystem("QuickStart")

    val file = Paths.get("/Users/pavel/devcore/data/passthrough.txt")

    val conf = new Configuration()
    conf.set("fs.default.name", "hdfs://localhost:9000")

    val pathGenerator = FilePathGenerator( (rotationCount: Long, timestamp: Long) => s"/data/$rotationCount-$timestamp")
    val settings =
      HdfsWritingSettings()
        .withOverwrite(true)
        .withNewLine(false)
        .withLineSeparator(System.getProperty("line.separator"))
        .withPathGenerator(pathGenerator)


    val fs: FileSystem = FileSystem.get(conf)
    val elements = (0 to 10000).map(s => s"$s\n")
    val source = Source(elements)

    def hdfsMsgToString: Flow[OutgoingMessage[String], String, NotUsed] =
    Flow[OutgoingMessage[String]].collect{
      case WrittenMessage(json, _) => json
    }
    val result = source
      .map { json =>
        HdfsWriteMessage(ByteString(json), json)
      }
      .via(
        HdfsFlow.dataWithPassThrough(
          fs,
          SyncStrategy.count(500),
          RotationStrategy.count(1000),
          settings
        )
      )
      .via(hdfsMsgToString)
      .via( Flow[String].map( ByteString(_)) )
      .runWith(FileIO.toPath(file))

      implicit val ec = system.dispatcher
      result.onComplete {
        case _ => system.terminate()
      }
  }
}
