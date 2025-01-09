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
