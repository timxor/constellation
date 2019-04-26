package org.constellation.trust
import scala.util.Random

object SelfAvoidingWalk {

  final def sample[A](dist: Map[A, Double]): A = {
    val p = scala.util.Random.nextDouble
    val it = dist.iterator
    var accum = 0.0
    while (it.hasNext) {
      val (item, itemProb) = it.next
      accum += itemProb
      if (accum >= p)
        return item  // return so that we don't have to search through the whole distribution
    }
    println(dist)
    sys.error(f"this should never happen")  // needed so it will compile
  }


  def walk(
            selfId: Int,
            currentId: Int,
            nodeMap: Map[Int, TrustNode],
            totalPathLength: Int,
            currentPathLength: Int,
            visited: Set[Int],
            currentTrust: Double
          ): (Int, Double) = {
    if (totalPathLength == currentPathLength) {
      currentId -> currentTrust
    } else {

      val n1 = nodeMap(currentId)
      val visitedNext = visited + currentId

      val normalEdges = n1.normalizedPositiveEdges(visitedNext)

      if (normalEdges.isEmpty) {
        currentId -> currentTrust
      } else {


        val transitionDst = sample(normalEdges)

        // Ignore paths that distrust self (maybe consider ignoring paths that distrust immediate
        // neighbors as well? This is where Jaccard distance is important
        // We need to discard walks where large distance exists from previous
        // (i.e. discard information from distant nodes if they distrust nearby nodes that you trust in general)
        if (nodeMap(transitionDst).edges.exists(edge => edge.trust < 0 && edge.dst == selfId)) {
          currentId -> currentTrust
        } else {

          val transitionTrust = normalEdges(transitionDst)

          val productTrust = currentTrust * transitionTrust

          /*
        println(
          s"currentLength $currentPathLength " +
          s"on $currentId " +
          s"visiting $transitionDst " +
          s"with transition trust $transitionTrust " +
          s"product $productTrust " +
          s"visited $visitedNext"
        )
     */

          walk(
            selfId,
            transitionDst,
            nodeMap,
            totalPathLength,
            currentPathLength + 1,
            visitedNext,
            productTrust
          )
        }
      }
    }
  }

  def main(args: Array[String]): Unit = {

    val nodes = DataGeneration.generateTestData()

    val nodeMap = nodes.map{n => n.id -> n}.toMap

    val n1 = nodes.head

    val maxPathLength = nodes.size - 1

    def walkFromOrigin() = {
      val totalPathLength = Random.nextInt(maxPathLength - 1) + 1
      walk(n1.id, n1.id, nodeMap, totalPathLength, 0, Set(n1.id), 1D)
    }

    val walkScores = Array.fill(nodes.size)(0D)

    for (_ <- 0 to 100000) {
      val (id, trust) = walkFromOrigin()
    //  println(s"Returning $id with trust $trust")
      if (id != n1.id) {
        walkScores(id) += trust
      }
    }

    val sumScore = walkScores.sum
    val walkProbability = walkScores.map{_ / sumScore}

    walkProbability.zipWithIndex.foreach{println}

    n1.positiveEdges.foreach{println}

    val weightedEdgesAll = Array.fill(nodes.size)(0D)

    walkProbability.zipWithIndex.foreach{
      case (prob, id) =>
        // Same issue here as above, need to discard information from untrustworthy original nodes in event
        // walk accidentally trusts them. -- See Jaccard distance comment
        nodeMap(id).edges.foreach{
          e => weightedEdgesAll(e.dst) += e.trust * prob
        }
    }
    println("Weighted edges all")

    // TODO: Normalize again
    weightedEdgesAll.foreach{println}


    println(s"n1 id: ${n1.id}")

  }
}