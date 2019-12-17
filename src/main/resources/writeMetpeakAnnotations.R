#!/usr/bin/env Rscript

# File: runMetpeak.R
# Author: Vince Reuter
# Email: vincer@pennmedicine.upenn.edu
# Description: Run a MeTPeak analysis; see:

library("argparse")
library("R.utils")

kAnnoKey <- "ANNOTATION"
kDataFilename <- "metpeak.Rdata"

# The name of the result object that can be loaded, that apparently metpeak creates.
kTableName <- "tmp_rs"

parser <- ArgumentParser()
parser$add_argument("-D", "--dataFolder", help="Path to folder with Rdata file to load")
parser$add_argument("-F", "--dataFile", help="Path to Rdata file to load")
parser$add_argument("-O", "--outfile", required=TRUE, help="Filepath to which to write annotations table")
args <- parser$parse_args()

nullLikeText <- function(s) { is.null(s) || identical(s, "") }

if ( ! (nullLikeText(args$dataFile) || nullLikeText(args$dataFolder)) ) { stop("Provide just 1 of data folder or file") }
dataPath <- {
  if (!nullLikeText(args$dataFolder)) { file.path(args$dataFolder, kDataFilename) }
  else if (!nullLikeText(args$dataFile)) { args$dataFile }
  else { stop("Provide either path to data file or folder") }
}

if (!file_test("-f", dataPath)) { stop("Missing data file: ", dataPath) }

message("Loading data file: ", dataPath)

load(dataPath)
annotations <- get(kTableName)[[kAnnoKey]]

message(sprintf("Annotation table dimensions: %d x %d", nrow(annotations), ncol(annotations)))

outputFolder <- dirname(args$outfile)
if (!nullLikeText(outputFolder) && !file_test("-d", outputFolder)) {
  message("Creating folder for output file")
  R.utils::mkdirs(outputFolder)
}

message("Writing annotations table: ", args$outfile)
write.table(annotations, args$outfile, quote=FALSE, sep="\t")

message("Complete: annotations written.")
