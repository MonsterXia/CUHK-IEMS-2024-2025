# Homework 2

[TOC]

## Statement
I declare that the assignment submitted on Elearning system is original except for source material explicitly acknowledged, and that the same or related material has not been previously submitted for another course. I also acknowledge that I am aware of University policy and regulations on honesty in academic work, and of the disciplinary guidelines and procedures applicable to breaches of such policy and regulations, as contained in the website [https://www.cuhk.edu.hk/policy/academichonesty/](https://www.cuhk.edu.hk/policy/academichonesty/).

<div align=center style="font-size: 18px;">
    Name<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>Your Name</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>SID<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>1155xxxxxx</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><br/>Date<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>3/11/2024</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>Signature <u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>
</div>


<div style="page-break-after: always;"></div>
## Question 0 [20 marks]: Frequent Itemsets

Considering running the PCY algorithm to count frequent item pairs on a dataset with 600 million baskets. Suppose each basket contains n items and there are d distinct item pairs amongst all of the baskets. Consider the following setup during the first pass of PCY: after keeping the counters for every singleton itemset observed during the first pass, we can still afford to store in main memory 400 million integers, each of which will be used as a bucket. Assume further that d is much larger than the total number of buckets available, i.e., d >> 400 million.

### a. [10 marks] 

What is the minimum support threshold s (in absolute number) we can allow if the average count for a bucket should be no more than 40% of the threshold s? Please detail the steps to derive it.

- If s is an large integer
    - Total pairs: $$d = Average Count * 2nd Bucket Numbers$$, thus $$Average Count = \frac{d}{400M}$$
    - Average count no more than 40% of the threshold s: $$Average Count \leq 0.4s$$
    - Thus $$\frac{d}{400M} \leq 0.4s <=> s \geq \frac{d}{1.6*10^8}$$

- If $s$ is a percentage
    - $600M * s \geq \frac{d}{160M} <=> s \geq \frac{d}{9.6*10^{16}}$


###  b. [10 marks] 

Suppose that A, B, C, D, E, and F are all the items under consideration. For a particular support threshold, the maximal frequent itemsets are {A, B, C} and {C, E, F}. What are all the other frequent itemsets?

- {A}, {B}, {C}, {E}, {F}
- {A, B}, {A, C}, {B, C}, {C, E}, {C, F}, {E, F}

## Q1 [80 marks + 20 Bonus marks]: Finding Frequent Itemsets

### a. [20 marks]

Implement the A-Priori algorithm to find frequent pairs on a single machine.

Finding Frequent Itemsets:

```java
package com.monsterxia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class FrequentPairs {
    public static void main(String[] args) {
        String fileName = "yelp_review_50000_100000";
        String outputFileName = "output";
        BigDecimal threshold = new BigDecimal("0.01");

        long startTimeMillis = System.currentTimeMillis();
        try {
            // Find frequent words
            BigInteger count = BigInteger.ZERO;

            // Create a FileReader and BufferedReader
            System.out.println("Path find at "+ FrequentPairs.class.getClassLoader().getResource(fileName));
            FileReader fileReader = new FileReader(FrequentPairs.class.getClassLoader().getResource(fileName).getFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            Map<String, Integer> map = new HashMap<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] temp = line.split(" ");
                count = count.add(BigInteger.ONE);
                for (String s : temp){
                    if (map.containsKey(s)){
                        map.put(s, map.get(s)+1);
                    }else{
                        map.put(s,1);
                    }
                }
            }
            // close the FileReader
            bufferedReader.close();
            BigDecimal countBD = new BigDecimal(count);

            // Store frequent words in set
            Set<String> frequentWords = new HashSet<>();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();

                BigDecimal valueBD = new BigDecimal(value);
                BigDecimal ratio = valueBD.divide(countBD, 8, BigDecimal.ROUND_HALF_UP);
                if (ratio.compareTo(threshold) > 0){
                    frequentWords.add(key);
                    // fileWriter.write(key + " " + value + "\n");
                    // System.out.println(key + " " + value);
                }
            }
            map.clear();

            System.out.println("Number of frequent words: " + frequentWords.size());

            // Find frequent words pairs
            List<String> stringList = new ArrayList<>(frequentWords);
            Collections.sort(stringList);
            frequentWords = null;
            System.gc();
            int n = stringList.size();
            for (int i = 0; i < n-1; i++) {
                for (int j = i + 1; j < n; j++) {
                    String word1 = stringList.get(i);
                    String word2 = stringList.get(j);
                    String pair = "{"+ word1 + "," + word2 + "}";
                    map.put(pair,0);
                }
            }

            System.out.println("Finish frequent pair map set");
            fileReader = new FileReader(FrequentPairs.class.getClassLoader().getResource(fileName).getFile());
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                String[] temp = line.split(" ");
                Arrays.sort(temp);
                for (int i = 0; i < temp.length-1; i++){
                    for (int j = i+1; j < temp.length; j++){
                        String pair = "{"+ temp[i] + "," + temp[j] + "}";
                        if (map.containsKey(pair)){
                            map.put(pair, map.get(pair)+1);
                        }
                    }
                }
            }
            // close the FileReader
            bufferedReader.close();
            System.out.println("Frequent pairs map finished");

            // Write frequent words to a file
            // Relocated to /src/main/resources
            String resourcesPath = FrequentPairs.class.getClassLoader().getResource(fileName).getPath();
            String outputPath = resourcesPath.substring(0, resourcesPath.indexOf("/target/classes")) + "/src/main/resources/";
            outputFileName = outputPath + outputFileName;
            FileWriter fileWriter = new FileWriter(outputFileName);
            int pairCount = 0;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();

                BigDecimal valueBD = new BigDecimal(value);

                BigDecimal ratio = valueBD.divide(countBD, 8, BigDecimal.ROUND_HALF_UP);
                if (ratio.compareTo(threshold) > 0){
                    //if (valueBD.compareTo(new BigDecimal("1")) > 0){
                    pairCount++;
                    fileWriter.write(key + "\t" + value + "\n");
                }
            }
            System.out.println("Number of frequent pairs: " + pairCount);
            fileWriter.close();

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        long endTimeMillis = System.currentTimeMillis();
        long elapsedTimeMillis = endTimeMillis - startTimeMillis;
        System.out.println("Total time(s): " + elapsedTimeMillis/1000);
    }
}

```

It turns out to have **3069** pairs of frequent pairs.

Total cost **131** s.

For **Top 30**, use filter:

```java
package com.monsterxia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PairsFilter {
    public static void main(String[] args) {
        final int N = 30;
        String fileName = "output";
        String outputFileName = "filteredOutput";

        Map<String, Integer> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PairsFilter.class.getClassLoader().getResource(fileName).getFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 2) {
                    String stringA = parts[0];
                    int valueA = Integer.parseInt(parts[1]);
                    map.put(stringA, valueA);
                }
            }

            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(map.entrySet());
            sortedEntries.sort(Map.Entry.<String, Integer>comparingByValue().reversed());


            // Relocated to /src/main/resources
            String resourcesPath = PairsFilter.class.getClassLoader().getResource(fileName).getPath();
            String outputPath = resourcesPath.substring(0, resourcesPath.indexOf("/target/classes")) + "/src/main/resources/";
            outputFileName = outputPath + outputFileName;
            System.out.println(outputFileName);
            FileWriter fileWriter = new FileWriter(outputFileName);
            int count = 1;
            for (Map.Entry<String, Integer> entry : sortedEntries) {

                fileWriter.write(entry.getKey() + "\t" + entry.getValue()+"\n");
                //System.out.println(entry.getKey() + " : " + entry.getValue());
                count++;
                if (count > N) {
                    break;
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

```

It turns out to be:

```sql
{and,to}	2110730
{I,and}	1979207
{I,to}	1711103
{and,of}	1514801
{of,to}	1219191
{and,in}	1183272
{and,it}	1156305
{and,for}	1151625
{I,of}	1135956
{and,that}	1066298
{in,to}	1003317
{it,to}	986428
{for,to}	986213
{I,it}	958945
{that,to}	955417
{I,that}	927713
{I,in}	927377
{I,for}	919987
{and,with}	895307
{and,my}	881387
{I,my}	856248
{and,on}	803232
{and,but}	796030
{my,to}	777188
{The,and}	770294
{in,of}	737286
{and,have}	717162
{and,they}	716634
{I,but}	709957
{and,had}	696880
```

### b. [30 marks] 

Implement the SON algorithm on MapReduce to find frequent pairs.

Before the MapReduce, several operation are needed:

- For big datasets, usually is to big for a single machine to deal with, thus spit it into k pieces, and better a single piece can be a single Mapper task. So I set Max size for a single piece to be around 1MB(For 4MB, Java heap space for couple; For 2MB, runt overtime for Triple).
- In MapReduce, for Mapper, usually it only read one line at a time, if we need to know how many buckets it has, all the words should be written in one line.
- To get threshold for every chunk, we need to know the total lines $$N$$ the dataset has.

Thus, we need to spit the dataset into k pieces whose size is less than 1MB and which only has one line.

```java
package com.monsterxia;

import java.io.*;

public class FileSplit {
    public static void main(String[] args) {
        final long SPLIT_SIZE = 1 * 1024 * 1024;
        String fileName = "yelp_review_50000_100000";
        String outputFileName = "chunk";

        try (BufferedReader reader = new BufferedReader(new FileReader(FileSplit.class.getClassLoader().getResource(fileName).getFile()))) {
            StringBuilder currentContent = new StringBuilder();
            int totalLines = 0;
            long currentSize = 0;
            int partNum = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                totalLines++;
                currentContent.append(line).append("$");
                currentSize += line.length() + "$".length();

                if (currentSize >= SPLIT_SIZE) {
                    currentContent.deleteCharAt(currentContent.length()-1);
                    String outputName = outputFileName + "_part" + partNum;
                    String resourcesPath = PairsFilter.class.getClassLoader().getResource(fileName).getPath();
                    String outputPath = resourcesPath.substring(0, resourcesPath.indexOf("/target/classes")) + "/src/main/resources/chunks/";
                    outputName = outputPath + outputName;
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputName))) {
                        writer.write(currentContent.toString());
                    }
                    currentContent.setLength(0);
                    currentSize = 0;
                    partNum++;
                }
            }

            // Write remaining content to a file
            if (currentContent.length() > 0) {
                String outputName = outputFileName + "_part" + partNum;
                String resourcesPath = PairsFilter.class.getClassLoader().getResource(fileName).getPath();
                String outputPath = resourcesPath.substring(0, resourcesPath.indexOf("/target/classes")) + "/src/main/resources/chunks/";
                outputName = outputPath + outputName;
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputName))) {
                    writer.write(currentContent.toString());
                }
            }
            System.out.println("Total lines: " + totalLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

```

Thus we get chunks and total lines of datasets:

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202411081932557.png"/>

Total lines $$N = 4628551$$

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202411070447606.png"/>

In this case, for every chunk: 

- I ues '\$' to split the bucket, which means $$n_i = NumberOf('\$') +1$$
- For pairs, if one should output, that hold: $$\frac{Numberof(pair)}{n_i} \geq s$$

The First MapReduce job should use A-priori algorithm to find the candidate pairs

```java
/*APriori.java*/
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class APriori {
    public static final BigDecimal THRESHOLD = new BigDecimal("0.01");
    public static class APrioriMapper extends Mapper<Object, Text, Text, IntWritable> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\\$");
            int line = split.length;
            BigDecimal countDB = BigDecimal.valueOf(line);

            Map<String, Integer> map = new HashMap<>();
            for (String s : split){
                String[] singleline = s.split(" ");
                for (String string : singleline) {
                    map.put(string, map.getOrDefault(string, 0) + 1);
                }
            }

            Set<String> frequentWords = new HashSet<>();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String str = entry.getKey();
                int singleCount = entry.getValue();

                BigDecimal valueBD = new BigDecimal(singleCount);
                BigDecimal ratio = valueBD.divide(countDB, 8, BigDecimal.ROUND_HALF_UP);
                if (ratio.compareTo(THRESHOLD) > 0){
                    frequentWords.add(str);
                }
            }
            map.clear();
            List<String> stringList = new ArrayList<>(frequentWords);
            Collections.sort(stringList);
            frequentWords = null;
            System.gc();

            for (int i = 0; i < stringList.size()-1; i++) {
                for (int j = i + 1; j < stringList.size(); j++) {
                    String word1 = stringList.get(i);
                    String word2 = stringList.get(j);
                    String pair = "{"+ word1 + "," + word2 + "}";
                    map.put(pair,0);
                }
            }

            for (String s : split) {
                String[] singleline = s.split(" ");
                for (int i = 0; i < singleline.length-1; i++){
                    for (int j = i+1; j <singleline.length; j++) {
                        String[] temp = new String[]{singleline[i], singleline[j]};
                        Arrays.sort(temp);
                        String pair = "{" + temp[0] + "," + temp[1] + "}";
                        if (map.containsKey(pair)) {
                            map.put(pair, map.get(pair) + 1);
                        }
                    }
                }
            }

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String str = entry.getKey();
                int singleCount = entry.getValue();

                BigDecimal valueBD = new BigDecimal(singleCount);
                BigDecimal ratio = valueBD.divide(countDB, 8, BigDecimal.ROUND_HALF_UP);
                if (ratio.compareTo(THRESHOLD) > 0){
                    Text word = new Text(str);
                    context.write(word, new IntWritable(1));
                }
            }
        }
    }

    public static class APrioriReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            context.write(key, new IntWritable(1));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: APriori <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(APriori.class);

        job.setMapperClass(APrioriMapper.class);
        job.setReducerClass(APrioriReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        job.setNumReduceTasks(4);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```

The second MapReduce job counts only the candidate frequent pairs.

```java
/*APrioriCheck.java*/
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class APrioriCheck {
    public static class APrioriCheckMapper extends Mapper<Object, Text, Text, IntWritable> {
        public static Set<String> set = new HashSet<>();
        @Override
        protected void setup(Context context) throws IOException{
            // Get Cache from DistributedCache
            Path[] files = DistributedCache.getLocalCacheFiles(context.getConfiguration());
            for (Path file : files) {
                // Process the file as needed
                BufferedReader reader = new BufferedReader(new FileReader(file.toString()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] temp = line.split("\t");
                    set.add(temp[0]);
                }
                reader.close();
            }
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\\$");

            for (String s : split) {
                String[] singleline = s.split(" ");
                for (int i = 0; i < singleline.length -1; i++){
                    for (int j = i+1; j < singleline.length; j++) {
                        String[] temp = new String[]{singleline[i], singleline[j]};
                        Arrays.sort(temp);
                        String pair = "{" + temp[0] + "," + temp[1] + "}";
                        if (set.contains(pair)) {
                            Text word = new Text(pair);
                            context.write(word, new IntWritable(1));
                        }
                    }
                }
            }
        }
    }

    public static class APrioriCheckReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public static final BigDecimal THRESHOLD = new BigDecimal("0.01");
        public static final int N = 4628551;
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int count = 0;
            for (IntWritable value : values){
                count++;
            }
            ;
            BigDecimal valueBD = BigDecimal.valueOf(count);
            BigDecimal ratio = valueBD.divide(new BigDecimal(N), 8, BigDecimal.ROUND_HALF_UP);

            if (ratio.compareTo(THRESHOLD) > 0){
                context.write(key, new IntWritable(count));
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 3) {
            System.err.println("Usage: APrioriCheck <dataset> <Candidate> <out>");
            System.exit(2);
        }

        Job job = Job.getInstance(conf);
        job.setJarByClass(APrioriCheck.class);

        job.setMapperClass(APrioriCheckMapper.class);
        job.setReducerClass(APrioriCheckReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        DistributedCache.addCacheFile(new Path(otherArgs[1]).toUri(), job.getConfiguration());
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));

        job.setNumReduceTasks(4);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```

Enter the dataset dir

```bash
cd /usr/local/hadoop/dataset
```

Upload all the datasets

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202411080242524.png"/>

Enter scripts

```bash
cd ../scripts
```

Run the server by

```bash
../sbin/start-dfs.sh
../sbin/start-yarn.sh
```

Create location for dataset in HDFS

```bash
../bin/hdfs dfs -mkdir /user
../bin/hdfs dfs -mkdir /user/5709master
../bin/hdfs dfs -mkdir /user/5709master/dataset
../bin/hadoop fs -put /usr/local/hadoop/dataset/* hdfs://hadoop-namenode:54310/user/5709master/dataset
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202411080246560.png"/>

Upload the scripts to the server

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202411080250314.png"/>

Create shell scripts called HW2B.sh


```shell
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
```

Run the program

```bash
chmod +x HW2B.sh
./HW2B.sh
```

This output has the same pairs as a, only orders are different. Put it in filter find that get the same result.

```sql
{and,to}	2110730
{I,and}	1979207
{I,to}	1711103
{and,of}	1514801
{of,to}	1219191
{and,in}	1183272
{and,it}	1156305
{and,for}	1151625
{I,of}	1135956
{and,that}	1066298
{in,to}	1003317
{it,to}	986428
{for,to}	986213
{I,it}	958945
{that,to}	955417
{I,that}	927713
{I,in}	927377
{I,for}	919987
{and,with}	895307
{and,my}	881387
{I,my}	856248
{and,on}	803232
{and,but}	796030
{my,to}	777188
{The,and}	770294
{in,of}	737286
{and,have}	717162
{and,they}	716634
{I,but}	709957
{and,had}	696880
```

**Overall Execution Time**

| Question | Overall Execution Time(s) |
| :------: | :-----------------------: |
|    a     |            131            |
|    b     |           3674            |

For Question a, I run on my personal PC with

| Type   | Detail    |
| ------ | --------- |
| CPU    | i7-10750H |
| Memory | 32GB      |

Due to enough space and memory, datasets are not needed to split, saving a lot of the i/o time, which is mainly time consumed.

And due strong computation power, compute much faster than server.

### c.  [30 marks] 

SON on MapReduce to find frequent triplets.

Form a triplet by

- if {A, B} {C,D} are frequent pairs, can't form a triplet.
- if {A, B} {A,C} are frequent pairs, forms a candidate {A, B, C}.
    - Though {B, C} may not be frequent pair, just count and that won't more than threshold in the case that {B, C} is not frequent pair

Complete the finding frequent triplets.

```java
/*APrioriTriple.java*/
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class APrioriTriple {
    public static final BigDecimal THRESHOLD = new BigDecimal("0.01");
    public static class APrioriTripleMapper extends Mapper<Object, Text, Text, IntWritable> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\\$");
            int line = split.length;
            BigDecimal countDB = BigDecimal.valueOf(line);

            Map<String, Integer> map = new HashMap<>();
            for (String s : split){
                String[] singleline = s.split(" ");
                for (String string : singleline) {
                    map.put(string, map.getOrDefault(string, 0) + 1);
                }
            }

            Set<String> frequentWords = new HashSet<>();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String str = entry.getKey();
                int singleCount = entry.getValue();

                BigDecimal valueBD = new BigDecimal(singleCount);
                BigDecimal ratio = valueBD.divide(countDB, 8, BigDecimal.ROUND_HALF_UP);
                if (ratio.compareTo(THRESHOLD) > 0){
                    frequentWords.add(str);
                }
            }
            map.clear();
            List<String> stringList = new ArrayList<>(frequentWords);
            Collections.sort(stringList);
            frequentWords = null;
            System.gc();

            for (int i = 0; i < stringList.size()-1; i++) {
                for (int j = i + 1; j < stringList.size(); j++) {
                    String word1 = stringList.get(i);
                    String word2 = stringList.get(j);
                    String pair = "{"+ word1 + "," + word2 + "}";
                    map.put(pair,0);
                }
            }

            for (String s : split) {
                String[] singleline = s.split(" ");
                for (int i = 0; i < singleline.length-1; i++){
                    for (int j = i+1; j <singleline.length; j++) {
                        String[] temp = new String[]{singleline[i], singleline[j]};
                        Arrays.sort(temp);
                        String pair = "{" + temp[0] + "," + temp[1] + "}";
                        if (map.containsKey(pair)) {
                            map.put(pair, map.get(pair) + 1);
                        }
                    }
                }
            }

            Set<String> couple = new HashSet<>();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String str = entry.getKey();
                int singleCount = entry.getValue();

                BigDecimal valueBD = new BigDecimal(singleCount);
                BigDecimal ratio = valueBD.divide(countDB, 8, BigDecimal.ROUND_HALF_UP);
                if (ratio.compareTo(THRESHOLD) > 0){
                    couple.add(str);
                }
            }

            map.clear();
            List<String> coupleList = new ArrayList<>(couple);

            for (int i = 0; i < coupleList.size()-1; i++) {
                for (int j = i + 1; j < coupleList.size(); j++) {
                    String coupleWord1 = coupleList.get(i);
                    String coupleWord2 = coupleList.get(j);
                    coupleWord1 = coupleWord1.substring(1, coupleWord1.length()-1);
                    coupleWord2 = coupleWord2.substring(1, coupleWord2.length()-1);

                    String[] temp1 = coupleWord1.split(",");
                    String[] temp2 = coupleWord2.split(",");

                    String[] toCheck = new String[]{temp1[0], temp2[0], temp1[1], temp2[1]};
                    Arrays.sort(toCheck);

                    String pair;
                    if (toCheck[0].equals(toCheck[1])){
                        pair = "{"+ toCheck[1] + "," + toCheck[2] + "," + toCheck[3] + "}";
                        map.put(pair,0);
                    }else if (toCheck[1].equals(toCheck[2])){
                        pair = "{"+ toCheck[0] + "," + toCheck[1] + "," + toCheck[3] + "}";
                        map.put(pair,0);
                    }else if (toCheck[2].equals(toCheck[3])){
                        pair = "{"+ toCheck[0] + "," + toCheck[1] + "," + toCheck[2] + "}";
                        map.put(pair,0);
                    }
                }
            }

            for (String s : split) {
                String[] singleline = s.split(" ");
                for (int i = 0; i < singleline.length-2; i++){
                    for (int j = i+1; j < singleline.length-1; j++) {
                        for (int k= j+1; k < singleline.length; k++) {
                            String[] temp = new String[]{singleline[i], singleline[j], singleline[k]};
                            Arrays.sort(temp);
                            String pair = "{"+ temp[0] + "," + temp[1] + "," + temp[2] + "}";
                            if (map.containsKey(pair)){
                                map.put(pair, map.get(pair)+1);
                            }
                        }
                    }
                }
            }

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String str = entry.getKey();
                int singleCount = entry.getValue();

                BigDecimal valueBD = new BigDecimal(singleCount);
                BigDecimal ratio = valueBD.divide(countDB, 8, BigDecimal.ROUND_HALF_UP);
                if (ratio.compareTo(THRESHOLD) > 0){
                    Text word = new Text(str);
                    context.write(word, new IntWritable(1));
                }
            }
        }
    }

    public static class APrioriTripleReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            context.write(key, new IntWritable(1));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: APrioriCheck <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(APrioriTriple.class);

        job.setMapperClass(APrioriTripleMapper.class);
        job.setReducerClass(APrioriTripleReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        job.setNumReduceTasks(4);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

```

Count the triples for candidates

```java
/*APrioriTripleCheck.java*/
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class APrioriTripleCheck {
    public static class APrioriTripleCheckMapper extends Mapper<Object, Text, Text, IntWritable> {
        public static Set<String> set = new HashSet<>();
        @Override
        protected void setup(Context context) throws IOException {
            // Get Cache from DistributedCache
            Path[] files = DistributedCache.getLocalCacheFiles(context.getConfiguration());
            for (Path file : files) {
                // Process the file as needed
                BufferedReader reader = new BufferedReader(new FileReader(file.toString()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] temp = line.split("\t");
                    set.add(temp[0]);
                }
                reader.close();
            }
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\\$");

            for (String s : split) {
                String[] singleline = s.split(" ");
                for (int i = 0; i < singleline.length-2; i++){
                    for (int j = i+1; j < singleline.length-1; j++) {
                        for (int k= j+1; k < singleline.length; k++) {
                            String[] temp = new String[]{singleline[i], singleline[j], singleline[k]};
                            Arrays.sort(temp);
                            String pair = "{" + temp[0] + "," + temp[1] + "," + temp[2] + "}";
                            if (set.contains(pair)) {
                                Text word = new Text(pair);
                                context.write(word, new IntWritable(1));
                            }
                        }
                    }
                }
            }
        }
    }

    public static class APrioriTripleCheckReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public static final BigDecimal THRESHOLD = new BigDecimal("0.01");
        public static final int N = 4628551;
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int count = 0;
            for (IntWritable value : values){
                count++;
            }

            BigDecimal valueBD = BigDecimal.valueOf(count);
            BigDecimal ratio = valueBD.divide(new BigDecimal(N), 8, BigDecimal.ROUND_HALF_UP);

            if (ratio.compareTo(THRESHOLD) > 0){
                context.write(key, new IntWritable(count));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 3) {
            System.err.println("Usage: APrioriTripleCheck <dataset> <Candidate> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(APrioriTripleCheck.class);

        job.setMapperClass(APrioriTripleCheckMapper.class);
        job.setReducerClass(APrioriTripleCheckReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        DistributedCache.addCacheFile(new Path(otherArgs[1]).toUri(), job.getConfiguration());
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));

        job.setNumReduceTasks(4);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```

Compile

```bash
../bin/hadoop com.sun.tools.javac.Main APrioriTriple.java
../bin/hadoop com.sun.tools.javac.Main APrioriTripleCheck.java
jar cf APrioriTriple.jar APrioriTriple*.class
jar cf APrioriTripleCheck.jar APrioriTripleCheck*.class
```

Create shell scripts called HW2C.sh

```shell
#!/bin/bash

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
```

Run the program

```bash
chmod +x HW2C.sh
./HW2C.sh
```

Totally find **5095** Triples

Use filter to find the target triples that fulfil the condition

- The count of the frequent triplet ranks Top **50** among all frequent triplets.
- SID = 1155xxxxxx that means $$N = 7, 17, 27, 37, 47$$

```java
package com.monsterxia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripleFilter {
    public static void main(String[] args) {
        final int N = 50;
        String fileName = "APrioriTripleOutput";
        String outputFileName = "filteredAPrioriTripleOutput";

        Map<String, Integer> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TripleFilter.class.getClassLoader().getResource(fileName).getFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 2) {
                    String stringA = parts[0];
                    int valueA = Integer.parseInt(parts[1]);
                    map.put(stringA, valueA);
                }
            }

            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(map.entrySet());
            sortedEntries.sort(Map.Entry.<String, Integer>comparingByValue().reversed());


            // Relocated to /src/main/resources
            String resourcesPath = TripleFilter.class.getClassLoader().getResource(fileName).getPath();
            String outputPath = resourcesPath.substring(0, resourcesPath.indexOf("/target/classes")) + "/src/main/resources/";
            outputFileName = outputPath + outputFileName;
            System.out.println(outputFileName);
            FileWriter fileWriter = new FileWriter(outputFileName);
            int count = 1;
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                if (count % 10 == 7){
                    fileWriter.write(entry.getKey() + "\t" + entry.getValue()+"\n");
                }
                //System.out.println(entry.getKey() + " : " + entry.getValue());
                count++;
                if (count > N) {
                    break;
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

```

Which result in:

```sql
{I,and,it}	699758
{I,for,to}	575291
{I,and,but}	469771
{and,had,to}	423205
{I,to,with}	388455
```

### d. [20 Bonus marks] 

Use the PCY algorithm to filter the candidate pairs in the SON algorithm.

```java
/*PCYAlgorithm.java*/
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class PCYAlgorithm {
    public static final BigDecimal THRESHOLD = new BigDecimal("0.01");
    public static class PCYAlgorithmMapper extends Mapper<Object, Text, Text, IntWritable> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\\$");
            int line = split.length;

            BigDecimal countBD = BigDecimal.valueOf(line);
            Map<String, Integer> map = new HashMap<>();
            Map<Integer, Integer> hashMap = new HashMap<>();

            for (String s : split){
                String[] singleline = s.split(" ");
                for (String string : singleline) {
                    map.put(string, map.getOrDefault(string, 0) + 1);
                }
                for (int i = 0; i < singleline.length-1; i++){
                    for (int j = i+1; j <singleline.length; j++) {
                        String[] temp = new String[]{singleline[i], singleline[j]};
                        Arrays.sort(temp);
                        String pair = "{" + temp[0] + "," + temp[1] + "}";
                        int hashedvalue = pair.hashCode() % 100000;
                        hashMap.put(hashedvalue, hashMap.getOrDefault(hashedvalue, 0)+1);
                    }
                }
            }

            Set<String> frequentWords = new HashSet<>();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String str = entry.getKey();
                int singleCount = entry.getValue();

                BigDecimal valueBD = new BigDecimal(singleCount);
                BigDecimal ratio = valueBD.divide(countBD, 8, BigDecimal.ROUND_HALF_UP);
                if (ratio.compareTo(THRESHOLD) > 0){
                    frequentWords.add(str);
                }
            }
            map.clear();
            List<String> stringList = new ArrayList<>(frequentWords);
            Collections.sort(stringList);
            frequentWords = null;
            System.gc();

            for (int i = 0; i < stringList.size()-1; i++) {
                for (int j = i + 1; j < stringList.size(); j++) {
                    String word1 = stringList.get(i);
                    String word2 = stringList.get(j);
                    String pair = "{"+ word1 + "," + word2 + "}";

                    int hashedvalue = pair.hashCode() % 100000;
                    if (hashMap.containsKey(hashedvalue)){
                        BigDecimal valueBD = new BigDecimal(hashMap.get(hashedvalue));
                        BigDecimal ratio = valueBD.divide(countBD, 8, BigDecimal.ROUND_HALF_UP);
                        if (ratio.compareTo(THRESHOLD) > 0){
                            map.put(pair,0);
                        }
                    }
                }
            }
            hashMap.clear();

            for (String s : split) {
                String[] singleline = s.split(" ");
                for (int i = 0; i < singleline.length-1; i++){
                    for (int j = i+1; j <singleline.length; j++) {
                        String[] temp = new String[]{singleline[i], singleline[j]};
                        Arrays.sort(temp);
                        String pair = "{" + temp[0] + "," + temp[1] + "}";
                        if (map.containsKey(pair)) {
                            map.put(pair, map.get(pair) + 1);
                        }
                    }
                }
            }

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String str = entry.getKey();
                int singleCount = entry.getValue();

                BigDecimal valueBD = new BigDecimal(singleCount);
                BigDecimal ratio = valueBD.divide(countBD, 8, BigDecimal.ROUND_HALF_UP);
                if (ratio.compareTo(THRESHOLD) > 0){
                    Text word = new Text(str);
                    context.write(word, new IntWritable(1));
                }
            }
        }
    }

    public static class PCYAlgorithmReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            context.write(key, new IntWritable(1));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: PCYAlgorithm <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(PCYAlgorithm.class);

        job.setMapperClass(PCYAlgorithmMapper.class);
        job.setReducerClass(PCYAlgorithmReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        job.setNumReduceTasks(4);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

```

PCY only change the progress of finding the candidate, thus we can just use A_Priori's second Mapreduce

Compile
```bash
../bin/hadoop com.sun.tools.javac.Main PCYAlgorithm.java
jar cf PCYAlgorithm.jar PCYAlgorithm*.class
```

Create shell scripts called HW2D.sh

```shell
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
```

Run the program

```bash
chmod +x HW2D.sh
./HW2D.sh
```

This output has the same pairs as a/b, only orders are different. Put it in filter find that get the same result.

```sql
{and,to}	2110730
{I,and}	1979207
{I,to}	1711103
{and,of}	1514801
{of,to}	1219191
{and,in}	1183272
{and,it}	1156305
{and,for}	1151625
{I,of}	1135956
{and,that}	1066298
{in,to}	1003317
{it,to}	986428
{for,to}	986213
{I,it}	958945
{that,to}	955417
{I,that}	927713
{I,in}	927377
{I,for}	919987
{and,with}	895307
{and,my}	881387
{I,my}	856248
{and,on}	803232
{and,but}	796030
{my,to}	777188
{The,and}	770294
{in,of}	737286
{and,have}	717162
{and,they}	716634
{I,but}	709957
{and,had}	696880
```

**Overall Execution Time**

| Question | Overall Execution Time(s) |
| :------: | :-----------------------: |
|    a     |            131            |
|    b     |           3674            |
|    e     |           2602            |

PCY let the candidates to be less thus reduce the time for count them, thus runs quickly.

Delete result and exit

```bash
hadoop fs -rm -r /user
hadoop fs -rm -r /tmp
../sbin/stop-yarn.sh
../sbin/stop-dfs.sh
```


## References
<ol>
    <li>https://mobitec.ie.cuhk.edu.hk/iems5709Fall2024/</li>
    <li>https://blog.csdn.net/qq_36426650/article/details/107104329</li>
</ol>
