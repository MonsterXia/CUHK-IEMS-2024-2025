#!/bin/bash
# Compile
../bin/hadoop com.sun.tools.javac.Main APriori.java
../bin/hadoop com.sun.tools.javac.Main APrioriCheck.java
jar cf APriori.jar APriori*.class
jar cf APrioriCheck.jar APrioriCheck*.class

# First MapReduce
start_time1=$(date +%s)
../bin/hadoop jar APriori.jar APriori ./dataset/chunks/* Candidates
end_time1=$(date +%s)

# Combine the candidates and write to disk
hadoop fs -getmerge Candidates Candidate

# Move the conbined Candidate back
../bin/hadoop fs -put /usr/local/hadoop/scripts/Candidate hdfs://hadoop-namenode:54310/user/5709master/Candidate

# Second MapReduce
start_time2=$(date +%s)
../bin/hadoop jar APrioriCheck.jar APrioriCheck ./dataset/chunks/* Candidate APriori_Out
end_time2=$(date +%s)

# Combine the result and write to disk waitting for filter
hadoop fs -getmerge APriori_Out APrioriOutput

execution_time=$((end_time1 + end_time2 - start_time1 - start_time2))
echo "Execution Time: $execution_time s"