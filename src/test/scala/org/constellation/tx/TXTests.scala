package org.constellation.tx

import org.constellation.Fixtures
import org.constellation.primitives.ChannelMessage.create
import org.constellation.primitives._
import org.scalatest.FlatSpec
import org.constellation.primitives.Schema.SendToAddress
import constellation._

class TXTests extends FlatSpec {

  "TX hashes" should "split evenly" in {}

  "Send json request" should "parse correctly" in {

    import constellation._

    assert("""{"dst": "asdf", "amount": 1}""".x[SendToAddress] == SendToAddress("asdf", 1))

    assert(
      """{"dst": "asdf", "amount": 1, "normalized": false}""".x[SendToAddress] ==
        SendToAddress("asdf", 1, normalized = false)
    )

  }

  "Message distance" should "match threshold" in {


    Seq.tabulate(100) { i =>
      val channelOpenRequest = ChannelOpen(i.toString)
      val genesisMessageStr = channelOpenRequest.json
      val msg =
        create(genesisMessageStr, Genesis.CoinBaseHash, channelOpenRequest.name)(Fixtures.kp)
      val hash = msg.signedMessageData.hash
      val distance = BigInt(hash.getBytes()) ^ Fixtures.id.bigInt
      distance
    }
  }

  "Message serialize" should "work with json" in {

    val channelOpenRequest = ChannelOpen("channel", Some(SensorData.jsonSchema))
    val genesisMessageStr = channelOpenRequest.json
    val msg = create(genesisMessageStr, Genesis.CoinBaseHash, channelOpenRequest.name)(Fixtures.kp)
    val hash = msg.signedMessageData.hash
    val md = ChannelMetadata(channelOpenRequest, ChannelMessageMetadata(msg))

    assert(md.json.x[ChannelMetadata] == md)

  }

  // distances.foreach{println}



}
