package flybrain571

import Refinement._

/**
 * Data for sample identity constitution
 *
 * @author Vince Reuter
 */
sealed trait Identifiable {
  def antibody: Antibody
  def marker: Marker
  def hasHeatShock: Boolean
  def replicate: Zpos
}

/**
 * Simple sample identity implementation
 *
 * @param ab The antibody/IP status combo for this sample
 * @param marker The marker for this sample
 * @param hs Whether this sample had heat shock treatment
 * @param rep Replicate ID for this sample identity
 * @return Newly minted sample ID instance
 * @author Vince Reuter
 */
final case class SampleID(ab: Antibody, marker: Marker, hs: Boolean, rep: Replicate) extends Identifiable {
  def replicate = rep.get
  def hasHeatShock = hs
  def antibody = ab
  def nonRepID: (Antibody, Marker, Boolean) = (ab, marker, hs)

}

/**
 * Implicits and utilities for working with {@code SampleID}
 *
 * @author Vince Reuter
 */
object SampleID {
  import scala.annotation.tailrec
  import cats.instances.boolean._
  import cats.instances.tuple._            // For element-wise Eq derivation
  import cats.syntax.eq._, cats.syntax.show._
  import mouse.boolean._
  import Antibody._, Marker._
  import Zpos._

  type NonRepID = (Antibody, Marker, Boolean)

  /**
   * Determine whether two sample IDs are replicates.
   *
   * @param a One ID to compare
   * @param b Other ID to compare
   * @return whether the IDs are replicates (identical save for replicate index)
   * @throws Exception if the IDs are entirely identical
   */
  def replicates = (a: SampleID, b: SampleID) => (a, b) match {
    case (SampleID(ab1, m1, shock1, rep1), SampleID(ab2, m2, shock2, rep2)) => {
      if (ab1 === ab2 && m1 === m2 && shock1 === shock2) {
        (rep1 =!= rep2).fold(true, throw new Exception(s"Sample IDs tested as replicates are identical. A: ${a}. B: ${b}") )
      } else { false }
    }
  }

  @tailrec
  private[this] def findRepGroup(groups: Vector[NonRepID])(subID: NonRepID, currIndex: Int): Option[Int] = {
    if (groups.isEmpty) Option.empty[Int]
    else if (groups.head === subID) Some(currIndex)
    else findRepGroup(groups.tail)(subID, currIndex + 1)
  }

  def groupReplicates = (samples: Iterable[SampleID]) => samples.foldRight(Vector.empty[(NonRepID, Vector[SampleID])]){
    case (sid, acc) => findRepGroup(acc.map(_._1))(sid.nonRepID, 0) match {
      case None => (acc :+ (sid.nonRepID -> Vector(sid)))
      case Some(i) => {
        val (subSID, prevGroup) = acc(i)
        acc.patch(i, Vector((subSID, (prevGroup :+ sid))), 1)
      }
    }
  }

  def groupReplicateData[V]: Iterable[(SampleID, V)] => Vector[(NonRepID, Vector[V])] = 
    data => groupReplicates(data.map(_._1)) map { 
      case (id, _) => id -> data.toVector.filter(_._1.nonRepID === id).map(_._2) }

}
