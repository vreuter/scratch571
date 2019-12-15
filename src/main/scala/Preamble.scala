package flybrain571

import com.typesafe.scalalogging.LazyLogging

/**
 * Project-specific data and such
 *
 * @author Vince Reuter
 */
object Preamble extends LazyLogging {
  import java.io.File
  import cats._
  import mouse.boolean._
  import MeTPeakProgram.{ ProgramInstance => MPP }
  import Refinement._, ExtantFile._

  // TODO: GTF for dm6 + possibly annotations
  private[this] val readsFileExt = ".sort.bam"
  private[this] val repNameIndex = 0
  private[this] val ipNameIndex = 1
  private[this] val markerNameIndex = 2
  private[this] val repPrefix = "Rep"
  
  val mpProg = MPP()

  /** Display a {@code SampleID} as text. */
  implicit val showSampleID: Show[SampleID] = new Show[SampleID] {
    def show(sid: SampleID): String = sid match {
      case SampleID(ab, mark, shock, rep) => {
        // TODO: fill out the fields.
        //val fields = List(s"${repPrefix}${rep.value}", ab.show, )
        //s"${repPrefix}${rep.value}"
        ???
      }
    }
  }

  /** Convert a {@code SampleID} to a filepath. */
  def id2File: SampleID => File = sid => new File(s"${showSampleID.show(sid)}${readsFileExt}")

  /**
   * Attempt parse of a {@code SampleID} from a filepath.
   *
   * @param f The filepath from which to parse/infer sample ID
   * @return Either a {@code Left} containing an error message, or a {@code Right} containing 
   *         a successfully parsed sample ID
   */
  def identityFromFile(f: ExtantFile): Either[String, SampleID] = {
    import cats.syntax.show._
    logger.info(s"Parsing identity from file: ${f.show}")
    val fn = f.value.getName
    if (!fn.endsWith(readsFileExt)) { Left(s"Invalid reads file (extension must be $readsFileExt): ${f.show}") }
    else {
      val fields = fn.split(".").head.split("_")
      logger.debug(s"${fields.length} fields from filename: ${fields.mkString(", ")}")
      val minNumFields = 4
      (fields.length < minNumFields).either(
        s"Too few fields (got ${fields.length}, ${minNumFields} required, from ${f.show}): ${fields.mkString(", ")}", ()) flatMap {
        _ => ???
      }
    }
  }

}
