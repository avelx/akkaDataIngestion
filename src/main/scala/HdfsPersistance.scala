import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.alpakka.hdfs.scaladsl.HdfsFlow
import akka.stream.alpakka.hdfs._
import akka.stream.scaladsl.{Compression, Sink, Source}
import akka.util.ByteString
import org.apache.hadoop.io.SequenceFile.CompressionType
import org.apache.hadoop.io.Text
import org.apache.hadoop.io.compress.zlib.ZlibCompressor.CompressionLevel
import org.apache.hadoop.io.compress.{CompressionCodec, DefaultCodec, GzipCodec}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.apache.hadoop.fs.Path
import org.apache.hadoop.util.NativeCodeLoader

import scala.util.Random

object HdfsPersistance {
  def main(args: Array[String]): Unit = {

    import org.apache.hadoop.conf.Configuration
    import org.apache.hadoop.fs.FileSystem

    implicit val system = ActorSystem("QuickStart")

    val conf = new Configuration()
    //conf.addResource(new Path("/Users/pavel/hadoop/hadoop-3.3.0/etc/hadoop/core-site.xml") )
    conf.set("fs.defaultFS", "hdfs://192.168.0.8:9000/")
    conf.set("io.compression.codecs",
      "org.apache.hadoop.io.compress.GzipCodec,org.apache.hadoop.io.compress.DefaultCodec,org.apache.hadoop.io.compress.BZip2Codec")
    conf.set("spark.io.compression.codec", "org.apache.hadoop.io.compress.GzipCodec")

    val pathGenerator = FilePathGenerator((rotationCount: Long, timestamp: Long) => s"/data/compressed/b/$rotationCount-$timestamp")
    val settings =
      HdfsWritingSettings()
        .withOverwrite(true)
        .withNewLine(true)
        .withLineSeparator(System.getProperty("line.separator"))
        .withPathGenerator(pathGenerator)

    val fs: FileSystem = FileSystem.get(conf)

    case class SimpleTrade(id: String, date: String, value: String)
    case class Row(id: String, json: String)

    //val counter = 2000000000
    val counter = 1000
    val source = Source
      .fromIterator( () => (0 to counter).iterator)

    val codec = new GzipCodec()
    val fsConf = fs.getConf
    codec.setConf(fsConf)

    val result = source
      .map( id => {
        val trade = SimpleTrade(Random.nextInt(10).toString, "2020-10-20", s"$id and value")
        Row (id.toString, trade.asJson.noSpaces)
      })
      .map { row =>
        HdfsWriteMessage( ByteString(row.json) )
        //HdfsWriteMessage((new Text(row.id), new Text(row.json)))
      }
      .via(
        HdfsFlow.compressed(
                    fs,
                    SyncStrategy.none,
                    RotationStrategy.size(100, FileUnit.MB),
                    codec,
                    settings
                  )
//        HdfsFlow.sequence(
//          fs,
//          SyncStrategy.none,
//          RotationStrategy.size(100, FileUnit.MB),
//          CompressionType.BLOCK,
//          codec,
//          settings,
//          classOf[Text],
//          classOf[Text]
//        )
      )
      .runWith(Sink.ignore)

    implicit val ec = system.dispatcher
    result.onComplete {
      case _ => system.terminate()
    }
  }
}
