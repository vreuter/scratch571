package flybrain571

/**
 * Interactive analysis working sandbox
 *
 * @author Vince Reuter
 */
object InteractiveAmmoniteSession {
  
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
  import flybrain571.DataPaths.{ findAllByExtension => findByExt }
  import MeTPeakProgram.{ ProgramInstance => MPP }
  
  // Part of filepaths
  val boniniLabName = "BoniniLab"

  // Get a filepath from an environment variable
  def pathFromEnvVar(check: File => Boolean): String => Either[String, File] = (v: String) => {
    val name = System.getenv(v)
    (name =!= "").either(s"No $v env var set", ()) flatMap { _ => {
      val path = new File(name)
      check(path).either(s"Missing path for $v: $path", path)
    } }
  }

  /* Specific use cases of the more general path-from-env-var function */
  def folderFromEnvVar = pathFromEnvVar((_: File).isDirectory)
  def fileFromEnvVar = pathFromEnvVar((_: File).isFile)

  // "Lift" a safe function to an unsafe version, unboxing an Either.
  def unsafe[A, B](f: A => Either[String, B]): A => B = a => f(a).fold(msg => throw new Exception(msg), identity _)

  /* Path to transcriptome GTF */
  val genomesEnvVar = "GENOMES"
  val genomeFolder = unsafe(folderFromEnvVar)(genomesEnvVar)
  val assembly = "dm6"
  val gtfName = "Drosophila_melanogaster.BDGP6.22.98.chr.gtf"
  val gtfPath = ExtantFile.unsafe(new File(Paths.get(genomeFolder.getPath, assembly, gtfName).toString))

  // Data location on disk
  val dataEnvVar = "DATA"
  val dataFolder = unsafe(folderFromEnvVar)(dataEnvVar)
  
  // Groups of data files
  val allDataFiles = findByExt(Preamble.readsFileExt)(new File(dataFolder, boniniLabName))
  val fileByID = allDataFiles.toVector.map(f => {
    val xf = ExtantFile.unsafe(new File(f))
    unsafe(Preamble.idFromFile)(xf) -> xf
  })
  
  // Antibody-specific data file groupings
  val (neFiles, ssFiles) = fileByID partition { case (id, f) => id.antibody match {
    case _: Nebl => true
    case _: SynSys => false
  } }
  val neRepGroups = SampleID.groupReplicateData(neFiles)
  val ssRepGroups = SampleID.groupReplicateData(ssFiles)

  // Results routing on disk
  val procEnvVar = "PROCESSED"
  val resFolder = new File(unsafe(folderFromEnvVar)(procEnvVar), "BoniniLab")

  // Create command with which to run MeTPeak.
  def metpeakCommand(name: String, ips: NEL[ExtantFile], ctrls: NEL[ExtantFile], subfolderName: String): String = {
    val prog = MPP()
    val outdir = new File(resFolder, subfolderName)
    prog.getCmd(gtfPath)(ips, ctrls)(outdir, name, clobber = true)
  }

  // Select treatment and antibody group.
  val neblIme4 = neRepGroups.filter(_._1._2 === Ime4)
  val neblMcherry = neRepGroups.filter(_._1._2 === Mcherry)
  val ssIme4 = ssRepGroups.filter(_._1._2 === Ime4)
  val ssMcherry = ssRepGroups.filter(_._1._2 === Mcherry)

  // Filter a collection, ensuring existence and uniqueness of match.
  def getExactlyOne[A](p: A => Boolean)(xs: Iterable[A]): A = xs.toList.filter(p) match {
    case a :: Nil => a
    case matches => throw new Exception(s"${matches.size} matches, not exactly 1: $matches")
  }

  // Group of filepaths associated with the same identity (antibody, IP status, condition, and treatment)
  type FileGroup = (SampleID.NonRepID, Vector[ExtantFile])

  // Vector to nonempty list
  def vect2Nel[A]: Vector[A] => NEL[A] = _.toList.toNel.get

  // Find NEB antibody sample filepath on disk.
  def findNebl(useIP: Boolean, useShock: Boolean): Iterable[FileGroup] => FileGroup = { 
    getExactlyOne( (idWithFiles: FileGroup) => {
      idWithFiles._1 match { case (ab, _, exp) => 
        ab === Nebl(useIP) && useShock.fold(exp, !exp) }
    } )
  }

  // Find SynapticSystems antibody sample filepath on disk.
  def findSynSys(abIpKind: Option[Boolean], useShock: Boolean): Iterable[FileGroup] => FileGroup = { 
    getExactlyOne( (idWithFiles: FileGroup) => {
      idWithFiles._1 match { case (ab, _, exp) => 
        ab === SynSys(abIpKind) && useShock.fold(exp, !exp) }
    } )
  }

