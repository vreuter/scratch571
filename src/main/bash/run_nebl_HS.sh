#!/bin/bash

# NEBL antibody, Ime4 IP vs. Input, heat shock
/home/vr/code/scratch571/src/main/resources/runMetpeak.R --gtf /home/vr/genomes/dm6/Drosophila_melanogaster.BDGP6.22.98.chr.gtf --ips /home/vr/data/BoniniLab/NEB_Antibody/Rep3_m6AIP_Ime4_HS.sort.rmdup.bam /home/vr/data/BoniniLab/NEB_Antibody/Rep2_m6AIP_Ime4_HS.sort.rmdup.bam /home/vr/data/BoniniLab/NEB_Antibody/Rep1_m6AIP_Ime4_HS.sort.rmdup.bam --controls /home/vr/data/BoniniLab/NEB_Antibody/Rep1_Input_Ime4_HS.sort.rmdup.bam /home/vr/data/BoniniLab/NEB_Antibody/Rep3_Input_Ime4_HS.sort.rmdup.bam /home/vr/data/BoniniLab/NEB_Antibody/Rep2_Input_Ime4_HS.sort.rmdup.bam -N Ime4_vs_Input_HS -O /home/vr/processed/BoniniLab --overwrite

# NEBL antibody, Mcherry IP vs. Input, heat shock
/home/vr/code/scratch571/src/main/resources/runMetpeak.R --gtf /home/vr/genomes/dm6/Drosophila_melanogaster.BDGP6.22.98.chr.gtf --ips /home/vr/data/BoniniLab/NEB_Antibody/Rep2_m6AIP_Mcherry_HS.sort.rmdup.bam /home/vr/data/BoniniLab/NEB_Antibody/Rep1_m6AIP_Mcherry_HS.sort.rmdup.bam /home/vr/data/BoniniLab/NEB_Antibody/Rep3_m6AIP_Mcherry_HS.sort.rmdup.bam --controls /home/vr/data/BoniniLab/NEB_Antibody/Rep3_Input_Mcherry_HS.sort.rmdup.bam /home/vr/data/BoniniLab/NEB_Antibody/Rep1_Input_Mcherry_HS.sort.rmdup.bam /home/vr/data/BoniniLab/NEB_Antibody/Rep2_Input_Mcherry_HS.sort.rmdup.bam -N Mcherry_vs_Input_HS -O /home/vr/processed/BoniniLab --overwrite

