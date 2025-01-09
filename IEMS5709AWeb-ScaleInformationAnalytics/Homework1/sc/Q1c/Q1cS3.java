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