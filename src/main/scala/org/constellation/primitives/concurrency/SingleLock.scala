package org.constellation.primitives.concurrency
import cats.effect.concurrent.Semaphore
import cats.effect.{Concurrent, Timer}
import cats.implicits._

import scala.concurrent.duration._

class SingleLock[F[_], R](name: String, s: Semaphore[F])(op: => F[R])(implicit F: Concurrent[F]){
  def get: F[R] = {
    println("here")
    for {
      x <- s.available
      _ <- F.delay(println(s"$name >> Availability: $x"))
      _ <- s.acquire
      y <- s.available
      _ <- F.delay(println(s"$name >> Started | Availability: $y"))
      res <- op
      _ <- s.release
      z <- s.available
      _ <- F.delay(println(s"$name >> Done | Availability: $z"))
    } yield res
  }

}

class SingleLockB[F[_]](name: String, s: Semaphore[F])//, op: => F[R])
                          (implicit F: Concurrent[F], timer: Timer[F]){
  println("here2")
  def get: F[Unit] ={
    println("here2")
    for {
      x <- s.available
      _ <- F.delay(println(s"$name >> Availability: $x"))
      _ <- s.acquire
      y <- s.available
      _ <- F.delay(println(s"$name >> Started | Availability: $y"))
      res <- timer.sleep(3.seconds)
      _ <- s.release
      z <- s.available
      _ <- F.delay(println(s"$name >> Done | Availability: $z"))
    } yield res
  }

}