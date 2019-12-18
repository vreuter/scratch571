package flybrain571

/**
 * Functionality for interacting with {@code bedtools}
 *
 * @author Vince Reuter
 */
object Bedtools {

  import java.io.File
  import cats.data.{ NonEmptyList => NEL }
  import Refinement.ExtantFile
  
  /**
   * Create command for getting BED3 + geneID, base-level offset from peak start, and converage count.
   *
   * @param regBed Path to file with regions of interest, e.g. peak calls, in BED format
   * @param bams Collection of alignment files to count for overlaps with regions
   * @param outfile Where to write output
   * @return Command to run to perform the desired action
   */
  def getDepthCommand(regBed: ExtantFile)(bams: NEL[ExtantFile], outfile: File): String = 
    s"bedtools coverage -d -a ${regBed.value} -b ${bams.toList.map(_.value).mkString(" ")} | cut -f 1-4,13-14 > $outfile"

}
