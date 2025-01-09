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
