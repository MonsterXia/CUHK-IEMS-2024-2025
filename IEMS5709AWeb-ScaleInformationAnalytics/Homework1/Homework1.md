# Homework 1

[TOC]

## Statement
I declare that the assignment submitted on Elearning system is original except for source material explicitly acknowledged, and that the same or related material has not been previously submitted for another course. I also acknowledge that I am aware of University policy and regulations on honesty in academic work, and of the disciplinary guidelines and procedures applicable to breaches of such policy and regulations, as contained in the website [https://www.cuhk.edu.hk/policy/academichonesty/](https://www.cuhk.edu.hk/policy/academichonesty/).

<div align=center style="font-size: 18px;">
    Name<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>Your Name</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>SID<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>1155xxxxxx</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><br/>Date<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>11/10/2024</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>Signature <u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>
</div>


<div style="page-break-after: always;"></div>
## Q1 [100 marks + 20 bonus marks]: Similarity Detection for Websites

Enter dataset

```bash
cd /usr/local/hadoop/dataset
```

Download and upload all the datasets

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410141635693.png"/>

Enter scripts

```bash
cd ../scripts
```

Run the server by

```bash
../sbin/start-dfs.sh
../sbin/start-yarn.sh
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410132215409.png"/>

Create location for dataset in HDFS

```bash
../bin/hdfs dfs -mkdir /user
../bin/hdfs dfs -mkdir /user/5709master
../bin/hdfs dfs -mkdir /user/5709master/dataset
../bin/hadoop fs -put /usr/local/hadoop/dataset/* hdfs://hadoop-namenode:54310/user/5709master/dataset
```

### a. [25 marks]

Steps:

|  Step  |       Input       |      Mapper       |          Shuffle           |        Reducer         |
| :----: | :---------------: | :---------------: | :------------------------: | :--------------------: |
| Step1  |     A &nbsp B     |        B A        |         B <A, ...>         |        A-...- B        |
| Step2  |     A-...- B      |    dA:A:dC:C B    | dA:A:dB:B <"C", "D", ...>  |  A ":B,{C,E}, count"   |
| Step3  | A :B,{C,E}, count | A :B,{C,E}, count | A <":B,{C,E}, count", ...> | A :B,{C,E}, count(max) |

Step1
```java
// Find who has refer the content
// A-B-C-D-E-... item
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

public class Q1aS1 {
    public static class Q1aS1Mapper extends Mapper<Object, Text, IntWritable, IntWritable> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split(" ");

            IntWritable referer = new IntWritable(Integer.parseInt(split[0]));
            IntWritable referee = new IntWritable(Integer.parseInt(split[1]));

            context.write(referee,referer);
        }
    }

    public static class Q1aS1Reducer extends Reducer<IntWritable, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            StringBuilder sb = new StringBuilder();
            for (IntWritable value : values){
                sb.append(value.toString()).append("-");
            }

            context.write(new Text(sb.toString()), key);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1aS1 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1aS1.class);

        job.setMapperClass(Q1aS1Mapper.class);
        // job.setCombinerClass(Q1aS1Reducer.class);
        job.setReducerClass(Q1aS1Reducer.class);

        job.setMapOutputKeyClass(IntWritable.class);
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

Step2

```java
// Get all the common item and count
// A:B,{C,E}, count
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.util.Arrays;

public class Q1aS2 {
    public static class Q1aS2Mapper extends Mapper<Object, Text, Text, IntWritable> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\t");
            String[] str_arr = split[0].split("-");

            IntWritable referee = new IntWritable(Integer.parseInt(split[1]));

            if (str_arr.length > 1){
                Integer[] int_arr = new Integer[str_arr.length];
                for(int i = 0; i < int_arr.length; i++){
                    int_arr[i] = Integer.parseInt(str_arr[i]);
                }

                Arrays.sort(int_arr);

                for (int i = 0; i < int_arr.length-1; i++) {
                    for (int j = i+1; j < int_arr.length; j++) {
                        context.write(
                                new Text(
                                        getDigit(int_arr[i])+":"+
                                        int_arr[i].toString()+":"+
                                        getDigit(int_arr[j])+":"+
                                        int_arr[j].toString())
                                , referee);
                    }
                }
            }
        }

        private static int getDigit(int num){
            int digit = 0;
            while (num != 0){
                num /= 10;
                digit++;
            }
            return digit;
        }
    }

    public static class Q1aS2Reducer extends Reducer<Text, IntWritable, IntWritable, Text> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            String[] split = key.toString().split(":");
            int num1 = Integer.parseInt(split[1]);
            IntWritable index = new IntWritable(num1);

            // int num2 = Integer.parseInt(split[3]);

            StringBuilder sb = new StringBuilder();
            sb.append(":").append(split[3]).append(", {");
            int count = 0;
            for (IntWritable value : values){
                sb.append(value).append(",");
                count++;
            }
            sb.deleteCharAt(sb.length()-1).append("}").append(count);

            context.write(index, new Text(sb.toString()));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1aS2 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1aS2.class);

        job.setMapperClass(Q1aS2Mapper.class);
        // job.setCombinerClass(Q1aS2Reducer.class);
        job.setReducerClass(Q1aS2Reducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        job.setNumReduceTasks(4);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

```

Step3

```java
// Find max for each website
// A:B,{C,E}, count
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

public class Q1aS3 {

    public static class Q1aS3Mapper extends Mapper<Object, Text, IntWritable, Text> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\t");
            IntWritable index = new IntWritable(Integer.parseInt(split[0]));
            context.write(index,new Text(split[1]));
        }
    }

    public static class Q1aS3Reducer extends Reducer<IntWritable, Text, IntWritable, Text> {
        @Override
        public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            int index = Integer.parseInt(key.toString());
            if (index % 10000 == 8707){
                int max = Integer.MIN_VALUE;
                String  outStr = "";
                for (Text value : values){
                    String[] split = value.toString().split("}");
                    int num = Integer.parseInt(split[1]);

                    if (num > max){
                        max = num;
                        outStr = split[0];
                    }
                }

                StringBuilder sb = new StringBuilder();
                sb.append(outStr).append("}, ").append(max);

                context.write(key, new Text(sb.toString()));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1aS3 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1aS3.class);

        job.setMapperClass(Q1aS3Mapper.class);
        // job.setCombinerClass(Q1aS3Reducer.class);
        job.setReducerClass(Q1aS3Reducer.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        job.setNumReduceTasks(4);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```

Upload the scripts to the server

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410170521431.png"/>

Back to scripts and compile

```bash
../bin/hadoop com.sun.tools.javac.Main Q1aS1.java
../bin/hadoop com.sun.tools.javac.Main Q1aS2.java
../bin/hadoop com.sun.tools.javac.Main Q1aS3.java
jar cf Q1aS1.jar Q1aS1*.class
jar cf Q1aS2.jar Q1aS2*.class
jar cf Q1aS3.jar Q1aS3*.class
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410140029451.png"/>

Run the program

```bash
../bin/hadoop jar Q1aS1.jar Q1aS1 ./dataset/medium_relation Q1aS1out
../bin/hadoop jar Q1aS2.jar Q1aS2 Q1aS1out Q1aS2out
../bin/hadoop jar Q1aS3.jar Q1aS3 Q1aS2out Q1aS3out
```

Write Back to local

```bash
hadoop fs -get Q1aS3out ./dataout/Q1aout
```

Thus output A:B,{C,D},E where 

```java
A % 10000 == 8707
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410140132461.png"/>

```sql
18498707	:36953516, {678959,14513106,7762628,9059038}, 4
25578707	:105918876, {14824855,15625719,16303112,19697421,1046667,112508246,234270831,40100713}, 8
117388707	:258972719, {11348288,24511462,23946677,23664872,52528988,64403851,82282578,77445273,82306952,78558372,78489448,76945890,77951186,14503322,20593895,813292,9624748,17001441,16273837,18038898,19738807,19664724,19981042,19912980,14159154,113420837,110375558,131031885,127512816,128594355,133379718,108506688,106496571,97190501,86029235,85867676,88956379,36414483,35618002,36683674,44309600,163910499,182360376,178978119,172814402,15780756,15649439,14856957,33669921,33509148,31639629,19006713,67232754,64954762,73050522,69281033,39764035,59062381,61426544,58451375,61162531,58743819,61442217,188591881,118704353,266008963,30313931,21645310,92291389,91046602,18714848,47279531,47616077,17481642}, 74
307478707	:347661232, {26257172,18479519,2557527}, 3
```

### B. [30 marks]

Steps:

| Step  |        Input         |        Mapper        |            Shuffle            |           Reducer            |
| :---: | :------------------: | :------------------: | :---------------------------: | :--------------------------: |
| Step1 |      A &nbsp B       |         A B          |         A <B,C, ...>          |          A#countA B          |
| Step2 |      A#countA B      |      B A#countA      |       B <A#countA, ...>       |       A#countA-...- B        |
| Step3 |   A#countA-...- B    | A#countA:C#countC B  | A#countA:B#countB <"C", ...>  |    A ":B,{C,E}Similarity"    |
| Step4 | A :B,{C,E}Similarity | A :B,{C,E}Similarity | A <":B,{C,E}Similarity", ...> | A :B,{C,E}, Similarity(TOP3) |

Step1:

```java
// Get total items involved in each website
// A#countA B
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
import java.util.ArrayList;
import java.util.List;

public class Q1bS1 {
    public static class Q1bS1Mapper extends Mapper<Object, Text, IntWritable, IntWritable> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split(" ");

            IntWritable referer = new IntWritable(Integer.parseInt(split[0]));
            IntWritable referee = new IntWritable(Integer.parseInt(split[1]));

            context.write(referer,referee);
        }
    }

    public static class Q1bS1Reducer extends Reducer<IntWritable, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            List<IntWritable> valuesList = new ArrayList<>();
            int count = 0;
            for (IntWritable value : values){
                count++;
                valuesList.add(new IntWritable(value.get()));
            }
            StringBuilder sb = new StringBuilder();
            sb.append(key.toString()).append("#").append(count);

            for (IntWritable value : valuesList) {
                context.write(new Text(sb.toString()), value);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1bS1 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1bS1.class);

        job.setMapperClass(Q1bS1Mapper.class);
        // job.setCombinerClass(Q1aS1Reducer.class);
        job.setReducerClass(Q1bS1Reducer.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
```

Step2:

```java
// Find who has refer the content
// A#countA-...- B
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

public class Q1bS2 {
    public static class Q1bS2Mapper extends Mapper<Object, Text, IntWritable, Text> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\t");

            IntWritable referee = new IntWritable(Integer.parseInt(split[1]));

            context.write(referee, new Text(split[0]));
        }
    }

    public static class Q1bS2Reducer extends Reducer<IntWritable, Text, Text, IntWritable> {
        @Override
        public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            StringBuilder sb = new StringBuilder();
            for (Text value : values){
                sb.append(value.toString()).append("-");
            }

            context.write(new Text(sb.toString()), key);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1bS2 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1bS2.class);

        job.setMapperClass(Q1bS2Mapper.class);
        // job.setCombinerClass(Q1bS2Reducer.class);
        job.setReducerClass(Q1bS2Reducer.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
```

Step3:

```java
// Calulate Similarity
// A ":B,{C,E}, Similarity"
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
import java.util.Arrays;

public class Q1bS3 {
    public static class Q1bS3Mapper extends Mapper<Object, Text, Text, IntWritable> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\t");
            String[] str_arr = split[0].split("-");

            IntWritable referee = new IntWritable(Integer.parseInt(split[1]));

            if (str_arr.length > 1){
                Arrays.sort(str_arr);

                for (int i = 0; i < str_arr.length-1; i++){
                    for (int j = i+1; j < str_arr.length; j++) {
                        context.write(new Text(str_arr[i]+":"+
                                getDigit(str_arr[j])+"#"+str_arr[j]), referee);
                    }
                }
            }
        }

        private static int getDigit(String str){
            String[] temp = str.split("#");
            int num = Integer.parseInt(temp[0]);
            int digit = 0;
            while (num != 0){
                num /= 10;
                digit++;
            }
            return digit;
        }
    }

    public static class Q1bS3Reducer extends Reducer<Text, IntWritable, IntWritable, Text> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            // key == a#b:d#a#b
            String[] split = key.toString().split(":");
            // split[0] == a#b split[1] == d#a#b
            String[] num = split[0].split("#");
            // num[0] == index, num[1] == numbersOf(index)

            int num1 = Integer.parseInt(num[0]);
            IntWritable index = new IntWritable(num1);

            String[] numb = split[1].split("#");
            // numb[1] == numb, numb[2] == numbersOf(numb)
            int num2 = Integer.parseInt(numb[1]);

            StringBuilder sb = new StringBuilder();
            sb.append(":").append(numb[1]).append(", {");
            int count = 0;
            for (IntWritable value : values){
                sb.append(value).append(",");
                count++;
            }
            sb.deleteCharAt(sb.length()-1).append("}");

            double similarity = (double) count / ( (double)num1 + (double)num2 - (double) count);

            sb.append(similarity);

            context.write(index, new Text(sb.toString()));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1bS3 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1bS3.class);

        job.setMapperClass(Q1bS3Mapper.class);
        // job.setCombinerClass(Q1bS3Reducer.class);
        job.setReducerClass(Q1bS3Reducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```

Step4:

```java
// Find top 3
// A :B,{C,E}, Similarity
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
import java.util.*;

public class Q1bS4 {
    public static class Q1bS4Mapper extends Mapper<Object, Text, IntWritable, Text> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\t");
            IntWritable index = new IntWritable(Integer.parseInt(split[0]));
            context.write(index,new Text(split[1]));
        }
    }

    public static class Q1bS4Reducer extends Reducer<IntWritable, Text, IntWritable, Text> {
        @Override
        public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            int index = Integer.parseInt(key.toString());
            if (index % 10000 == 8707){
                Map<String, Double> data = new HashMap<>();
                for (Text value : values){
                    String[] split = value.toString().split("}");
                    double num = Double.parseDouble(split[1]);
                    StringBuilder sb = new StringBuilder();
                    sb.append(split[0]).append("}, ").append(num);
                    data.put(sb.toString(),num);
                }

                List<Map.Entry<String, Double>> list = new ArrayList<>(data.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });

                for (int i = 0; i < 3 && i < list.size(); i++) {
                    Map.Entry<String, Double> entry = list.get(i);
                    context.write(key, new Text(entry.getKey()));
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1bS4 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1bS4.class);

        job.setMapperClass(Q1bS4Mapper.class);
        // job.setCombinerClass(Q1bS4Reducer.class);
        job.setReducerClass(Q1bS4Reducer.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}

```

Upload,  compile, run, write back

```bash
../bin/hadoop com.sun.tools.javac.Main Q1bS1.java
../bin/hadoop com.sun.tools.javac.Main Q1bS2.java
../bin/hadoop com.sun.tools.javac.Main Q1bS3.java
../bin/hadoop com.sun.tools.javac.Main Q1bS4.java

jar cf Q1bS1.jar Q1bS1*.class
jar cf Q1bS2.jar Q1bS2*.class
jar cf Q1bS3.jar Q1bS3*.class
jar cf Q1bS4.jar Q1bS4*.class

../bin/hadoop jar Q1bS1.jar Q1bS1 ./dataset/medium_relation Q1bS1out
../bin/hadoop jar Q1bS2.jar Q1bS2 Q1bS1out Q1bS2out
../bin/hadoop jar Q1bS3.jar Q1bS3 Q1bS2out Q1bS3out
../bin/hadoop jar Q1bS4.jar Q1bS4 Q1bS3out Q1bS4out

hadoop fs -get Q1bS4out ./dataout/Q1bout
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410140411435.png"/>

```sql
18498707	:773320, {678959,14513106,9059038,7762628}, 2.0755475437114203E-7
18498707	:2909667, {678959,14513106,7762628}, 1.4013210066286688E-7
18498707	:36329, {7762628,14513106}, 1.0790376753557614E-7
25578707	:6539598, {19697421,14824855,16303112,112508246}, 1.2453958881573467E-7
25578707	:7093358, {16303112,14824855,112508246,1046667}, 1.224287626054567E-7
25578707	:821199, {16303112,14824855,112508246}, 1.1363678116544594E-7
117388707	:17658792, {6519528,813292,30313931,110375558,113420837,106496571,108506688,108508334,108738283,133769089,133379718,69281033,76945890,67232754,73050522,19006713,11348288,15647682,15649439,14159154,17001441,14503322,14856957,52528988,58451375,58743819,59062381,35618002,36414483,35727271,36683674,47616077,44309600,47279531,44615678,15780756,17481642,18714848,742149,494252917,23664872,23946677,24511462,21645310,61442217,61426544,61162531,49276940,20593895,19738807,19981042,19912980,16273837,18212358,18038898,9624748,77445273,82306952,78558372,82282578,77951186,78489448,7152578,19664724,19542313,31639629,33669921,33509148,64954762,64403851,91046602,92291389,90410359,157947395,266008963,188591881,182360376,208469697,118704353,120176956,249677522,258972719,178978119,172814402,177553373,163910499,369505843,127512816,131031885,128594355,39764035,86029235,85867676,88956379,97190501}, 7.034566914000065E-7
117388707	:17001441, {30313931,113420837,108738283,108508334,106496571,110375558,19006713,133769089,133379718,67232754,69281033,76945890,73050522,7152578,9624748,19664724,64403851,64954762,33509148,33669921,31639629,90410359,92291389,91046602,18038898,18212358,82306952,82282578,78489448,77951186,78558372,77445273,11348288,15649439,15647682,14503322,14159154,14856957,6519528,813292,742149,86029235,85867676,88956379,97190501,127512816,131031885,128594355,39764035,16273837,21645310,20593895,49276940,24511462,23946677,23664872,19981042,19912980,19738807,61442217,61162531,61426544,47279531,44615678,44309600,47616077,18714848,15780756,58451375,52528988,59062381,17481642,36414483,35727271,36683674,35618002,266008963,208469697,120176956,118704353,369505843,249677522,258972719,157947395,188591881,182360376,163910499,172814402,178978119,177553373}, 6.696923964420046E-7
117388707	:15780756, {813292,30313931,113420837,108738283,108508334,110375558,108506688,106496571,69281033,73050522,67232754,76945890,19006713,133379718,133769089,742149,17481642,58743819,59062381,58451375,52528988,36683674,35618002,35727271,36414483,44615678,44309600,47279531,47616077,18714848,20593895,21645310,49276940,24511462,23946677,23664872,19738807,19981042,19912980,61442217,61162531,61426544,39764035,97190501,85867676,88956379,86029235,128594355,131031885,127512816,15647682,15649439,11348288,14856957,14159154,17001441,172814402,178978119,163910499,177553373,208469697,118704353,369505843,157947395,266008963,249677522,258972719,182360376,188591881,9624748,7152578,92291389,91046602,90410359,19664724,18038898,18212358,77951186,78558372,82282578,78489448,82306952,77445273,33669921,33509148,31639629,64403851,64954762}, 6.608125929854368E-7
307478707	:62109, {32135710,26257172,13213128,261074213,14885866}, 1.625800486036957E-8
307478707	:41509148, {26257172,2557527,32765540,32135710,19426557}, 1.432714634621234E-8
307478707	:51263598, {2557527,32135710,32765540,19426557,26257172}, 1.3937581378053271E-8
```

### c. [25 marks]

Steps:

| Step  |   Input   | Mapper  |       Shuffle        |      Reducer       |
| :---: | :-------: | :-----: | :------------------: | :----------------: |
| Step1 | A &nbsp B |   B A   |      B <A, ...>      |      A-...- B      |
| Step2 | A-...- B  |  B "T"  |     B <"G", "T">     |      G count       |
|       | B &nbsp G |  B "G"  |                      |                    |
| Step3 |  G count  | G count | G<count, count, ...> | Community G: count |

Step1:

```java
// Find who has refer the content
// A-...- B
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

public class Q1cS1 {
    public static class Q1cS1Mapper extends Mapper<Object, Text, IntWritable, IntWritable> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split(" ");

            IntWritable referer = new IntWritable(Integer.parseInt(split[0]));
            IntWritable referee = new IntWritable(Integer.parseInt(split[1]));

            context.write(referee,referer);
        }
    }

    public static class Q1cS1Reducer extends Reducer<IntWritable, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            StringBuilder sb = new StringBuilder();
            for (IntWritable value : values){
                sb.append(value.toString()).append("-");
            }

            context.write(new Text(sb.toString()), key);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1cS1 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1cS1.class);

        job.setMapperClass(Q1cS1Mapper.class);
        // job.setCombinerClass(Q1aS1Reducer.class);
        job.setReducerClass(Q1cS1Reducer.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

```

Step2:

```java
// If 2 website have same common referee, output : referee "T"
// Output referee's community : referee G
// Referee has "T" ? G 1 : G 0
// G count
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.Objects;

public class Q1cS2 {
    public static class Q1cS2Mapper extends Mapper<Object, Text, IntWritable, Text> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            FileSplit fileSplit = (FileSplit) context.getInputSplit();
            String fileName = fileSplit.getPath().getName();

            if (fileName.matches(".*_label")){
                String[] split = value.toString().split(" ");
                IntWritable index = new IntWritable(Integer.parseInt(split[0]));
                context.write(index, new Text(split[1]));
            }else{
                String[] split = value.toString().split("\t");
                String[] str_arr = split[0].split("-");

                IntWritable referee = new IntWritable(Integer.parseInt(split[1]));
                if (str_arr.length > 1){
                    context.write(referee, new Text("T"));
                }
            }
        }
    }

    public static class Q1cS2Reducer extends Reducer<IntWritable, Text, IntWritable, IntWritable> {
        @Override
        public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            int count = 0;
            String group = "";
            for (Text value : values){
                if (Objects.equals(value.toString(), "T")){
                    count = 1;
                }else {
                    group = value.toString();
                }
            }
            IntWritable out_key = new IntWritable(Integer.parseInt(group));
            IntWritable out_value = new IntWritable(count);

            context.write(out_key, out_value);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 3) {
            System.err.println("Usage: Q1cS2 <in1> <in2> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1cS2.class);

        job.setMapperClass(Q1cS2Mapper.class);
        // job.setCombinerClass(Q1aS2Reducer.class);
        job.setReducerClass(Q1cS2Reducer.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```

Step3:

```java
// add the count
// Community G: count
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

public class Q1cS3 {
    public static class Q1cS3Mapper extends Mapper<Object, Text, IntWritable, IntWritable> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\t");
            IntWritable group = new IntWritable(Integer.parseInt(split[0]));
            IntWritable count = new IntWritable(Integer.parseInt(split[1]));

            context.write(group, count);
        }
    }
    public static class Q1cS3Reducer extends Reducer<IntWritable, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            StringBuilder sb = new StringBuilder();
            sb.append("Community ").append(key.toString()).append(": ");
            int count = 0;
            for (IntWritable value : values){
                count += value.get();
            }

            context.write(new Text(sb.toString()), new IntWritable(count));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1cS3 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1cS3.class);

        job.setMapperClass(Q1cS3Mapper.class);
        // job.setCombinerClass(Q1bS3Reducer.class);
        job.setReducerClass(Q1cS3Reducer.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```

Upload,  compile, run, write back

```bash
../bin/hadoop com.sun.tools.javac.Main Q1cS1.java
../bin/hadoop com.sun.tools.javac.Main Q1cS2.java
../bin/hadoop com.sun.tools.javac.Main Q1cS3.java

jar cf Q1cS1.jar Q1cS1*.class
jar cf Q1cS2.jar Q1cS2*.class
jar cf Q1cS3.jar Q1cS3*.class

../bin/hadoop jar Q1cS1.jar Q1cS1 ./dataset/medium_relation Q1cS1out
../bin/hadoop jar Q1cS2.jar Q1cS2 Q1cS1out ./dataset/medium_label Q1cS2out
../bin/hadoop jar Q1cS3.jar Q1cS3 Q1cS2out Q1cS3out

hadoop fs -get Q1cS3out ./dataout/Q1cout
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410141616878.png"/>

```sql
Community 0: 	23633
Community 1: 	23751
Community 2: 	23705
```

### d. [20 marks] 

Firewall allow port 19888

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410162226109.png"/>

Start history server web UI

```bash
../sbin/mr-jobhistory-daemon.sh start historyserver
```

Other commands are listed in Q1(b)

To set Mapper, need to set max/min split size of input

```java
// FileInputFormat.setMinInputSplitSize(job, size);
// FileInputFormat.setMaxInputSplitSize(job, size);
FileInputFormat.setMinInputSplitSize(job, 64*1024);
FileInputFormat.setMaxInputSplitSize(job, 64*1024*1024);
```

To set Reducer

```java
// job.setNumReduceTasks(num);
job.setNumReduceTasks(4);
```

1st Run:

| #job                   | Mapper num | Reducer num | Max mapper time | Min mapper time | Avg mapper time | Max reducer time | Min reducer time | Avg reducer time | Total time |
| ---------------------- | ---------- | ----------- | --------------- | --------------- | --------------- | ---------------- | ---------------- | ---------------- | ---------- |
| job_1729088003362_0001 | 1          | 1           | 7s              | 7s              | 7s              | 1s               | 1s               | 1s               | 17s        |
| job_1729088003362_0002 | 1          | 1           | 7m, 45s         | 7m, 45s         | 7m, 45s         | 2m, 11s          | 2m, 11s          | 2m, 11s          | 10m, 27s   |
| job_1729088003362_0003 | 22         | 1           | 1m, 17s         | 57s             | 1m, 5s          | 26s              | 26s              | 26s              | 2m, 8s     |

2nd Run:

| #job                   | Mapper num | Reducer num | Max mapper time | Min mapper time | Avg mapper time | Max reducer time | Min reducer time | Avg reducer time | Total time |
| ---------------------- | ---------- | ----------- | --------------- | --------------- | --------------- | ---------------- | ---------------- | ---------------- | ---------- |
| job_1729088003362_0004 | 1          | 4           | 6s              | 6s              | 6s              | 0s               | 1s               | 0s               | 15s        |
| job_1729088003362_0005 | 4          | 4           | 6m, 39s         | 6m, 4s          | 6m, 18s         | 33s              | 32s              | 43s              | 7m, 27s    |
| job_1729088003362_0006 | 24         | 4           | 1m, 16s         | 29s             | 56s             | 24s              | 22s              | 23s              | 1m, 48s    |

3rd Run:

| #job                   | Mapper num | Reducer num | Max mapper time | Min mapper time | Avg mapper time | Max reducer time | Min reducer time | Avg reducer time | Total time |
| ---------------------- | ---------- | ----------- | --------------- | --------------- | --------------- | ---------------- | ---------------- | ---------------- | ---------- |
| job_1729088003362_0007 | 1          | 1           | 7s              | 7s              | 7s              | 1s               | 1s               | 1s               | 18s        |
| job_1729088003362_0008 | 1          | 1           | 7m, 37s         | 7m, 37s         | 7m, 37s         | 2m, 17s          | 2m, 17s          | 2m, 17s          | 10m, 24s   |
| job_1729088003362_0009 | 43         | 1           | 55s             | 5s              | 42s             | 26s              | 26s              | 26s              | 2m, 18s    |

4th Run:

| #job                   | Mapper num | Reducer num | Max mapper time | Min mapper time | Avg mapper time | Max reducer time | Min reducer time | Avg reducer time | Total time |
| ---------------------- | ---------- | ----------- | --------------- | --------------- | --------------- | ---------------- | ---------------- | ---------------- | ---------- |
| job_1729088003362_0010 | 1          | 16          | 6s              | 6s              | 6s              | 2s               | 0s               | 1s               | 24s        |
| job_1729088003362_0011 | 16         | 16          | 4m, 17s         | 2m, 29s         | 3m, 4s          | 1m, 17s          | 31s              | 1m, 2s           | 5m, 44s    |
| job_1729088003362_0012 | 32         | 16          | 1m, 28s         | 17s             | 58s             | 12s              | 1s               | 9s               | 1m, 53s    |

<ul>
    <strong>Observations</strong>
    <li>In the case that Q1(b), input size is very small, far small than a default, if set more mapper, splitting the input into multiple parts and having the servers communicate with each other for these extra tasks will take longer than the time saved by chunking the distributed execution.</li>
    <li>Even though there may be large files (approximately 2.8GB) in the intermediate data, at most only one file will not reach the maximum block size. This has minimal impact on the total time, and the size of intermediate data changes with different data, making it difficult to adjust on the fly. When the number of Mapper tasks exceeds the number of servers, dividing more Mappers does not really reduce the total time and increases the overhead of the Shuffle phase. Hadoop's default partitioning ensures that in the case of large files, each server can have at least one Mapper task running, without being idle. If the file split size is further increased to create fewer Mappers, it may lead to an increase in Mapper runtime due to server idleness.</li>
    <li>When a large file is composed of numerous small files, the default partitioning can result in a large number of Mappers, leading to significant overhead in terms of splitting and shuffling. In such cases, setting a minimum split size can effectively reduce the number of Mappers, thereby decreasing the costs associated with splitting, communication, and shuffling. However, this approach requires a specific data structure for small files and necessitates special handling in certain scenarios.</li>
    <li>For number of Reducers, in the absence of a Combiner, having multiple Reducers can lead to the result data being partitioned. If the goal is to obtain a single output file in the end, it's best to have only one Reducer in the final stage. This way, data can be filtered and selected for output without further partitioning.</li>
    <li>Similarly to Mappers, Reducers should strive to ensure that all servers have tasks to execute, thereby reducing the total processing time. It is important to strike a balance to avoid an excessive number of Reducers, which could lead to fragmentation of input files in the subsequent steps and an increase in overhead during the Mapper phase.</li>
</ul>

### e. [Bonus 20 marks*]

In Q1(b), for logic only need to change the K in Q1bS4.java.

```java
// only change 3->4
// for (int i = 0; i < 3 && i < list.size(); i++) {
for (int i = 0; i < 4 && i < list.size(); i++) {
	Map.Entry<String, Double> entry = list.get(i);
    context.write(key, new Text(entry.getKey()));
}
```

However there are still some question to solve the problem.

In Q1(b), int is enough to store the index value, thus we can use 

```java
IntWritable(int temp)
```

But for example "112746947348183292052" can not be stored in int and used to create IntWritable. Text is to be used for instead.

Also to let IDs are compared as integers rather than strings, we use same solution, we need to let the format of number to be 

```java
// If sort, number with less digit will be arranged first
"theDigitOfNumber$number"
```

Steps:

| Step  |         Input          |         Mapper         |             Shuffle             |           Reducer            |
| :---: | :--------------------: | :--------------------: | :-----------------------------: | :--------------------------: |
| Step1 |       A &nbsp B        |   digitA\$A digitB\$B   | digitA\$A <digitB\$B,digitC\$C, ...> |          digitA\$A#countA digitB\$B          |
| Step2 |       digitA\$A#countA digitB\$B       |       digitB\$B digitA\$A#countA       |        digitB\$B <digitA\$A#countA, ...>        | digitA\$A#countA-...- digitB\$B |
| Step3 | digitA\$A#countA-...- digitB\$B | digitA\$A#countA:digitC\$C#countC digitB\$B | digitA\$A#countA:digitB\$B#countB <"digitC\$C", ...> |   digitA\$A ":digitB\$B.{C,E}Similarity"   |
| Step4 | digitA\$A ":digitB\$B.{C,E}Similarity" | digitA\$A ":digitB\$B.{C,E}Similarity" | digitA\$A <":digitB\$B.{C,E}Similarity", ...> | A :B,{C,E}, Similarity(TOP4) |

Step1:

```java
// Get total items involved in each website
// digitA\$A#countA digitB\$B
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Q1eS1 {
    public static class Q1eS1Mapper extends Mapper<Object, Text, Text, Text> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split(" ");
            StringBuilder sb0 = new StringBuilder();
            StringBuilder sb1 = new StringBuilder();

            sb0.append(split[0].length()).append("$").append(split[0]);
            sb1.append(split[1].length()).append("$").append(split[1]);

            // digitA$A digitB$B
            context.write(new Text(sb0.toString()),new Text(sb1.toString()));
        }
    }

    public static class Q1eS1Reducer extends Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // digitA$A <digitB$B, digitC$C, ...>
            List<Text> valuesList = new ArrayList<>();
            int count = 0;
            for (Text value : values){
                count++;
                valuesList.add(new Text(value.toString()));
            }

            // sb = digitA$A#count
            StringBuilder sb = new StringBuilder();
            sb.append(key.toString()).append("#").append(count);

            for (Text value : valuesList) {
                // digitA$A#count digitB$B
                context.write(new Text(sb.toString()), value);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1bS1 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1eS1.class);

        job.setMapperClass(Q1eS1Mapper.class);
        // job.setCombinerClass(Q1eS1Reducer.class);
        job.setReducerClass(Q1eS1Reducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        job.setNumReduceTasks(4);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

```

Step2:

```java
// Find who has refer the content
// digitB$B digitA$A#count-digitC$C#count-...
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;

public class Q1eS2 {
    public static class Q1eS2Mapper extends Mapper<Object, Text, Text, Text> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // digitA$A#count digitB$B
            String[] split = value.toString().split("\t");

            // digitB$B digitA$A#count
            context.write(new Text(split[1]), new Text(split[0]));
        }
    }

    public static class Q1eS2Reducer extends Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // digitB$B <digitA$A#count, digitC$C#count, ...>
            StringBuilder sb = new StringBuilder();
            for (Text value : values){
                sb.append(value.toString()).append("-");
            }

            // digitB$B digitA$A#count-digitC$C#count-...
            context.write(new Text(sb.toString()), key);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1eS2 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1eS2.class);

        job.setMapperClass(Q1eS2Mapper.class);
        // job.setCombinerClass(Q1bS2Reducer.class);
        job.setReducerClass(Q1eS2Reducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        job.setNumReduceTasks(16);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}

```

Step3:

```java
// Calulate Similarity
// digitA$A :digitB$B. {C,D,...}similarity
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.Arrays;

public class Q1eS3 {
    public static class Q1eS3Mapper extends Mapper<Object, Text, Text, Text> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // digitA$A#count-digitC$C#count-...    digitB$B
            String[] split = value.toString().split("\t");
            String[] str_arr = split[0].split("-");

            Text referee = new Text(split[1]);

            if (str_arr.length > 1){
                Arrays.sort(str_arr);

                for (int i = 0; i < str_arr.length-1; i++){
                    for (int j = i+1; j < str_arr.length; j++) {
                        // digitA$A#count
                        String[] check1 = str_arr[i].split("\\$");
                        String[] check11 = check1[1].split("#");

                        String[] check2 = str_arr[j].split("\\$");
                        String[] check21 = check2[1].split("#");

                        if (check11[0].endsWith("8707") || check21[0].endsWith("8707")){
                            // digitA$A#count:digitC$C#count    digitB$B
                            context.write(new Text(str_arr[i]+":"+str_arr[j]), referee);
                        }
                    }
                }
            }
        }
    }

    public static class Q1eS3Reducer extends Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // digitA$A#count:digitB$B#count    <digitC$C, digitD$D, ...>

            //key = digitA$A#count:digitB$B#count
            String[] split = key.toString().split(":");
            // split[0] == digitA$A#count split[1] == digitB$B#count
            String[] num = split[0].split("#");
            // num[0] == digitA$A, num[1] == numbersOf(digitA$A)
            int num1 = Integer.parseInt(num[1]);

            String[] numb = split[1].split("#");
            // numb[0] == digitB$B, numb[1] == numbersOf(digitB$B)
            int num2 = Integer.parseInt(numb[1]);

            Text index = new Text(num[0]);

            // sb = ::digitB$B, {
            StringBuilder sb = new StringBuilder();
            sb.append(":").append(numb[0]).append(". {");
            int count = 0;
            for (Text value : values){
                String[] temp = value.toString().split("\\$");
                sb.append(temp[1]).append(",");
                count++;
            }
            sb.deleteCharAt(sb.length()-1).append("}");

            double similarity = (double) count / ( (double)num1 + (double)num2 - (double) count);
            sb.append(similarity);

            // digitA$A :digitB$B. {C,D,...}similarity
            context.write(index, new Text(sb.toString()));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1eS3 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1eS3.class);

        job.setMapperClass(Q1eS3Mapper.class);
        // job.setCombinerClass(Q1eS3Reducer.class);
        job.setReducerClass(Q1eS3Reducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        job.setNumReduceTasks(16);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```

Step4:

```java
// Find top 4
// A :B, {C,D,...}, similarity
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
import java.util.*;

public class Q1eS4 {
    public static class Q1eS4Mapper extends Mapper<Object, Text, Text, Text> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // digitA$A :digitB$B. {C,D,...}similarity
            String[] split = value.toString().split("\t");

            // digitA$A :digitB$B. {C,D,...}similarity
            context.write(new Text(split[0]), new Text(split[1]));
        }
    }

    public static class Q1eS4Reducer extends Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // digitA$A <":digitB$B. {C,D,...}similarity", ":digitC$C. {D,E,...}similarity">
            String[] check = key.toString().split("\\$");

            // end with 8707 (1155xxxxxx)
            if (check[1].endsWith("8707")){
                Map<String, Double> data = new HashMap<>();
                for (Text value : values){
                    // :digitB$B. {C,D,...}similarity

                    String[] split = value.toString().split("}");
                    // split[0] = :digitB$B. {C,D,...
                    // split[1] = similarity
                    double num = Double.parseDouble(split[1]);

                    // {":digitB$B", " {C,D,..."}
                    String[] pre = split[0].split("\\.");
                    // {":digitB", "B"}
                    String[] inpre = pre[0].split("\\$");

                    // sb = :B, {C,D,...}, similarity
                    StringBuilder sb = new StringBuilder();
                    sb.append(":").append(inpre[1]).append(",").append(pre[1]).append("}, ").append(num);
                    data.put(sb.toString(),num);
                }

                List<Map.Entry<String, Double>> list = new ArrayList<>(data.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });

                for (int i = 0; i < 4 && i < list.size(); i++) {
                    Map.Entry<String, Double> entry = list.get(i);

                    Text outKey = new Text(check[1]);
                    context.write(outKey, new Text(entry.getKey()));
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1bS4 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1eS4.class);

        job.setMapperClass(Q1eS4Mapper.class);
        // job.setCombinerClass(Q1eS4Reducer.class);
        job.setReducerClass(Q1eS4Reducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```

Upload,  compile, run, write back

```bash
../bin/hadoop com.sun.tools.javac.Main Q1eS1.java
../bin/hadoop com.sun.tools.javac.Main Q1eS2.java
../bin/hadoop com.sun.tools.javac.Main Q1eS3.java
../bin/hadoop com.sun.tools.javac.Main Q1eS4.java

jar cf Q1eS1.jar Q1eS1*.class
jar cf Q1eS2.jar Q1eS2*.class
jar cf Q1eS3.jar Q1eS3*.class
jar cf Q1eS4.jar Q1eS4*.class

../bin/hadoop jar Q1eS1.jar Q1eS1 ./dataset/large_relation Q1eS1out
../bin/hadoop jar Q1eS2.jar Q1eS2 Q1eS1out Q1eS2out
../bin/hadoop jar Q1eS3.jar Q1eS3 Q1eS2out Q1eS3out
../bin/hadoop jar Q1eS4.jar Q1eS4 Q1eS3out Q1eS4out
hadoop fs -get Q1eS4out ./dataout/Q1eout
hadoop fs -get Q1eS3out ./dataout/temp
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202410170519792.png"/>

```sql
101877975660448088707	:116780889332453380140, {100972223169596251967,102638191745753164063,108373054660269328919,101550491289025468870,115097859400552969737,109431899716413562512}, 0.16216216216216217
101877975660448088707	:109565531665669537977, {100972223169596251967,105473557341367536650,102638191745753164063,117593496713159306165,110385681616478345963}, 0.16129032258064516
101877975660448088707	:117888233045202400417, {105473557341367536650,100972223169596251967,117593496713159306165,109146002063561258874,102638191745753164063,101885213279476407823,114248789033154861902,109431899716413562512,108373054660269328919,115097859400552969737}, 0.15625
101877975660448088707	:103396020916288393634, {108373054660269328919,110385681616478345963,109431899716413562512,117593496713159306165,102638191745753164063,105241256005026558591,117054813244844825026}, 0.14893617021276595
102108625619739868707	:103804737747482287595, {101149790069455088286,102717421433762219481,106422711035746240833,108189587050871927626,103073491679741548304,111930343403502205059,115474229489284556714,112771323377266882686,106258380218567160284,103125970510649691211,115515252844412246894,109216170916494348723,104558253096863947788,105037104815911535960,102034052532213921846,110107698878563510929,110255193504699698349,116626367309939192330,103334611312899628094,109524702084450613382,116059998563577101559,101616131029261770405,103354693083460731610,109147099159025717839,116886129421619920925,100180575185522802907}, 0.18181818181818182
102108625619739868707	:105716316100591540262, {104437754419996754786,109824807810200422771,106641576811513429429,108249232416813189692,109404491328800973702,101174951617223562807,103073491679741548304,109283284891179095023,113735310430199015099,105541557736879337565,109524702084450613382,116059998563577101559,105037104815911535960,112600205125796554595,103354693083460731610,113364856660738964005,106258380218567160284,109735868860370294412,110255193504699698349,118075919496626375798,111930343403502205059,106356964679457437002,109213463086683318539,101149790069455088286}, 0.15483870967741936
102108625619739868707	:103334611312899628094, {100535338638690515342,115474229489284556714,118207880179234484617,102717421433762219481,101149790069455088286,106422711035746240833,106258380218567160284,109735868860370294412,109283284891179095023,103073491679741548304,108189587050871927626,102034052532213921846,116886129421619920925,113735310430199015099,105831704374179338123,104047378152483720732,100180575185522802907,105541557736879337565,109824807810200422771,115515252844412246894,114484815067147880059,102579928162855977219,109813896768294978303,101616131029261770405,103354693083460731610,110255193504699698349,116626367309939192330,109524702084450613382,112600205125796554595}, 0.1518324607329843
102108625619739868707	:103681029286159969307, {103354693083460731610,109813896768294978303,101616131029261770405,109147099159025717839,110634877589748180450,110107698878563510929,106356964679457437002,109213463086683318539,101149790069455088286,118207880179234484617,112600205125796554595,105037104815911535960,115515252844412246894,112771323377266882686,116626367309939192330,109524702084450613382,116059998563577101559,102034052532213921846,108249232416813189692,116886129421619920925,104047378152483720732,113735310430199015099,108189587050871927626,111930343403502205059,107033731246200681031,115474229489284556714,106258380218567160284}, 0.15168539325842698
103200386324523658707	:116304446406743623130, {104560124403688998130,107940613488592745016}, 0.16666666666666666
103200386324523658707	:107059122075811840426, {104560124403688998130,107940613488592745016}, 0.14285714285714285
103200386324523658707	:117503370035998183213, {104560124403688998130,107940613488592745016}, 0.14285714285714285
103200386324523658707	:118260465081908288293, {104560124403688998130,114989358912013434562}, 0.125
110715095928031808707	:114590717897717187485, {113718269858549593086,116426018234598080082,113397391314808142377,115049735125827192234,111875665525715264613,110814750979235028568,115895618107840963558,114820480752300993897,108584940222628465197,100578202127502407110,103753246154433626800,116429435802395645602,106247049058205465708,100163238332537499590,112373237775458072009,104145159340267405226,106951987054299687353,104472648636466535478,107520568772200234214,113435302917113419967,117804484225868285861,101872053529554144352,105040058487223563359,108055614721877801906,117359612091376902625,107188788248244608960,106407292572142932917,101010784922652715019}, 0.10606060606060606
110715095928031808707	:115599601881370161920, {116426018234598080082,113718269858549593086,103458647163810854709,104145159340267405226,100163238332537499590,112373237775458072009,102349987850176560767,118269406419478362903,106202342569754356874,108584940222628465197,103429825907085420708,105093931597417731658,107188788248244608960,116429435802395645602,115592034719992693966,114822593626242568956,117804484225868285861,113435302917113419967,113991510450146807429,102000765766827851357,105613252741181373606,100578202127502407110,111875665525715264613,106407292572142932917,101010784922652715019,100534386183913201796,103049373110021341274}, 0.09926470588235294
110715095928031808707	:112269775811709161986, {118122556596388698594,105237212888595777026,110023904208758175749,112292414198276054105,115695578304416858666,107120385844936556538,102122012748196677496,101111725148957537944,106600962597764825752,113102862520765968593,113210096909000624344,109895887909967698712,105144963864645396994,117198316664830078870,116420972797550666139,103861741370066605534,100974258168375166698,114820480752300993897,113097276181543898581,117697379995591819348,105508660898837488937,114274687956791581930,108285296632855004178,118147897097670149368,104279107049574225563,108729164162563480440,104799160568450312853,107738139256801286428,103637162978776725639,102600774742322762233,102533732658641069179,101482635367518865552,113959435258864431400,118107045405823607902,105756423183388339729}, 0.09722222222222222
110715095928031808707	:117690926606277057266, {114245534338887342716,105093931597417731658,105489065160959389682,112622420762576522136,115854718917136776960,118147897097670149368,105168276680128924520,107066609145001672629,101482635367518865552,118107045405823607902,107252020925742571259,103958441795544241160,106237697616959334125,110023904208758175749,117951052831974893969,102393079978161767139,113882100233272026660,110834083330330746179,114820480752300993897,104799160568450312853,107738139256801286428,104279107049574225563,108729164162563480440,104672614700283598137}, 0.08108108108108109
111360564525526218707	:115124440650414300595, {102430203479378160638}, 1.0
111360564525526218707	:114923296774156115439, {102430203479378160638}, 1.0
111360564525526218707	:111736405112756618471, {102430203479378160638}, 0.3333333333333333
111360564525526218707	:117839057504369757545, {102430203479378160638}, 0.3333333333333333
118082806863266758707	:118109751794658508592, {102150693225130002919}, 0.14285714285714285
118082806863266758707	:118094233569925092212, {102150693225130002919}, 0.08333333333333333
118082806863266758707	:118146682064597320987, {102150693225130002919}, 0.07692307692307693
118082806863266758707	:118168425931104247072, {102150693225130002919}, 0.07142857142857142
```

Delete result and exit

```bash
hadoop fs -rm -r /user
hadoop fs -rm -r /tmp
../sbin/stop-yarn.sh
../sbin/stop-dfs.sh
```

## Q2 [20 marks]: Q&As for HW#0 Review

### a. [10 marks] 

#### 1) Which default ports do machines (VMs) in a multi-node Hadoop cluster use for inter-machine communications (i.e., transmission of network traffic between machines in the cluster)? Name at least 2 ports and describe their roles. 

**Refers to <a href="https://ambari.apache.org/1.2.4/installing-hadoop-using-ambari/content/reference_chap2_1.html">Apache Offcial Website</a>**

- **NameNode WebUI**
    - <strong>50070</strong> : Web UI to look at current status of HDFS, explore file system
    - <strong>50470</strong> : Secure http service
- **NameNode metadata service**
    - <strong>8020/9000</strong> : File system metadata operations
- <strong>DataNode</strong>
    - <strong>50075</strong> : DataNode WebUI to access the status, logs etc.
    - <strong>50475</strong> : Secure http service
    - <strong>50010</strong> : Data transfer
    - <strong>50020</strong> : Metadata operations
- <strong>Secondary NameNode</strong>
    - <strong>50090</strong> : Checkpoint for NameNode metadata
- <strong>JobTracker  WebUI</strong>
    - <strong>50030</strong> : Web UI for JobTracker

#### 2) Are you using public or private IPs of the VMs to access SSH? Are machines in your Hadoop cluster using public or private IPs to identify and communicate with each other? 

I am using public IPs to access SSH, since my PC are not in the same network as the VMs while using Google Cloud.

Machines in my Hadoop cluster using private IPs to identify and communicate with each other because every time when start the VMs, every VM will get an IP and that IP may different from every start due to Google's IP policy. Thus if using public IP, I will need to change it every time. Private IPs always remain the same as the VMs have not been delete.

#### 3) To ensure proper communication between machines, did you set up extra firewall rules/policies as you did for SSH (port 22) in HW#0? Why or why not?

I have set the firewall rules to protect my SSH.

Only the SSH traffic from CUHK network or my static IP or private network can login the VMs thus as long as there are no bad guy in CUHK network, my VMs can remain safe even if I didn't protect my secret key well. And of course I do keep my secret key secret.

### b. [10 marks] 

Consider a Hadoop cluster with the following configurations: 

<ol>
    <li>You have allocated 100GB of disk space for each VM in your Google Cloud<del>/AWS</del> Console. </li>
    <li>There are at most 4 such VMs that can be utilized by the Hadoop cluster.</li>
</ol>

 Given this setup, please evaluate the feasibility of taking up 150GB of total disk space on the HDFS (Hadoop Distributed File System).

<ul>
    <li>The cluster may not have the ability to deal with 150GB data.</li>
    <li>For example, if set the replication of HDFS to 3. In that case, it will actul take 150 × 3 = 450GB > 400G(total disk space).</li>
    <li>Meanwhile ,there are some intermediate data exist during the map-reduce process, which will aslo take some place.</li>
</ul>

## References
<ol>
    <li>https://mobitec.ie.cuhk.edu.hk/iems5709Fall2024/</li>
    <li>https://ambari.apache.org/1.2.4/installing-hadoop-using-ambari/content/reference_chap2_1.html</li>
</ol>
