package flybrain571

object InteractiveAmmoniteSession {
  
  import java.io.File
  import java.nio.file.Paths
  interp.load.ivy("org.typelevel" %% "cats-core" % "2.0.0")
  import cats._, cats.implicits._
  val thisJarFile = new File(s"${System.getenv("CODE")}/scratch571/target/scala-2.12/flybrain571_v0.0.1-SNAPSHOT.jar")
  interp.load.cp(ammonite.ops.Path(thisJarFile))
  import flybrain571.{ Preamble, Refinement }
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

  val genomesEnvVar = "GENOMES"
  val genomeFolder = folderFromEnvVar(genomesEnvVar).fold(errMsg => throw new Exception(errMsg), identity _)
  val assembly = "dm6"
  val gtfName = "Drosophila_melanogaster.BDGP6.22.98.chr.gtf"
  val gtfPath = new File(Paths.get(genomeFolder.getPath, assembly, gtfName).toString)
  if (!gtfPath.isFile) { throw new Exception(s"Missing GTF: $gtfPath") }

  val dataEnvVar = "DATA"
  val dataFolder = folderFromEnvVar(dataEnvVar).fold(errMsg => throw new Exception(errMsg), identity _)
  val allDataFiles = findByExt(Preamble.readsFileExt)(new File(dataFolder, "BoniniLab"))
  val fileByID = allDataFiles.map(f => {
    val xf = Refinement.ExtantFile.unsafe(new File(f))
    Preamble.idFromFile(xf) -> xf
  })

}
