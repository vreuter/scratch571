package flybrain571

sealed trait Antibody
final case class Nebl(ip: Boolean) extends Antibody
final case class SynSys(ip: Option[Boolean]) extends Antibody

object Antibody {
  import cats._

  val neblAlias = "NEB_Antibody"
  val synSysAlias = "SynapticSystems_Antibody"
  val inputTreatmentText = "Input"
  val neblPosText = "m6AIP"
  val ssNegText = "Negative"
  val ssPosText = "Positive"
  
  private[this] def readNeblIP(s: String): Either[String, Boolean] = s match {
    case `inputTreatmentText` => Right(false)
    case `neblPosText` => Right(true)
    case _ => Left(s"Invalid NEBL antibody IP text: $s")
  }

  private[this] def readSynSysIP(s: String): Either[String, Option[Boolean]] = s match {
    case `inputTreatmentText` => Right(Option.empty[Boolean])
    case `ssNegText` => Right(Some(false))
    case `ssPosText` => Right(Some(true))
    case _ => Left(s"Invalid SS antibody IP text: $s")
  }

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

  def read(abText: String, ipText: String): Either[String, Antibody] = abText match {
    case `neblAlias` => readNeblIP(ipText).map(ip => Nebl(ip))
    case `synSysAlias` => readSynSysIP(ipText).map(ipOpt => SynSys(ipOpt))
    case _ => Left(s"Invalid antibody text: $abText")
  }

}
