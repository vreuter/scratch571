package flybrain571

import com.typesafe.scalalogging.StrictLogging

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
   */
  def regionsFromBed = (bed: ExtantFile) => Source.fromFile(bed.value).getLines.foldLeft(
    Vector.empty[(String, Range, String)]){ case (recs, line) => {
      val fields = line.split("\t")
      val newRec = (fields(0), (fields(1).toInt -> fields(2).toInt), fields(3))
      recs :+ newRec
    } }

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

  def addSeq2Regions(fasta: ExtantFile)(beds: NEL[ExtantFile]): Either[String, NEL[ExtantFile]] = {
    logger.info(s"Building sequence map from FASTA: ${fasta.value}")
    val seqMap = fbFasta2TranscriptExonSeqMap(fasta)
    logger.info("Sequence map complete")
    val getSeq = regionSeq(seqMap) _
    logger.info("Starting region processing")
    Right( beds map { b => {
      logger.info(s"Using BED: ${b.value}")
      val (errors, result) = regionsFromBed(b).foldLeft(
        Vector.empty[String] -> Vector.empty[(String, Range, String, String)] ){ 
          case ((bads, goods), (chr, range, id)) => 
            getSeq(id, range).fold(
              errMsg => (bads :+ errMsg, goods), 
              _.fold(bads -> goods)(seq => (bads, goods :+ (chr, range, id, seq))))
        }
      logger.info(s"Region processing complete for ${b.value}")
      if (errors.isEmpty) {
        val outfile = new File(b.value.getPath.replaceAllLiterally(".bed", ".seq"))
        logger.info(s"Writing result from ${b.value}: ${outfile}")
        val w = new BufferedWriter(new FileWriter(outfile))
        try { result foreach { case (chr, (start, end), id, seq) => {
          val fields = List(chr, start.toString, end.toString, id, seq)
          w.write(fields.mkString("\t"))
          w.newLine()
        } } }
        finally { w.close() }
        logger.info(s"Write complete: ${outfile}")
        ExtantFile.unsafe(outfile)
      } else { throw new Exception(s"${errors.size} error(s); max 5: ${errors.take(5).mkString("\n")}") }
    } } )
    
  }

}
