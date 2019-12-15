package flybrain571

/**
 * Functionality related to paths on disk
 *
 * @author Vince Reuter
 */
object DataPaths {

  import java.io.File
  import scala.sys.process._

  /**
   * Find all files with a given extension and rooted in a given location.
   *
   * @param ext Extension required for a file to match
   * @param root The folder from which to start the recursive search
   * @return A collection of paths matching the extension, and within the filesystem 
   *         subtree rooted as required
   */
  def findAllByExtension(ext: String)(root: File): Stream[String] = {
    require(root.isDirectory, s"Root folder for find operation is not a directory: $root")
    Seq("find", root.getPath, "-name", s"*${ext}").lineStream
  }

}
