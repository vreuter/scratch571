package flybrain571

sealed trait Marker
final case object Mcherry extends Marker
final case object Ime4 extends Marker

object Marker {
  import cats._, cats.instances.string._, cats.syntax.eq._

  implicit val eqMarker: Eq[Marker] = new Eq[Marker] {
    def eqv(a: Marker, b: Marker): Boolean = (a, b) match {
      case (Mcherry, Mcherry) => true
      case (Ime4, Ime4) => true
      case _ => false
    }
  }

  def read = (s: String) => {
    if (s === Mcherry.toString) Some(Mcherry)
    else if (s === Ime4.toString) Some(Ime4)
    else Option.empty[Marker]
  }

  implicit val showMark: Show[Marker] = Show.fromToString[Marker]

}
