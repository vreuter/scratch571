package flybrain571

/**
 * Helper types with refined domain
 *
 * @author Vince Reuter
 */
object Refinement {

  import java.io.File
  import cats._
  import eu.timepit.refined.{ refineV }
  import eu.timepit.refined.api.{ Refined, Validate }
  import eu.timepit.refined.numeric._

  // Type for RHS of File existence refinement
  protected final case class ExtantFileChecked()

  /** Positive integer */
  type Zpos = Refined[Int, Positive]

  /** Path validated as pointing to file */
  type ExtantFile = Refined[File, ExtantFileChecked]
  
  /** Implicits and syntactic convenience for refinement of filepaths as existing files. */
  object ExtantFile {
    /** Check that given path is an extant file. */
    implicit val ValidateExtantFile: Validate.Plain[File, ExtantFileChecked] = 
      Validate.fromPredicate(_.isFile, p => s"(not a file: ${p.getPath})", ExtantFileChecked())
    /** Safely refine by checking the path as a file. */
    def apply(f: File): Either[String, ExtantFile] = refineV[ExtantFileChecked](f)
    /** Force the refinement as extant file, throwing exception if not. */
    def unsafe(f: File): ExtantFile = apply(f).fold(e => throw new Exception(e), identity _)
    /** Display an extant file by its path. */
    implicit val showExtantFile: Show[ExtantFile] = Show.show(_.value.getPath)
    /** Generalize refined extant file as ordinary {@code File} */
    implicit def widenExtantFile: ExtantFile => File = _.value
  }
  
  /** Syntax and implicits for positive integer */
  object Zpos {
    import cats.implicits._
    def apply(x: Int): Either[String, Zpos] = refineV[Positive](x)
    implicit val showZpos: Show[Zpos] = Show.show(_.value.toString)
    implicit def getZposVal: Zpos => Int = _.value
    implicit val eqZpos: Eq[Zpos] = Eq.by(_.value)
  }

}
