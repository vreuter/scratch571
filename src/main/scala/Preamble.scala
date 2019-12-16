package flybrain571

import com.typesafe.scalalogging.LazyLogging

/**
 * Project-specific data and such
 *
 * @author Vince Reuter
 */
object Preamble extends LazyLogging {
  import java.io.File
  import cats._, cats.instances.int._, cats.instances.string._
  import cats.syntax.eq._, cats.syntax.show._
  import mouse.boolean._
  import Refinement._, ExtantFile._

  // TODO: GTF for dm6 + possibly annotations
  val readsFileExt = ".sort.bam"
  private[this] val repPrefix = "Rep"
  private[this] val filenameDelimiter = "_"
  
  type RawID = (String, String, String, String, String)

  /** Display a {@code SampleID} as text. */
  implicit val showSampleID: Show[SampleID] = new Show[SampleID] {
    import Antibody._, Marker._, Replicate._
    import Zpos._
    def show(sid: SampleID): String = sid match {
      case SampleID(ab, mark, shock, rep) => {
        val finalizeCondition = ab match {
          case _: Nebl => { (s: String) => s }
          case _: SynSys => { (s: String) => s"SYS${filenameDelimiter}$s" }
        }
        // TODO: fill out the fields.
        val rawCondStr = shock.fold("HS", "control")
        val fields = List(s"${repPrefix}${rep.get.show}", ab.show, mark.show, finalizeCondition(rawCondStr))
        fields.mkString(filenameDelimiter)
      }
    }
  }

  /** Convert a {@code SampleID} to a filepath. */
  def idToFile: SampleID => File = sid => new File(s"${showSampleID.show(sid)}${readsFileExt}")

  private[this] def fnFields: String => Array[String] = fn => {
    logger.debug(s"Getting fields from filename: $fn")
    fn.stripSuffix(readsFileExt).split(filenameDelimiter)
  }

  def rawFromSS(fn: String): Either[String, RawID] = {
    val fields = fnFields(fn)
    val maybeIpMarkPair = {
      if (fields.size === 5) { Right(fields(1) -> fields(2)) } 
      else if (fields.size === 6) { Right(fields(2) -> fields(3)) }
      else { Left(s"SynSys antibody's filename should have 5 or 6 fields; got ${fields.size} from filename: $fn") }
    }
    maybeIpMarkPair map { case(a, b) => (Antibody.synSysAlias, a, b, fields.last, fields(0)) }
  }

  def rawFromNE(fn: String): Either[String, RawID] = {
    val fields = fnFields(fn)
    val fieldCount = 4
    if (fields.size === fieldCount) { Right(Antibody.neblAlias, fields(1), fields(2), fields.last, fields(0)) }
    else { Left(s"Required $fieldCount fields in NEBL antibody filename; got ${fields.size} from $fn") }
  }

  def readShockBool = (s: String) => {
    if (s === "HS") Some(true)
    else if (s === "control") Some(false)
    else Option.empty[Boolean]
  }

  /**
   * Attempt parse of a {@code SampleID} from a filepath.
   *
   * @param f The filepath from which to parse/infer sample ID
   * @return Either a {@code Left} containing an error message, or a {@code Right} containing 
   *         a successfully parsed sample ID
   */
  def idFromFile(f: ExtantFile): Either[String, SampleID] = {
    import cats.syntax.show._
    logger.info(s"Parsing identity from file: ${f.show}")
    val abText = f.value.getParentFile.getName
    val fn = f.value.getName
    val maybeRawID = {
      if (!fn.endsWith(readsFileExt)) Left(s"Invalid reads file (extension must be $readsFileExt): ${f.show}")
      else if (abText === Antibody.neblAlias) rawFromNE(fn)
      else if (abText === Antibody.synSysAlias) rawFromSS(fn)
      else Left(s"Illegal antibody text ($abText) from file: ${f.show}")
    }
    maybeRawID flatMap { case (abStr, ipStr, markStr, condStr, repStr) => for {
      rep <- Replicate(repStr.stripPrefix(repPrefix).toInt).toRight(s"Replicate parse of '$repStr' failed (from ${f.show})")
      //ab <- Antibody.read(abStr, ipStr).toRight(s"Illegal antibody text '$abStr' (from ${f.show})")
      ab <- Antibody.read(abStr, ipStr)
      m <- Marker.read(markStr).toRight(s"Could not parse marker from text '$markStr' (from ${f.show})")
      exp <- readShockBool(condStr).toRight(s"Could not parse shock status from text '$condStr' (from ${f.show})")
    } yield SampleID(ab, m, exp, rep) }
  }

}
