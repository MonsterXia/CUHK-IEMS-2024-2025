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
