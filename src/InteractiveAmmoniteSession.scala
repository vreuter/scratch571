package flybrain571

object InteractiveAmmoniteSession {
  
  import java.io.File
  import java.nio.file.Paths
  interp.load.ivy("org.typelevel" %% "cats-core" % "2.0.0")
  import cats._, cats.implicits._
  val thisJarFile = new File(s"${System.getenv("CODE")}/scratch571/target/scala-2.12/flybrain571_v0.0.1-SNAPSHOT.jar")
  interp.load.cp(ammonite.ops.Path(thisJarFile))
  import flybrain571._
  interp.load.ivy("org.typelevel" % "mouse_2.12" % "0.23")
  import mouse.boolean._
  import flybrain571.DataPaths.{ findAllByExtension => findByExt }
  
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
  val gtfPath = new File(Paths.get(genomeFolder.getPath, assembly, gtfName).toString)
  if (!gtfPath.isFile) { throw new Exception(s"Missing GTF: $gtfPath") }

  val dataEnvVar = "DATA"
  val dataFolder = unsafe(folderFromEnvVar)(dataEnvVar)
  val allDataFiles = findByExt(Preamble.readsFileExt)(new File(dataFolder, "BoniniLab"))
  val fileByID = allDataFiles.toVector.map(f => {
    val xf = Refinement.ExtantFile.unsafe(new File(f))
    unsafe(Preamble.idFromFile)(xf) -> xf
  })
  val (neFiles, ssFiles) = fileByID partition { case (id, f) => id.antibody match {
    case _: Nebl => true
    case _: SynSys => false
  } }

}
