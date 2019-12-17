package flybrain571

import com.typesafe.scalalogging.StrictLogging

object Preprocess extends StrictLogging {
  import java.io.{ BufferedWriter, File, FileWriter }
  import cats.instances.either._, cats.instances.string._
  import cats.syntax.bifunctor._, cats.syntax.eq._, cats.syntax.show._
  import Refinement.ExtantFile, ExtantFile._
  
  private[this] val bamSuffix = ".bam"
  val dedupSuffix = s".dedup$bamSuffix"
  
  def rmdupAndIndexCmd(f: ExtantFile): Either[String, (String, String)] = {
    require(f.value.getName.endsWith(bamSuffix))
    logger.info(s"Using BAM: ${f.show}")
    val picardEnvVar = "PICARD"
    val picardPath = System.getenv(picardEnvVar)
    if (picardPath == null) Left(s"Missing Picard env var ($picardEnvVar)")
    else {
      val picardJar = new File(picardPath)
      val outfile = new File(f.value.getPath.replaceAllLiterally(bamSuffix, dedupSuffix))
      ExtantFile(picardJar).bimap(
        _ => s"Picard path isn't a file: $picardPath", 
        _ => {
          val dedupCmd = s"java -jar $picardPath MarkDuplicates I=${f.value} O=${outfile}"
          val indexCmd = s"samtools index $outfile"
          dedupCmd -> indexCmd
        }
      )
    }
  }

  def writeDedupAndIndexScript(script: File, rmdup: String, index: String): File = {
    val dstDir = script.getParentFile
    if (dstDir != null && dstDir.getPath =!= "" && !dstDir.isDirectory) {
      logger.debug(s"Creating path for script: $script")
      dstDir.mkdirs()
    }
    val w = new BufferedWriter(new FileWriter(script))
    val lines = List("#!/bin/bash", rmdup, index)
    try { lines foreach { l => w.write(l); w.newLine(); w.newLine() } }
    finally { w.close() }
    script
  }

}
