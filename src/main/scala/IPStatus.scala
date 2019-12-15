package flybrain571

sealed trait IPStatus
final case object Input extends IPStatus
final case object Positive extends IPStatus
final case object Negative extends IPStatus

object IPStatus {
  import cats._
  implicit val eqIP: Eq[IPStatus] = new Eq[IPStatus] {
    def eqv(a: IPStatus, b: IPStatus): Boolean = (a, b) match {
      case (Input, Input) => true
      case (Negative, Negative) => true
      case (Positive, Positive) => true
      case _ => false
    }
  }
}
