package flybrain571

import com.typesafe.scalalogging.LazyLogging

/**
 * Interfacing with the {@code MeTPeak} package/program in {@code R}.
 *
 * @author Vince Reuter
 */
object MeTPeakProgram extends LazyLogging {
  
  import java.io.File
  import java.nio.file.Paths
  import cats._, cats.implicits._
  import cats.data.{ NonEmptyList => NEL }
  import Refinement._, ExtantFile._
  
  //final case class ProgramInstance(execFile: ExtantFile) {
  final case class ProgramInstance() {
    /*
    require(execFile.value.canExecute(), 
      s"Path to program instance must be an executable file: $execFile")
    */
    
    def run(gtf: ExtantFile)(ips: NEL[ExtantFile], controls: NEL[ExtantFile])(
      outdir: File, name: String, clobber: Boolean = false): Stream[String] = {
      import scala.sys.process._
      val cmd = getCmd(gtf)(ips, controls)(outdir, name, clobber)
      logger.info(s"Running command: $cmd")
      cmd.lineStream
    }

    def getCmd(gtf: ExtantFile)(
      ips: NEL[ExtantFile], controls: NEL[ExtantFile])(
      outdir: File, name: String, clobber: Boolean = false): String = {
      val files2Text = (fs: NEL[ExtantFile]) => fs.toList.map(_.show).mkString(" ")
      val ipText = files2Text(ips)
      val ctrlText = files2Text(controls)
      //val progPath = execFile.value.getPath
      // TODO: figure out how to properly leverage this
      //val progPath = new File(this.getClass().getResource("/runMetpeak.R").toString).getPath
      val progPath = {
        val progName = "runMetpeak.R"
        val codeDir = System.getenv("CODE")
        if (codeDir == null) progName
        else {
          val resDir = new File(Paths.get(codeDir, "scratch571", "src", "main", "resources").toString)
          new File(resDir, progName).getPath
        }
      }
      logger.debug(s"Program path: $progPath")
      val cmd1 = s"$progPath --gtf ${gtf.show} --ips $ipText --controls $ctrlText -N $name -O $outdir"
      if (clobber) cmd1 ++ " --overwrite" else cmd1
    }
    
  }

}
