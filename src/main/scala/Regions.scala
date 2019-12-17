package flybrain571

import com.typesafe.scalalogging.StrictLogging

object Regions extends StrictLogging {
  
  import java.io.File
  import scala.io.Source
  import mouse.boolean._
  import FastaTools._, Refinement.{ ExtantFile }
  
  /**
   * Parse pairs of geneID and coordinates from BED.
   *
   * @param bed The BED to parse
   * @return sequence of pairs of geneID ({@code name} field of BED per {@code MeTPeak}) and coordinates (0-based, incl/excl)
   */
  def regionsFromBed = (bed: ExtantFile) => Source.fromFile(bed.value).getLines.foldLeft(Vector.empty[(String, Range)]){
    case (recs, line) => {
      val fields = line.split("\t")
      val newRec = fields(3) -> (fields(1).toInt -> fields(2).toInt)
      recs :+ newRec
    }
  }

  def readRegionsFile(f: ExtantFile, geneIdIndex: Int = 0, sep: String = "\t"): Vector[(String, Range)] = {
    Source.fromFile(f.value).getLines.foldLeft(Vector.empty[(String, Range)]){ case (recs, line) => {
      val fields = line.split(sep)
      if (fields.size < 3) throw new Exception(s"Too few fields (${fields.size}) from line: $line")
      else {
        val readNumSafe: String => Either[String, Int] = s => {
          try { Right(s.toInt) }
          catch { case _: NumberFormatException => Left(s"Tried to parse $s as coordinate from line: $line") }
        }
        val rec = for {
          start <- readNumSafe(fields(1))
          end <- readNumSafe(fields(2))
        } yield (fields(geneIdIndex) -> (start -> end))
        rec.fold(msg => throw new Exception(msg), r => recs :+ r)
      }
    } }
  }

  def sequenceRegions(fasta: ExtantFile)(bed: ExtantFile): Either[String, Vector[(String, Range, String)]] = {
    logger.info(s"Associating regions from ${bed.value} using sequences from ${fasta.value} ")
    logger.debug("Building sequence map")
    val seqMap = fbFasta2TranscriptExonSeqMap(fasta)
    logger.debug("Sequence map complete")
    val getSeq = regionSeq(seqMap) _
    logger.debug("Starting region processing")
    val (errors, result) = regionsFromBed(bed).foldLeft(
      Vector.empty[String] -> Vector.empty[(String, Range, String)] ){ 
        case ((bads, goods), (id, range)) => 
          getSeq(id, range).fold(
            errMsg => (bads :+ errMsg, goods), 
            seq => (bads, goods :+ (id, range, seq)))
      }
    logger.debug("Region processing complete")
    errors.isEmpty.either(s"${errors.size} error(s); max 5: ${errors.take(5).mkString("\n")}", result)
  }

}
