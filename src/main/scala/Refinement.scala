package flybrain571

object Refinement {
  import cats._
  import eu.timepit.refined.{ refineV }
  import eu.timepit.refined.api.{ Refined }
  import eu.timepit.refined.numeric._

  type Zpos = Refined[Int, Positive]

  /** Syntax and implicits for positive integer */
  object Zpos {
    import cats.implicits._
    def apply(x: Int): Either[String, Zpos] = refineV[Positive](x)
    implicit val showZpos: Show[Zpos] = Show.show(_.value.toString)
    implicit def getZposVal: Zpos => Int = _.value
    implicit val eqZpos: Eq[Zpos] = Eq.by(_.value)
  }

}
