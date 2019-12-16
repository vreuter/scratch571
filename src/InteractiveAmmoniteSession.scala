package flybrain571

object InteractiveAmmoniteSession {
  
  import java.io.File
  import java.nio.file.Paths
  interp.load.ivy("org.typelevel" %% "cats-core" % "2.0.0")
  import cats._, cats.implicits._
  import cats.data.{ NonEmptyList => NEL }
  val thisJarFile = new File(s"${System.getenv("CODE")}/scratch571/target/scala-2.12/flybrain571_v0.0.1-SNAPSHOT.jar")
  interp.load.cp(ammonite.ops.Path(thisJarFile))
  import flybrain571._
  import Refinement.ExtantFile
  interp.load.ivy("org.typelevel" % "mouse_2.12" % "0.23")
  import mouse.boolean._
  import flybrain571.DataPaths.{ findAllByExtension => findByExt }
  import MeTPeakProgram.{ ProgramInstance => MPP }
  
  val boniniLabName = "BoniniLab"

  def pathFromEnvVar(check: File => Boolean): String => Either[String, File] = (v: String) => {
    val name = System.getenv(v)
    (name =!= "").either(s"No $v env var set", ()) flatMap { _ => {
      val path = new File(name)
      check(path).either(s"Missing path for $v: $path", path)
    } }
  }

  def folderFromEnvVar = pathFromEnvVar((_: File).isDirectory)
  def fileFromEnvVar = pathFromEnvVar((_: File).isFile)

  def unsafe[A, B](f: A => Either[String, B]): A => B = a => f(a).fold(msg => throw new Exception(msg), identity _)

  val genomesEnvVar = "GENOMES"
  val genomeFolder = unsafe(folderFromEnvVar)(genomesEnvVar)
  val assembly = "dm6"
  val gtfName = "Drosophila_melanogaster.BDGP6.22.98.chr.gtf"
  val gtfPath = ExtantFile.unsafe(new File(Paths.get(genomeFolder.getPath, assembly, gtfName).toString))

  val dataEnvVar = "DATA"
  val dataFolder = unsafe(folderFromEnvVar)(dataEnvVar)
  
  val allDataFiles = findByExt(Preamble.readsFileExt)(new File(dataFolder, boniniLabName))
  val fileByID = allDataFiles.toVector.map(f => {
    val xf = ExtantFile.unsafe(new File(f))
    unsafe(Preamble.idFromFile)(xf) -> xf
  })
  
  val (neFiles, ssFiles) = fileByID partition { case (id, f) => id.antibody match {
    case _: Nebl => true
    case _: SynSys => false
  } }
  val neRepGroups = SampleID.groupReplicateData(neFiles)
  val ssRepGroups = SampleID.groupReplicateData(ssFiles)

  val procEnvVar = "PROCESSED"
  val resFolder = new File(unsafe(folderFromEnvVar)(procEnvVar), "BoniniLab")

  def metpeakCommand(name: String, ips: NEL[ExtantFile], ctrls: NEL[ExtantFile]): String = {
    val prog = MPP()
    prog.getCmd(gtfPath)(ips, ctrls)(resFolder, name, clobber = true)
  }

  val neblIme4 = neRepGroups.filter(_._1._2 === Ime4)

  def getExactlyOne[A](p: A => Boolean)(xs: Iterable[A]): A = xs.toList.filter(p) match {
    case a :: Nil => a
    case matches => throw new Exception(s"${matches.size} matches, not exactly 1: $matches")
  }

  type FileGroup = (SampleID.NonRepID, Vector[ExtantFile])

  import cats.syntax.list._
  def vect2Nel[A]: Vector[A] => NEL[A] = _.toList.toNel.get

  val ime4NeblIpVsInputUnshockedCmd = {
    val find: Boolean => (Iterable[FileGroup] => FileGroup) = p => {
      getExactlyOne( (idWithFiles: FileGroup) => 
        idWithFiles._1 match { case (ab, _, exp) => ab === Nebl(p) && !exp } )
    }
    val (ipFiles, ctrlFiles) = vect2Nel(find(true)(neblIme4)._2) -> vect2Nel(find(false)(neblIme4)._2)
    metpeakCommand("Ime4_vs_Input_Control", ipFiles, ctrlFiles)
  }

}
