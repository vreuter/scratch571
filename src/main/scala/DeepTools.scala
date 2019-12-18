package flybrain571

/**
 * Functions for working with {@code deepTools}
 *
 * @author Vince Reuter
 */
object DeepTools {
  
  import java.io.File
  import cats.data.{ NonEmptyList => NEL }
  import mouse.boolean._
  import Refinement.ExtantFile, ExtantFile._
  
  /**
   * Get command to summarize BAM data over BED regions with {@code deepTools}
   *
   * @param bed The regions of interest
   * @param bams The aligned reads data
   * @param outfile Where to write output
   * @return The command with which to summarize aligned reads data over regions
   */
  def bamsOverBedSummaryCommand(bed: ExtantFile)(bams: NEL[ExtantFile])(outfile: File): String = 
    s"multiBamSummary BED-file --BED ${bed.value} --bamfiles ${bams.toList.map(_.value).mkString(" ")} -o $outfile"

  /*
  /** {@code deepTools} correlation method keys */
  val corPlotMethods = List("pearson", "spearman")

  /** {@code deepTools} correlation plot type keys */
  val corPlotTypes = List("heatmap", "scatterplot")

  def plotCorrelationCommand(method: String, plotType: String)(dataFile: File, plotFile: File): Either[String, String] = for {
    m <- corPlotMethods.contains(method).either(
      s"Illegal correlation method (choose from ${corPlotMethods.mkString(", ")}): $method", ())
    p <- corPlotTypes.contains(plotType).either(
      s"Illegal plot type (choose from ${corPlotTypes.mkString(", ")}): $plotType", ())
  } yield s"plotCorrelation --corMethod $method --whatToPlot $plotType --corData $dataFile --plotFile $plotFile"

  def plotBamsOverBedSummaryCommand(bed: ExtantFile, bams: NEL[ExtantFile])(
    method: String, plotType: String)(dataFile: File, plotFile: File): Either[String, (String, String)] = 
    plotCorrelationCommand(method, plotType)(dataFile, plotFile).map(
      plotCmd => bamsOverBedSummaryCommand(bed)(bams)(dataFile) -> plotCmd)
  */

}
