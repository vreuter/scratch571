package flybrain571

import Refinement._

sealed trait Identifiable {
  def ipStatus: IPStatus
  def marker: Marker
  def hasHeatShock: Boolean
  def replicate: Zpos

}

final case class SampleID(ip: IPStatus, marker: Marker, hs: Boolean, rep: Replicate) extends Identifiable {
  def replicate = rep.get
  def ipStatus = ip
  def hasHeatShock = hs
}
