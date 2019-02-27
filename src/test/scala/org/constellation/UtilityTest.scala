package org.constellation

import java.security.KeyPair

import org.scalatest.FlatSpec
import org.constellation.crypto.KeyUtils._
import org.constellation.serializer.KryoSerializer
import org.json4s.JsonAST.JValue

//case class Test(a: EdgeHashType, b: EdgeHashType)

// case class Task[U]

case class FakeTask[T, U](
  t: T,
  func: T => U
                         ) {
  def run: U = func(t)
}

class UtilityTest extends FlatSpec {

  // TODO: Test CB serializations
  "Bundles" should "serialize and deserialize properly with json" in {

    implicit val kp: KeyPair = makeKeyPair()

    val task = FakeTask(5, (x: Int) => x + 1)

    val ser = KryoSerializer.serializeAnyRef(task)

    val deser = KryoSerializer.deserializeCast[FakeTask[Any, Any]](ser)

    print(deser.run)

  }

  "Json messages" should "parse from UI request" in {

    val messageExamples: String =
      """[
        |{"temperature": 20, "name": "SFWEATH"},
        |{"temperature": 25, "name": "NYWEATH"},
        |{"temperature": -500, "name": "asdkldzlxkc"}
        |]""".stripMargin

    import constellation._
    println(messageExamples.x[Seq[JValue]].map { _.json })

    //assert(b3.json.x[Bundle] == b3)
  }

  "BigInt hash" should "XOR properly as a distance metric" in {
    /*

    // Use bigint hex for dumping key hashes later.
    val hash = Fixtures.transaction3.hash
    val hash2 = Fixtures.transaction4.hash
    println(hash)
    println(hash2)
    val bi = BigInt(hash, 16)
    val bi2 = BigInt(hash2, 16)
    val xor = bi ^ bi2
    println(bi)
    println(bi2)
    println(xor)
    val xorHash = xor.toString(16)
    println(xorHash)

    val xor2 = bi ^ BigInt(Fixtures.transaction2.hash, 16)
    println(xor > xor2)
   */

  }

  "Case object serialization" should "work" in {

    /*val t = Test(TXHash, AsdfHash)
    println(t.j)
    println(t.j.x[Test])
    assert(t.j.x[Test] == t)
   */
  }

}
