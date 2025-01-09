package com.monsterxia;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

public class KMeans {
    public static class KMeansMapper extends Mapper<Object, Text, Text, Text> {
        static BigInteger[][] average = new BigInteger[10][256];
        @Override
        protected void setup(Context context) throws IOException {
            // Get Cache from DistributedCache
            Path[] files = DistributedCache.getLocalCacheFiles(context.getConfiguration());
            for (Path file : files) {
                // Process the file as needed
                BufferedReader reader = new BufferedReader(new FileReader(file.toString()));
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    String[] pixels = line.split(",");
                    for (int j = 0; j < pixels.length; j++) {
                        average[i][j] = new BigInteger(pixels[j]);
                    }
                    i++;
                }
                reader.close();
            }
        }

        @Override
        public void map(Object key, Text value, Mapper.Context context) throws IOException, InterruptedException {
            int tag = closest(value.toString(), average);

            Text index = new Text(String.valueOf(tag));
            context.write(index, new Text(value.toString()));
        }

        public static int closest(String image, BigInteger[][] average) {
            BigInteger[] distance = new BigInteger[10];
            for (int i = 0; i < 10; i++) {
                distance[i] = new BigInteger("0");
            }

            String[] temp = image.split(",");
            for (int i = 0; i < temp.length; i++) {
                BigInteger test = new BigInteger(temp[i]);
                for (int j = 0; j < 10; j++) {
                    BigInteger temp2 = average[j][i].subtract(test).abs();
                    distance[j] = distance[j].add(
                            temp2.multiply(temp2)
                    );
                }
            }

            BigInteger min = distance[0];
            int minIndex = 0;
            for (int i = 1; i < distance.length; i++) {
                if (distance[i].compareTo(min) < 0) {
                    min = distance[i];
                    minIndex = i;
                }
            }

            return minIndex;
        }
    }

    public static class KMeansReducer extends Reducer<Text, Text, Text, Text> {
        public static final BigDecimal THRESHOLD = new BigDecimal("0.01");
        public static final int N = 4628551;
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            BigInteger[] sum = new BigInteger[784];
            Arrays.fill(sum, new BigInteger("0"));

            int count = 0;
            for (Text value : values){
                String[] pixels = value.toString().split(",");
                for (int i = 0; i < pixels.length; i++) {
                    sum[i] = sum[i].add(new BigInteger(pixels[i]));
                }

                count++;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 784; i++) {
                if (sum[i].compareTo(new BigInteger("0")) != 0) {
                    sum[i] = sum[i].divide(new BigInteger(String.valueOf(count)));
                }
                sb.append(sum[i]).append(",");
            }
            sb.deleteCharAt(sb.length()-1).append("\n");

            Text result = new Text(sb.toString());
            Text empty = new Text("");
            context.write(result, empty);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: KMeans <dataset> <out>");
            System.exit(2);
        }
        int rounds = 1;
        boolean stop = false;
        System.out.println("Round <>: D(curr, prev) = <>");
        while (!stop) {
            String[] prev = getCenter(new Path(otherArgs[1]));

            Job job = Job.getInstance(conf);
            job.setJarByClass(KMeans.class);

            job.setMapperClass(KMeansMapper.class);
            job.setReducerClass(KMeansReducer.class);

            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);

            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);

            FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
            DistributedCache.addCacheFile(new Path(otherArgs[1]).toUri(), job.getConfiguration());
            FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

            job.waitForCompletion(true);

            String[] curr = getCenter(new Path(otherArgs[1]));
            stop = isStop(prev, curr, rounds);
            rounds++;
        }

        System.exit(0);
    }

    static String[] getCenter(Path path) {
        String[] center = new String[10];
        Configuration conf = new Configuration();
        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
            FSDataInputStream in = fs.open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                center[index++] = line;
            }
            reader.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return center;
    }

    static boolean isStop(String[] prev, String[] curr, int rounds) {
        double sum = 0;
        for (int i = 0; i < 10; i++) {
            String[] prevPixels = prev[i].split(",");
            String[] currPixels = curr[i].split(",");
            for (int j = 0; j < prevPixels.length; j++) {
                int temp = Integer.parseInt(prevPixels[j]) - Integer.parseInt(currPixels[j]);
                sum += Math.pow(temp, 2);
            }
        }
        sum = Math.sqrt(sum);

        System.out.println("Round " + rounds + ": D(curr, prev) = " + sum);
        if (sum < 0.05) {
            System.out.println("Achieve convergence at round = " + rounds);
            return true;
        }else {
            return false;
        }
    }
}
