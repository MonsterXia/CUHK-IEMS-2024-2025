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