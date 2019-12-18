package flybrain571

/**
 * Functionality for interacting with {@code samtools}
 *
 * @author Vince Reuter
 */
object Samtools {
  import java.io.File
  import cats.data.{ NonEmptyList => NEL }
  import Refinement.ExtantFile

  /** Create command to calculate coverage depth over target regions and write results to disk. */
  def depthCommandBamsOverBed(bed: ExtantFile)(bamsListFile: ExtantFile, outfile: File): String = 
    s"samtools depth -b ${bed.value} -f ${bamsListFile.value} -o $outfile"

}
