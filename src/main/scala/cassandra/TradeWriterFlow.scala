package cassandra

import akka.stream.alpakka.cassandra.CassandraWriteSettings
import akka.stream.alpakka.cassandra.scaladsl.{CassandraFlow, CassandraSession}
import com.datastax.oss.driver.api.core.cql.{BoundStatement, PreparedStatement}
import model.TradeKey

trait TradeWriterFlow {
  implicit val cassandraSession: CassandraSession

  def writerToCassandra[T] = {
    val statementBinder: (TradeKey, PreparedStatement) => BoundStatement =
      (json, preparedStatement) => preparedStatement.bind(Int.box(json.id), json.date, json.json)
    CassandraFlow.create(CassandraWriteSettings.defaults,
      s"INSERT INTO sand_box.trade_temp(id, date, counterparty, json) VALUES (?, ?, ?, ?)",
      statementBinder)
  }
}
