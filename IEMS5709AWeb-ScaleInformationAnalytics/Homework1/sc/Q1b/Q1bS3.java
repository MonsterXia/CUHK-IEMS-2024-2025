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

public class Q1bS3 {
    public static class Q1bS3Mapper extends Mapper<Object, Text, Text, IntWritable> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\t");
            String[] str_arr = split[0].split("-");

            IntWritable referee = new IntWritable(Integer.parseInt(split[1]));

            if (str_arr.length > 1){
                Arrays.sort(str_arr);

                for (int i = 0; i < str_arr.length-1; i++){
                    for (int j = i+1; j < str_arr.length; j++) {
                        context.write(new Text(str_arr[i]+":"+
                                getDigit(str_arr[j])+"#"+str_arr[j]), referee);
                    }
                }
            }
        }

        private static int getDigit(String str){
            String[] temp = str.split("#");
            int num = Integer.parseInt(temp[0]);
            int digit = 0;
            while (num != 0){
                num /= 10;
                digit++;
            }
            return digit;
        }
    }

    public static class Q1bS3Reducer extends Reducer<Text, IntWritable, IntWritable, Text> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            // key == a#b:d#a#b
            String[] split = key.toString().split(":");
            // split[0] == a#b split[1] == d#a#b
            String[] num = split[0].split("#");
            // num[0] == index, num[1] == numbersOf(index)

            int num1 = Integer.parseInt(num[0]);
            IntWritable index = new IntWritable(num1);

            String[] numb = split[1].split("#");
            // numb[1] == numb, numb[2] == numbersOf(numb)
            int num2 = Integer.parseInt(numb[1]);

            StringBuilder sb = new StringBuilder();
            sb.append(":").append(numb[1]).append(", {");
            int count = 0;
            for (IntWritable value : values){
                sb.append(value).append(",");
                count++;
            }
            sb.deleteCharAt(sb.length()-1).append("}");

            double similarity = (double) count / ( (double)num1 + (double)num2 - (double) count);

            sb.append(similarity);

            context.write(index, new Text(sb.toString()));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1bS3 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1bS3.class);

        job.setMapperClass(Q1bS3Mapper.class);
        // job.setCombinerClass(Q1bS3Reducer.class);
        job.setReducerClass(Q1bS3Reducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}