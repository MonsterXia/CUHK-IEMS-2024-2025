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
