package org.constellation

import java.util.concurrent.ForkJoinPool

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import better.files.File
import com.typesafe.scalalogging.Logger
import org.constellation.crypto.KeyUtils
import org.constellation.primitives.{IPManager, PeerManager, RandomTransactionManager}
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}
import scala.util.Try
import org.constellation.util.{Metrics, TestNode}

class SingleNodeGenesisTest extends FlatSpec with BeforeAndAfterAll {

  val logger = Logger("SingleNodeGenesisTest")

  val tmpDir = "tmp"

  implicit val system: ActorSystem = ActorSystem("ConstellationTestNode")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override def beforeAll(): Unit = {
    // Cleanup DBs
    //Try{File(tmpDir).delete()}
    //Try{new java.io.File(tmpDir).mkdirs()}
  }

  override def afterAll() {
    // Cleanup DBs
    TestNode.clearNodes()
    system.terminate()
    Try { File(tmpDir).delete() }
  }

  def createNode(
    randomizePorts: Boolean = false,
    portOffset: Int = 0,
    isGenesisNode: Boolean = false,
    randomTxRate: Option[Int] = None
  ): ConstellationNode = {
    implicit val executionContext: ExecutionContextExecutorService =
      ExecutionContext.fromExecutorService(new ForkJoinPool(100))

    TestNode(
      randomizePorts = randomizePorts,
      portOffset = portOffset,
      seedHosts = Seq(),
      isGenesisNode = isGenesisNode,
      randomTxRate = randomTxRate
    )
  }

  val dummyConfig = NodeConfig(isGenesisNode = true,
    randomTxRate = Option(3501)
  )

//  val node = createNode(isGenesisNode = true, randomTxRate = Option(3501))//todo remove and create
//  val nodeConfig = node.nodeConfig
  "Genesis created" should "verify the node has created genesis" in {

    val node = createNode(isGenesisNode = true)
    val api = node.getAPIClient()

    Thread.sleep(6000*1000)

  }

//  "Transaction mempools" should "have the same contents" in {//TODO see note about tx's pas mempool threshold getting into lru cache
////    val node = createNode(isGenesisNode = true, randomTxRate = Option(3501))
////    val api = node.getAPIClient()
//    implicit val dao: DAO = new DAO()
//    dao.initialize(dummyConfig)
//    dao.metrics = new Metrics(periodSeconds = dao.processingConfig.metricCheckInterval)
//
//    val ipManager = IPManager()
//
//    dummyConfig.seeds.foreach { peer =>
//      ipManager.addKnownIP(peer.host)
//    }
//
//    dao.peerManager = system.actorOf(
//      Props(new PeerManager(ipManager)),
//      s"PeerManager_${dao.publicKeyHash}"
//    )
//    val randomTXManager = new RandomTransactionManager(dummyConfig.randomTxRate.get)(dao)
//    Thread.sleep(30*1000)//TODO set appropriate time window and/or use future
//    assert(dao.threadSafeTXMemPool.unsafeCount == 3500)
//    assert(dao.transactionService.memPool.cacheSize == 3500)
//
//  }

//  "CheckpointBlock creation" should "occur even when empty" in {
//    val node = createNode(isGenesisNode = true)
//    val api = node.getAPIClient()
//    //Thread.sleep(6000*1000)//TODO set appropriate time window and/or use future
//    //query cpb mempool
//  }
//
//  "CheckpointBlock contents" should "equal mempool contents" in {
//    val node = createNode(isGenesisNode = true)
//    val api = node.getAPIClient()
//    //Thread.sleep(6000*1000)//TODO set appropriate time window and/or use future
//    //query cpb mempool
//  }

//  "CheckpointBlock contents" should "equal mempool contents" in {
//    val node = createNode(isGenesisNode = true)
//    val api = node.getAPIClient()
//    //Thread.sleep(6000*1000)//TODO set appropriate time window and/or use future
//    //query cpb mempool
//  }

}
