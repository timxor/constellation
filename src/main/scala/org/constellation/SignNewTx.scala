package org.constellation

import better.files.File
import constellation.{createTransaction, _}
import org.constellation.crypto.KeyUtils._
import org.constellation.primitives._
import java.nio.file.Paths

import constellation._

object SignNewTx extends App {
  val dagDir = System.getProperty("user.home") + "/.dag"
  val acctDir = dagDir + "/acct"
  val keyDir = dagDir + "/key"
  case class TxData(ammt: Long, dst: String, fee: Long = 0L)//todo: add logic for multiple keygen and key/acct storage
  val newTxData = args match {
    case Array(ammt, dst, fee) => TxData(ammt.toLong, dst, fee.toLong)
    case Array() => TxData(420, "ijTest", 1L)
  }
  val keyPair = loadDefaultKeyPair(keyDir)
  val keyPairFile = File(keyDir)
  val acctFile = File(acctDir)
  val (prevTxHash, prevTxCount) =
    if (acctFile.notExists)(fromBase64("baseHash"), 0L)
    else {
    val loadedTx = acctFile.lines.head.x[Transaction]
    (loadedTx.signature, loadedTx.count)
    }
  val src = publicKeyToHex(keyPair.getPublic)
  val signature = signData(prevTxHash)(keyPair.getPrivate)
  val newTx = createTransaction(
    src,
    newTxData.dst,
    newTxData.ammt,
    keyPair,
    prevTxCount + 1,
    true,
    false,
    newTxData.fee,
    signature)
    newTx.jsonAppend(acctDir)
}
