#!/bin/bash
# Compile
../bin/hadoop com.sun.tools.javac.Main PCYAlgorithm.java
jar cf PCYAlgorithm.jar PCYAlgorithm*.class

# First MapReduce
start_time1=$(date +%s)
../bin/hadoop jar PCYAlgorithm.jar PCYAlgorithm ./dataset/chunks/* PCYAlgorithmCandidates
end_time1=$(date +%s)

# Combine the candidates and write to disk
hadoop fs -getmerge PCYAlgorithmCandidates PCYAlgorithmCandidate

# Move the conbined Candidate back
../bin/hadoop fs -put /usr/local/hadoop/scripts/PCYAlgorithmCandidate hdfs://hadoop-namenode:54310/user/5709master/PCYAlgorithmCandidate

# Second MapReduce
start_time2=$(date +%s)
../bin/hadoop jar APrioriCheck.jar APrioriCheck ./dataset/chunks/* PCYAlgorithmCandidate PCYAlgorithm_Out
end_time2=$(date +%s)

# Combine the result and write to disk waitting for filter
hadoop fs -getmerge PCYAlgorithm_Out PCYAlgorithmOutput

execution_time=$((end_time1 + end_time2 - start_time1 - start_time2))
echo "Execution Time: $execution_time s"