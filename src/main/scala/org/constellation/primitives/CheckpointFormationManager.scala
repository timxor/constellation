package org.constellation.primitives

import java.time.{LocalDateTime, Duration => JDuration}
import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import com.typesafe.scalalogging.StrictLogging
import constellation.{EasyFutureBlock, futureTryWithTimeoutMetric}
import org.constellation.DAO
import org.constellation.consensus.CrossTalkConsensus.StartNewBlockCreationRound
import org.constellation.consensus.EdgeProcessor
import org.constellation.primitives.Schema.NodeState
import org.constellation.util.Periodic

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Random, Try}

class CheckpointFormationManager(
  periodSeconds: Int = 1,
  undersizedCheckpointThresholdSeconds: Int = 30,
  crossTalkConsensusActor: ActorRef
)(implicit dao: DAO)
    extends Periodic[Try[Option[Boolean]]]("RandomTransactionManager", periodSeconds)
    with StrictLogging {

  def toFiniteDuration(d: java.time.Duration): FiniteDuration = Duration.fromNanos(d.toNanos)

  implicit val ec: ExecutionContextExecutor = dao.edgeExecutionContext

  private var formEmptyCheckpointAfterThreshold: FiniteDuration =
    undersizedCheckpointThresholdSeconds.seconds

  //noinspection ScalaStyle
  override def trigger() = {
    val memPoolCount = dao.threadSafeTXMemPool.unsafeCount
    val elapsedTime = toFiniteDuration(JDuration.between(dao.lastCheckpoint, LocalDateTime.now))
    logger.info(
      s"Attempting to create checkpoint: Mempoolcount: $memPoolCount, elapsedTime: $elapsedTime"
    )

    val minTXInBlock = dao.processingConfig.minCheckpointFormationThreshold

    if ((memPoolCount >= minTXInBlock || (elapsedTime >= formEmptyCheckpointAfterThreshold)) &&
        dao.formCheckpoints &&
        dao.nodeState == NodeState.Ready &&
        !dao.blockFormationInProgress) {

      logger.info("forming checkpoint")

      if (elapsedTime >= formEmptyCheckpointAfterThreshold) {
        // randomize the next threshold window to prevent nodes from synchronizing empty block formation.
        formEmptyCheckpointAfterThreshold = Duration(
          (undersizedCheckpointThresholdSeconds / 2) + Random.nextInt(
            undersizedCheckpointThresholdSeconds
          ),
          TimeUnit.SECONDS
        )
      }

      crossTalkConsensusActor ! StartNewBlockCreationRound
      dao.metrics.updateMetric("blockFormationInProgress", dao.blockFormationInProgress.toString)
    } else { logger.info("Skipping checkpoint formation") }

    Future.successful(Try(None))
  }
}
