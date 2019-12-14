package flybrain571

import Refinement._

final case class Replicate private(get: Zpos)

object Replicate {
  import mouse.boolean._
  def apply(i: Int): Option[Replicate] = Zpos(i).toOption.map(z => new Replicate(z))
  def unsafe(i: Int): Replicate = apply(i).getOrElse(throw new Exception(s"Illegal replicate (must be in Z+): $i"))
}
