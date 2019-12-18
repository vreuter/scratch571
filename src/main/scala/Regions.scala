package flybrain571

import com.typesafe.scalalogging.StrictLogging

/**
 * Tools for working with genomic regions
 *
 * @author Vince Reuter
 */
object Regions extends StrictLogging {
  
  import java.io.{ BufferedWriter, File, FileWriter }
  import scala.io.Source
  import cats.data.{ NonEmptyList => NEL }
  import mouse.boolean._
  import FastaTools._, Refinement.{ ExtantFile }
  
  /**
   * Parse pairs of geneID and coordinates from BED.
   *
   * @param bed The BED to parse
   * @return Collection of tuples of chromosome name, coordinates, and gene ID
   */
  def regionsFromBed = (bed: ExtantFile) => Source.fromFile(bed.value).getLines.foldLeft(
    Vector.empty[(String, Range, String)]){ case (recs, line) => {
      val fields = line.split("\t")
      val newRec = (fields(0), (fields(1).toInt -> fields(2).toInt), fields(3))
      recs :+ newRec
    } }

}
