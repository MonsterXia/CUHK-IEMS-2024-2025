
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