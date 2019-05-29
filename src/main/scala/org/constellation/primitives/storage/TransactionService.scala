package org.constellation.primitives.storage

import better.files.File
import cats.effect.IO
import com.typesafe.scalalogging.StrictLogging
import org.constellation.DAO
import org.constellation.datastore.swaydb.SwayDbConversions._
import org.constellation.primitives.TransactionCacheData
import org.constellation.util.Periodic
import swaydb.serializers.Default.StringSerializer

import scala.concurrent.{ExecutionContextExecutor, Future}

object TransactionsOld {
  def apply(dao: DAO) = new TransactionsOld(dao.dbPath)(dao.edgeExecutionContext)
}

class TransactionsOld(path: File)(implicit ec: ExecutionContextExecutor)
    extends DbStorage[String, TransactionCacheData](
      dbPath = (path / "disk1" / "transactions_old").path
    )

object TransactionsMid {
  val midCapacity = 1

  def apply(dao: DAO) = new TransactionsMid(dao.dbPath, midCapacity)(dao.edgeExecutionContext)
}

class TransactionsMid(path: File, midCapacity: Int)(implicit ec: ExecutionContextExecutor)
    extends MidDbStorage[String, TransactionCacheData](dbPath =
                                                         (path / "disk1" / "transactions_mid").path,
                                                       midCapacity)

class TransactionMemPool()
    extends StorageService[TransactionCacheData](Some(240))

object TransactionService {
  def apply(implicit dao: DAO) = new TransactionService(dao)
}

class TransactionMidPool() extends TransactionMemPool()
class TransactionOldPool() extends TransactionMemPool()

class TransactionService(dao: DAO)
    extends MerkleService[TransactionCacheData]
    with StrictLogging {
  val merklePool = new StorageService[Seq[String]]()
  val arbitraryPool = new TransactionMemPool()
  val memPool = new TransactionMemPool()
  val midDb: MidDbStorage[String, TransactionCacheData] = TransactionsMid(dao)
  val oldDb: DbStorage[String, TransactionCacheData] = TransactionsOld(dao)

  def getMetricsMap: Map[String, Long] = {
    val q = Map(
      "merklePool" -> merklePool.cacheSize(),
      "arbitraryPool" -> arbitraryPool.cacheSize(),
      "memPool" -> memPool.cacheSize(),
      "midDb" -> midDb.size.toLong,
      "oldDb" -> oldDb.size.toLong
    )
    q
  }

  def migrateOverCapacity(): IO[Unit] = {
    for {
      overage <- midDb.pullOverCapacity()
      kvs = overage.map(tx => tx.transaction.hash -> tx)
      _ <- oldDb.putAll(kvs)
    } yield ()
  }

  override def lookup: String => IO[Option[TransactionCacheData]] =
    DbStorage.extendedLookup[String, TransactionCacheData](List(memPool, midDb, oldDb))

  def contains: String ⇒ IO[Boolean] =
    DbStorage.extendedContains[String, TransactionCacheData](List(memPool, midDb, oldDb))

  override def findHashesByMerkleRoot(merkleRoot: String): IO[Option[Seq[String]]] =
    merklePool.get(merkleRoot)
}

class TransactionPeriodicMigration(periodSeconds: Int = 10)(implicit dao: DAO)
    extends Periodic[Unit]("TransactionPeriodicMigration", periodSeconds) {

  def trigger(): Future[Unit] = {
    dao.transactionService
      .migrateOverCapacity()
      .unsafeToFuture()
  }
}
