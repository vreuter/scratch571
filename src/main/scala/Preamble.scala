package flybrain571

import com.typesafe.scalalogging.LazyLogging

/**
 * Project-specific data and such
 *
 * @author Vince Reuter
 */
object Preamble extends LazyLogging {
  import java.io.File
  import MeTPeakProgram.{ ProgramInstance => MPP }
  import Refinement._
  val neblAlias = "NEB_Antibody"
  val synSysAlias = "SynapticSystems_Antibody"
  // TODO: *.sort.bam
  // TODO: GTF for dm6 + possibly annotations
  val readsFileExt = ".sort.bam"
  //private[this] val rawProgPath = new File(getClass().getResource("/runMetpeak.R").toString)
  //logger.info(s"Program path: $rawProgPath")
  //private[this] val progPath = ExtantFile.unsafe(rawProgPath)
  //val mpProg = MPP(progPath)
  val mpProg = MPP()
}
