package org.constellation
import com.softwaremill.sttp.Response
import com.typesafe.scalalogging.StrictLogging
import constellation._
import org.constellation.crypto.KeyUtils
import org.constellation.primitives._
import org.constellation.util.APIClient
import org.scalameta.logger

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

class ConstellationApp(
                        val clientApi: APIClient
                      )(implicit val ec: ExecutionContext) extends StrictLogging {

  val channelIdToChannel = scala.collection.mutable.HashMap[String, Channel]()
  def registerChannels(chaMsg: Channel) = channelIdToChannel.update(chaMsg.channelId, chaMsg)

  //todo add auth for redeploy
  def deploy(
              schemaStr: String,
              channelName: String = s"channel_${channelIdToChannel.keys.size + 1}"
            )(implicit ec: ExecutionContext) = {
    val response = clientApi.postNonBlocking[Some[ChannelOpenResponse]]("channel/open", ChannelOpen(channelName, jsonSchema = Some(schemaStr)), timeout = 30 seconds)
    response.map { resp: Option[ChannelOpenResponse] =>
      logger.info(s"ChannelOpenResponse: ${resp.toString}")
      val deployRespChannel = resp.map(msg => Channel(msg.genesisHash, channelName, msg))
      deployRespChannel.foreach(registerChannels)
      deployRespChannel
    }
  }

  def broadcast[T <: ChannelRequest](messages: Seq[T])(implicit ec: ExecutionContext): Future[ChannelSendResponse] = {
    val msgType = messages.map(_.channelId).head//todo handle multiple message types, or throw error
    val serializedMessages = messages.map(_.json)
    clientApi.postNonBlocking[ChannelSendResponse](
      "channel/send",
      ChannelSendRequest(msgType, serializedMessages)
    )
  }
}

case class Channel(channelId: String, channelName: String, channelOpenRequest: ChannelOpenResponse)

