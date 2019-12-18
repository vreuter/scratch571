package flybrain571

/**
 * Testing out the association of FASTA sequence info with called peak BEDs
 *
 * @author Vince Reuter
 */
object InteractiveTestSeqRegions {
  
  /* Imports and setup */
  import scala.io.Source
  import java.io.{ BufferedWriter, File, FileWriter }
  import java.nio.file.Paths
  interp.load.ivy("org.typelevel" %% "cats-core" % "2.0.0")
  import cats._, cats.implicits._
  import cats.data.{ NonEmptyList => NEL }
  import cats.syntax.list._
  val thisJarFile = new File(s"${System.getenv("CODE")}/scratch571/target/scala-2.12/flybrain571_v0.0.3.jar")
  interp.load.cp(ammonite.ops.Path(thisJarFile))
  import flybrain571._
  import Refinement.ExtantFile
  interp.load.ivy("org.typelevel" % "mouse_2.12" % "0.23")
  import mouse.boolean._
  
  // Path to the D. mel exons FASTA from FlyBase
  val exonsFasta = ExtantFile.unsafe(new File(s"${System.getenv("GENOMES")}/dm6/dmel-all-exon-r6.30.fasta"))
  
  // Canonical peak call set filepath
  val regFile = ExtantFile.unsafe(new File(Paths.get(
    s"${System.getenv("HOME")}", "BoniniLab", Antibody.neblAlias, "Ime4_vs_Input_HS.peak.bed").toString))

  // Fasta "records," repped as header and sequence
  val faRecs = FastaTools.parseFbFasta(exonsFasta)

  // Bundle sequence data by the gene ID info in the header from FlyBase
  val recsByGeneID = FastaTools.faRecords2ExonSeqMap(faRecs)

  // Use the fixed mapping of geneID and location info to sequence to determine sequence for called peaks
  val getRegSeq = (geneID: String, coords: (Int, Int)) => FastaTools.regionSeq(recsByGeneID)(geneID, coords)

  /* Translate of raw called peak data to queries against the mini FASTA "database" built here */
  val metpeakCalls2Queries = (bed: ExtantFile) => Source.fromFile(bed.value).getLines.toList.tail map { l => {
    val fields = l.split("\t")
    fields(3) -> (fields(1).dropWhile(c => !c.isDigit).toInt -> fields(2).takeWhile(_.isDigit).toInt)
  } }
  val peakCallQueries = metpeakCalls2Queries(regFile)
  val rawQueryResults = peakCallQueries map { case (geneID, coords) => getRegSeq(geneID, coords) }
  
  // Print out some number regarding the usability of peaks (immediate usability requires a nonempty Right (sequence data))
  rawQueryResults.size
  rawQueryResults.count(_.isLeft)
  rawQueryResults.count(x => x.isRight && x.right.get.isEmpty)
  rawQueryResults.count(x => x.isRight && x.right.get.nonEmpty)

}
