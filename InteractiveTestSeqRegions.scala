package flybrain571

object InteractiveTestSeqRegions {
  
  /* Imports and setup */
  import java.io.{ BufferedWriter, File, FileWriter }
  import java.nio.file.Paths
  interp.load.ivy("org.typelevel" %% "cats-core" % "2.0.0")
  import cats._, cats.implicits._
  import cats.data.{ NonEmptyList => NEL }
  import cats.syntax.list._
  val thisJarFile = new File(s"${System.getenv("CODE")}/scratch571/target/scala-2.12/flybrain571_v0.0.3-SNAPSHOT.jar")
  interp.load.cp(ammonite.ops.Path(thisJarFile))
  import flybrain571._
  import Refinement.ExtantFile
  interp.load.ivy("org.typelevel" % "mouse_2.12" % "0.23")
  import mouse.boolean._
  
  val exonsFasta = ExtantFile.unsafe(new File(s"${System.getenv("GENOMES")}/dm6/dmel-all-exon-r6.30.fasta"))
  val regFile = ExtantFile.unsafe(new File(Paths.get(
    s"${System.getenv("HOME")}", "BoniniLab", Antibody.neblAlias, "Ime4_vs_Input_HS.peak.bed").toString))

  val oneRes = Regions.addSeq2Regions(exonsFasta)(NEL(regFile, Nil))

}
