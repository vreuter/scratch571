package flybrain571

import Refinement._

sealed trait Identifiable {
  def antibody: Antibody
  def marker: Marker
  def hasHeatShock: Boolean
  def replicate: Zpos
}

final case class SampleID(ab: Antibody, marker: Marker, hs: Boolean, rep: Replicate) extends Identifiable {
  def replicate = rep.get
  def hasHeatShock = hs
  def antibody = ab
}
