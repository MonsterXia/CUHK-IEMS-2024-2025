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