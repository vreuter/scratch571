package flybrain571

sealed trait Marker
final case object Mcherry extends Marker
final case object Ime4 extends Marker

object Marker {
  import cats._
  implicit val eqMarker: Eq[Marker] = new Eq[Marker] {
    def eqv(a: Marker, b: Marker): Boolean = (a, b) match {
      case (Mcherry, Mcherry) => true
      case (Ime4, Ime4) => true
      case _ => false
    }
  }
  implicit val showMark: Show[Marker] = Show.show((_: Marker) match {
    case Mcherry => "Mcherry"
    case Ime4 => "Ime4"
  })
}
