# scratch571

## Requirements (for using the `Interactive...` analysis)
1. [Ammonite](https://ammonite.io/) installation
2. Recommendation is to download a JAR, but you may also clone the repo and build the project yourself. For a build, you'll need `sbt` at minimum. 
Then run the `assembly` task in `sbt` to build a local JAR, where the path to it will be provided in the `sbt` output.
3. `DATA` environment variable, with subfolder `BoniniLab` containing the subfolders with data of interest (`SynapticSystem_Antibody` and `NEB_Antibody`)
4. `PROCESSED` environment variable for routing output, with subfolder `BoniniLab`
5. `GENOMES` environment variable, with subfolder `dm6` containing the *D. melanogaster* GTF from Ensembl or FlyBase, for dm6.

## `MeTPeak` postprocessing
1. Load `Rdata` file (`metpeak.Rdata`) and write annotations (`get("tmp_rs")$ANNOTATION`)
```
> head(tmp_rs$ANNOTATION, 5)
   chr feature start  stop strand        gene  transcript
1   2L    exon  7529  8116      + FBgn0031208 FBtr0300689
2   2L    exon  8193  9484      + FBgn0031208 FBtr0300689
3   2L    exon  7529  8116      + FBgn0031208 FBtr0300690
4   2L    exon  8193  8589      + FBgn0031208 FBtr0300690
5   2L    exon  8668  9484      + FBgn0031208 FBtr0300690
```
2. Map transcript ID (`transcript`) from annotation table to sequence (FASTA)
3. Determine base-level coverage from pooled evidence set used to generate peak calls, for peak call set.
4. Model prior probability of m<sup>6</sup>A at each `A` in the sequence for each peak (discard peak if no `A` is present)
5. Update prior probability according to base-level data/evidence from reads.
6. Emit estimates of base-level methylation probability (per `A` per peak)

