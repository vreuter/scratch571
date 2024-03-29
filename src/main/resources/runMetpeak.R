#!/usr/bin/env Rscript

# File: runMetpeak.R
# Author: Vince Reuter
# Email: vincer@pennmedicine.upenn.edu
# Description: Run a MeTPeak analysis; see:
#     Software -- https://github.com/compgenomics/MeTPeak
#     Paper    -- https://www.ncbi.nlm.nih.gov/pubmed/27307641

library("argparse")
library("R.utils")
library("MeTPeak")

# CLI definition and parsing
parser <- ArgumentParser()
parser$add_argument("-A", "--gtf", required=TRUE, help="Path to transcript annotation file to use")
parser$add_argument("-N", "--name", required=TRUE, help="Name for the 'experiment' being analyzed")
parser$add_argument("-O", "--outputFolder", required=TRUE, help="Path to folder for output")
parser$add_argument("--ips", nargs="+", help="Path(s) to IP sample file(s) -- aligned BAM")
parser$add_argument("--controls", nargs="+", help="Path(s) to control sample file(s) -- aligned BAM")
parser$add_argument("--overwrite", action="store_true", help="Allow overwrite of existing data on disk")
args <- parser$parse_args()

message("Main output folder: ", args$outputFolder)

# Handle output folder creation as necessary
if (!file_test("-d", args$outputFolder)) {
  message("Creating path to output folder: ", args$outputFolder)
  R.utils::mkdirs(args$outputFolder)
} else if (!args$overwrite) { stop("Output folder exists and overwriting was not specified") }

# Note that the experiment name will determine subfolder within main OUTPUT_DIR.
metpeak(GENE_ANNO_GTF = args$gtf, IP_BAM = args$ips, INPUT_BAM = args$controls, EXPERIMENT_NAME = args$name, OUTPUT_DIR = args$outputFolder)

message("Complete")
