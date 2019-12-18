package flybrain571

/** Denotation of experimental treatment, (perhaps poorly named) */
sealed trait Marker

/** No RNAi */
final case object Mcherry extends Marker

/** RNAi KD of Ime4 */
final case object Ime4 extends Marker

/**
 * Implicits for working with denotation of experimental treatment
 *
 * @author Vince Reuter
 */
object Marker {
  import cats._, cats.instances.string._, cats.syntax.eq._

  implicit val eqMarker: Eq[Marker] = new Eq[Marker] {
    def eqv(a: Marker, b: Marker): Boolean = (a, b) match {
      case (Mcherry, Mcherry) => true
      case (Ime4, Ime4) => true
      case _ => false
    }
  }

  /** Attempt to parse marker from raw text */
  def read = (s: String) => {
    if (s === Mcherry.toString) Some(Mcherry)
    else if (s === Ime4.toString) Some(Ime4)
    else Option.empty[Marker]
  }

  implicit val showMark: Show[Marker] = Show.fromToString[Marker]

}
