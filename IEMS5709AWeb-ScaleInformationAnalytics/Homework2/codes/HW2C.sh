#!/bin/bash
# Compile
../bin/hadoop com.sun.tools.javac.Main APrioriTriple.java
../bin/hadoop com.sun.tools.javac.Main APrioriTripleCheck.java
jar cf APrioriTriple.jar APrioriTriple*.class
jar cf APrioriTripleCheck.jar APrioriTripleCheck*.class

# First MapReduce
start_time1=$(date +%s)
../bin/hadoop jar APrioriTriple.jar APrioriTriple ./dataset/chunks/* TripleCandidates
end_time1=$(date +%s)

# Combine the candidates and write to disk
hadoop fs -getmerge TripleCandidates TripleCandidate

# Move the conbined Candidate back
../bin/hadoop fs -put /usr/local/hadoop/scripts/TripleCandidate hdfs://hadoop-namenode:54310/user/5709master/TripleCandidate

# Second MapReduce
start_time2=$(date +%s)
../bin/hadoop jar APrioriTripleCheck.jar APrioriTripleCheck ./dataset/chunks/* TripleCandidate APrioriTriple_Out
end_time2=$(date +%s)

# Combine the result and write to disk waitting for filter
hadoop fs -getmerge APrioriTriple_Out APrioriTripleOutput

execution_time=$((end_time1 + end_time2 - start_time1 - start_time2))
echo "Execution Time: $execution_time s"