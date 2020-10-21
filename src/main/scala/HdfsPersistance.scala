import java.nio.file.Paths

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.alpakka.hdfs.scaladsl.HdfsFlow
import akka.stream.alpakka.hdfs.{FilePathGenerator, FileUnit, HdfsWriteMessage, HdfsWritingSettings, OutgoingMessage, RotationMessage, RotationStrategy, SyncStrategy, WrittenMessage}
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source}
import akka.util.ByteString
import org.apache.hadoop.io.compress.zlib.ZlibCompressor.CompressionLevel
import org.apache.hadoop.io.compress.{CompressionCodec, DefaultCodec, GzipCodec, SnappyCodec}

object HdfsPersistance {
  def main(args: Array[String]): Unit = {

    import org.apache.hadoop.conf.Configuration
    import org.apache.hadoop.fs.FileSystem
    implicit val system = ActorSystem("QuickStart")

    val file = Paths.get("/Users/pavel/devcore/data/passthrough.txt")

    val conf = new Configuration()
    conf.set("fs.defaultFS", "hdfs://192.168.0.8:9000/")
    conf.setBoolean("mapred.compress.map.output", true)
    conf.setClass("mapred.map.output.compression.codec", classOf[GzipCodec], classOf[CompressionCodec])
    conf.setBoolean("mapreduce.output.fileoutputformat.compress", true)
    conf.setClass("mapreduce.output.fileoutputformat.compress.codec", classOf[GzipCodec], classOf[CompressionCodec])
    conf.set("mapreduce.output.fileoutputformat.compress.type", "BLOCK")
    conf.set("io.compression.codecs",
      "org.apache.hadoop.io.compress.GzipCodec,org.apache.hadoop.io.compress.DefaultCodec")

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

    def hdfsMsgToString: Flow[OutgoingMessage[String], String, NotUsed] =
    Flow[OutgoingMessage[String]].collect{
      case WrittenMessage(json, _) => json
    }
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
      //.via(hdfsMsgToString)
      //.via( Flow[String].map( ByteString(_)) )
      .runWith(Sink.ignore)

      implicit val ec = system.dispatcher
      result.onComplete {
        case _ => system.terminate()
      }
  }
}
