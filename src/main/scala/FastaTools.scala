package flybrain571

/**
 * Functionality for working with {@code fasta} files
 *
 * @author Vince Reuter
 */
object FastaTools {

  import scala.annotation.tailrec
  import scala.io.Source
  import cats.data.{ NonEmptyList => NEL }
  import Refinement.ExtantFile
  
  type Range = (Int, Int)
  type FastaExonMap = Map[String, Vector[FbFastaExon]]

  /**
   * Represent exon data from a FlyBase FASTA.
   *
   * @param geneID Record's gene identifier/key
   * @param range Record coordinate interval
   * @param seq Record nucleotide sequence
   * @return new record representation instance
   */
  final case class FbFastaExon(geneID: String, range: Range, seq: String) {
    def contains = (maybeSubRange: Range) => maybeSubRange._1 >= range._1 && maybeSubRange._2 <= range._2
  }
  
  /** Parse a FlyBase FASTA file to a collection of pairs of record header and sequence. */
  def parseFbFasta: ExtantFile => Vector[(String, String)] = fasta => {
    @tailrec
    def go(lines: Vector[String], acc: Vector[(String, String)]): Vector[(String, String)] = {
      if (lines.isEmpty) acc
      else {
        val recHead = lines.head
        if (!recHead.startsWith(">")) throw new IllegalStateException(s"Started record improperly: $recHead")
        val (seqParts, rest) = lines.tail.span(l => !l.startsWith(">"))
        val newRec = recHead.stripPrefix(">") -> seqParts.mkString("")
        go(rest, acc :+ newRec)
      }
    }
    go(Source.fromFile(fasta.value).getLines.toVector, Vector.empty[(String, String)])
  }

  /**
   * Read FlyBase FASTA into mapping from gene ID to collection of exon records
   *
   * @param fasta The file to parse
   * @return mapping from gene ID to collection of representations of exon records
   */
  def fbFasta2TranscriptExonSeqMap(fasta: ExtantFile): Map[String, Vector[FbFastaExon]] = {
    parseFbFasta(fasta).foldLeft(Map.empty[String, Vector[FbFastaExon]]){
      case (accMap, (head, seq)) => {
        val rawFields = head.split("; ")
        // First field is of form <geneID>:<exon-num> type=exon
        val geneID = rawFields(0).split(" ")(0).split(":")(0)
        val range = rawFields(1).split(":")(1).split("..").toList match {
          case start :: end :: Nil => start.toInt -> end.toInt
          case _ => throw new Exception(s"Could not parse coordinates from record header: $head")
        }
        val exons = accMap.getOrElse(geneID, Vector.empty[FbFastaExon])
        accMap + (geneID -> (exons :+ FbFastaExon(geneID, range, seq)))
      }
    }
  }

  /**
   * Determine the nucleotide sequence of an arbitrary region.
   *
   * @param exonsByGene mapping from gene ID to collection of associated exon record representations
   * @param geneID gene ID of the region for which to get sequence
   * @param range coordinate interval for which to get sequence
   * @return Either a {@code Left} with explanatory error message, or a {@code Right} with the sequence
   */
  def regionSeq(exonsByGene: FastaExonMap)(geneID: String, range: Range): Either[String, String] = 
    exonsByGene.get(geneID).toRight(s"Gene ID not found: $geneID") flatMap {
      _.filter(_.contains(range)).toList match {
        case e :: Nil => Right(e.seq)
        case containers => Left(s"${containers.size} exon(s) containing $range for gene $geneID")
      }
    }

}