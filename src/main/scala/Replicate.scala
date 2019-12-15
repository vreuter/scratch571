package flybrain571

import Refinement._

/** A replicate is a domain-specific wrapper */
final case class Replicate private(get: Zpos)

/**
 * Functionality for working with replicate identifiers
 *
 * @author Vince Reuter
 */
object Replicate {
  import cats._, cats.implicits._
  import mouse.boolean._
  /** Create replicate identifier by validation of positive integral membership, providing message if not. */
  def apply(i: Int): Option[Replicate] = Zpos(i).toOption.map(z => new Replicate(z))
  /** Create replicate identifier by validation of positive integral membership, thowing error if not. */
  def unsafe(i: Int): Replicate = apply(i).getOrElse(throw new Exception(s"Illegal replicate (must be in Z+): $i"))
  /** Replicates are equivalent iff their values are equal. */
  implicit val eqReplicate: Eq[Replicate] = Eq.by(_.get.value)
}
