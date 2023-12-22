Center Star heuristic 
Multiple Sequence Alignment

INPUT:
hardcoded to run fasta input from strings on "fasta5.txt" file
args[0] is mismatch distance score (positive because we care trying to minimize this score)
args[1] is indel distance score

OUTPUT
For every pair of strings:
-distance matrix
-optimal alignment (of two strings only)

for the set of strings:
-chooses central string


geri, add third step, where strings are added one by one, retroactively correcting the spaces in previously added strings to produce final alignment.
