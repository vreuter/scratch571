package flybrain571

object Annotate {
  
  import java.io.File
  import scala.io.Source
  import mouse.boolean._
  import FastaTools._, Refinement.{ ExtantFile }
  
  def readAnnotationsFile(f: ExtantFile, geneIdIndex: Int = 0, sep: String = "\t"): Vector[(String, Range)] = {
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

  def sequenceRegions(fasta: ExtantFile)(regionsFile: ExtantFile): Either[String, Vector[(String, Range, String)]] = {
    val seqMap = fbFasta2TranscriptExonSeqMap(fasta)
    val getSeq = regionSeq(seqMap) _
    val (errors, result) = readAnnotationsFile(regionsFile).foldLeft(
      Vector.empty[String] -> Vector.empty[(String, Range, String)] ){ 
        case ((bads, goods), (id, range)) => 
          getSeq(id, range).fold(
            errMsg => (bads :+ errMsg, goods), 
            seq => (bads, goods :+ (id, range, seq)))
      }
    errors.isEmpty.either(s"${errors.size} error(s); max 5: ${errors.take(5).mkString("\n")}", result)
  }

}
