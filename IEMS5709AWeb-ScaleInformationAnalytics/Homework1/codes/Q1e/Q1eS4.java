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