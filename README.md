# scratch571

## Requirements
1. Recommendation is to download a JAR, but you may also clone the repo and build the project yourself. For a build, you'll need `sbt` at minimum. 
Then run the `assembly` task in `sbt` to build a local JAR, where the path to it will be provided in the `sbt` output.
2. `DATA` environment variable, with subfolder `BoniniLab` containing the subfolders with data of interest (`SynapticSystem_Antibody` and `NEB_Antibody`)
3. `PROCESSED` environment variable for routing output, with subfolder `BoniniLab`
4. `GENOMES` environment variable, with subfolder `dm6` containing the *D. melanogaster* GTF from Ensembl or FlyBase, for dm6.

