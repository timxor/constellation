package org.constellation.primitives.concurrency
import java.util.concurrent.atomic.AtomicInteger

import cats.effect.concurrent.Semaphore
import cats.effect.{ContextShift, IO}
import org.scalatest.{Matchers, WordSpec}
import cats.Parallel
import cats.implicits._

import scala.concurrent.ExecutionContext

class SingleLockTest extends WordSpec with Matchers {
  "SingleLock" should {
      "not allow concurrent modifications of same resource" in {
        implicit val ioContextShift: ContextShift[IO] =
          IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)
        lazy val throwError: Unit = {
          throw new RuntimeException("throwError")
        }
        implicit val timer = IO.timer(ExecutionContext.global)


        Semaphore[IO](1).map { s =>
          println("here")
        new SingleLockB[IO]("R1", s).get
        }
//          for {
//            s  <- Semaphore[IO](1)
//            r1 = new SingleLock[IO, Unit]("R1", s, IO{println("do op")})
//            r2 = new SingleLock[IO, Unit]("R2", s, IO{{throwError}})
//            r3 = new SingleLock[IO, Unit]("R3", s, IO{println("do op")})
//            _  <- List(r1.acquire, r2.acquire, r3.acquire).parSequence.void
//          } yield ()
      }
    "Handle exceptions thrown" in {
//      implicit val ioContextShift: ContextShift[IO] =
//        IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)
//
//      val counter = new AtomicInteger()
//      counter.set(0)
//      val program: IO[Unit] =
//        for {
//          s  <- Semaphore[IO](1)
//          r1 = new SingleLock[IO, Unit]("R1", s, )
//          r2 = new SingleLock[IO, Unit]("R2", s)
//          r3 = new SingleLock[IO, Unit]("R3", s)
//          _  <- List(r1.use, r2.use, r3.use).parSequence.void
//        } yield ()
    }
    }
  }
