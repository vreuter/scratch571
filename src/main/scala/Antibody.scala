package flybrain571

sealed trait Antibody
final case class Nebl(ip: Boolean) extends Antibody
final case class SynSys(ip: Option[Boolean]) extends Antibody

object Antibody {
  import cats._
  implicit val eqAB: Eq[Antibody] = new Eq[Antibody] {
    import cats.instances.boolean._, cats.syntax.eq._
    def eqv(a: Antibody, b: Antibody): Boolean = (a, b) match {
      case (Nebl(false), Nebl(false)) => true
      case (Nebl(true), Nebl(true)) => true
      case (SynSys(aOpt), SynSys(bOpt)) => aOpt.fold(bOpt.isEmpty)(p => bOpt.fold(false)(_ === p))
      case _ => false
    }
  }
}
