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
}

/**
 * Implicits and utilities for working with {@code SampleID}
 *
 * @author Vince Reuter
 */
object SampleID {
  import cats.instances.boolean._, cats.syntax.eq._, cats.syntax.show._
  import mouse.boolean._
  import Antibody._, Marker._
  import Zpos._

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
}
