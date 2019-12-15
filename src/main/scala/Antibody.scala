package flybrain571

sealed trait Antibody
final case class Nebl(ip: Boolean) extends Antibody
final case class SynSys(ip: Option[Boolean]) extends Antibody

object Antibody {
  import cats._

  val neblAlias = "NEB_Antibody"
  val synSysAlias = "SynapticSystems_Antibody"
  
  implicit val eqAB: Eq[Antibody] = new Eq[Antibody] {
    import cats.instances.boolean._, cats.syntax.eq._
    def eqv(a: Antibody, b: Antibody): Boolean = (a, b) match {
      case (Nebl(false), Nebl(false)) => true
      case (Nebl(true), Nebl(true)) => true
      case (SynSys(aOpt), SynSys(bOpt)) => aOpt.fold(bOpt.isEmpty)(p => bOpt.fold(false)(_ === p))
      case _ => false
    }
  }
  implicit val showAB: Show[Antibody] = new Show[Antibody] {
    import mouse.boolean._
    def show(ab: Antibody): String = ab match {
      case Nebl(ip) => s"${neblAlias}_${ip.fold("m6AIP", "Input")}"
      case SynSys(ipOpt) => s"${synSysAlias}_${ipOpt.fold("Input")(_.fold("Positive", "Negative"))}"
    }
  }
}