  /* The basic comparison commands, IP vs. non-IP for the same antibody, condition, and treatment combo */
  val ime4NeblIpVsInputUnshockedCmd = {
    val ipFiles = vect2Nel(findNebl(true, false)(neblIme4)._2)
    val ctrlFiles = vect2Nel(findNebl(false, false)(neblIme4)._2)
    metpeakCommand("Ime4_vs_Input_Control", ipFiles, ctrlFiles, Antibody.neblAlias)
  }
  val mcherryNeblIPVsInputUnshockedCmd = {
    val ipFiles = vect2Nel(findNebl(true, false)(neblMcherry)._2)
    val ctrlFiles = vect2Nel(findNebl(false, false)(neblMcherry)._2)
    metpeakCommand("Mcherry_vs_Input_Control", ipFiles, ctrlFiles, Antibody.neblAlias)
  }
  val ime4NeblIpVsInputShockedCmd = {
    val ipFiles = vect2Nel(findNebl(true, true)(neblIme4)._2)
    val ctrlFiles = vect2Nel(findNebl(false, true)(neblIme4)._2)
    metpeakCommand("Ime4_vs_Input_HS", ipFiles, ctrlFiles, Antibody.neblAlias)
  }
  val mcherryNeblIPVsInputShockedCmd = {
    val ipFiles = vect2Nel(findNebl(true, true)(neblMcherry)._2)
    val ctrlFiles = vect2Nel(findNebl(false, true)(neblMcherry)._2)
    metpeakCommand("Mcherry_vs_Input_HS", ipFiles, ctrlFiles, Antibody.neblAlias)
  }
  val ime4SynSysIpVsInputUnshockedCmd = {
    val ipFiles = vect2Nel(findSynSys(Some(true), false)(ssIme4)._2)
    val ctrlFiles = vect2Nel(findSynSys(Some(false), false)(ssIme4)._2)
    metpeakCommand("Ime4_vs_Input_Control", ipFiles, ctrlFiles, Antibody.synSysAlias)
  }
  val ime4SynSysIpVsNegativeUnshockedCmd = {
    val ipFiles = vect2Nel(findSynSys(Some(true), false)(ssIme4)._2)
    val ctrlFiles = vect2Nel(findSynSys(Option.empty[Boolean], false)(ssIme4)._2)
    metpeakCommand("Ime4_vs_Negative_Control", ipFiles, ctrlFiles, Antibody.synSysAlias)
  }
  val mcherrySynSysIpVsInputUnshockedCmd = {
    val ipFiles = vect2Nel(findSynSys(Some(true), false)(ssMcherry)._2)
    val ctrlFiles = vect2Nel(findSynSys(Some(false), false)(ssMcherry)._2)
    metpeakCommand("Mcherry_vs_Input_Control", ipFiles, ctrlFiles, Antibody.synSysAlias)
  }
  val mcherrySynSysIpVsNegativeUnshockedCmd = {
    val ipFiles = vect2Nel(findSynSys(Some(true), false)(ssMcherry)._2)
    val ctrlFiles = vect2Nel(findSynSys(Option.empty[Boolean], false)(ssMcherry)._2)
    metpeakCommand("Mcherry_vs_Negative_Control", ipFiles, ctrlFiles, Antibody.synSysAlias)
  }
  val ime4SynSysIpVsInputShockedCmd = {
    val ipFiles = vect2Nel(findSynSys(Some(true), true)(ssIme4)._2)
    val ctrlFiles = vect2Nel(findSynSys(Some(false), true)(ssIme4)._2)
    metpeakCommand("Ime4_vs_Input_HS", ipFiles, ctrlFiles, Antibody.synSysAlias)
  }
  val ime4SynSysIpVsNegativeShockedCmd = {
    val ipFiles = vect2Nel(findSynSys(Some(true), true)(ssIme4)._2)
    val ctrlFiles = vect2Nel(findSynSys(Option.empty[Boolean], true)(ssIme4)._2)
    metpeakCommand("Ime4_vs_Negative_HS", ipFiles, ctrlFiles, Antibody.synSysAlias)
  }
  val mcherrySynSysIpVsInputShockedCmd = {
    val ipFiles = vect2Nel(findSynSys(Some(true), true)(ssMcherry)._2)
    val ctrlFiles = vect2Nel(findSynSys(Some(false), true)(ssMcherry)._2)
    metpeakCommand("Mcherry_vs_Input_HS", ipFiles, ctrlFiles, Antibody.synSysAlias)
  }
  val mcherrySynSysIpVsNegativeShockedCmd = {
    val ipFiles = vect2Nel(findSynSys(Some(true), true)(ssMcherry)._2)
    val ctrlFiles = vect2Nel(findSynSys(Option.empty[Boolean], true)(ssMcherry)._2)
    metpeakCommand("Mcherry_vs_Negative_HS", ipFiles, ctrlFiles, Antibody.synSysAlias)
  }

  // Write script with commands to run MeTPeak for the Synaptic Systems antibody samples'
  // basic comparisons (IP+ vs. input and IP+ vs. IP-, within-condition (HS+/-) and within-treatment(Ime4 KD+/-)).
  val runMetpeakSynSysSamplesScript = {
    val script = new File(resFolder, s"runMP_SynSysSamples.sh")
    val w = new BufferedWriter(new FileWriter(script))
    val cmds = List(
      ime4SynSysIpVsInputUnshockedCmd, ime4SynSysIpVsNegativeUnshockedCmd, 
      mcherrySynSysIpVsInputUnshockedCmd, mcherrySynSysIpVsNegativeUnshockedCmd, 
      ime4SynSysIpVsInputShockedCmd, ime4SynSysIpVsNegativeShockedCmd, 
      mcherrySynSysIpVsInputShockedCmd, mcherrySynSysIpVsNegativeShockedCmd
    )
    val lines = "#!/bin/bash" :: cmds
    try { lines foreach { l => { w.write(l); w.newLine(); w.newLine() } } }
    finally { w.close() }
    script
  }

}
