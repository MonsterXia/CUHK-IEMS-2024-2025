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
import java.util.Arrays;

public class Q1eS3 {
    public static class Q1eS3Mapper extends Mapper<Object, Text, Text, Text> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // digitA$A#count-digitC$C#count-...    digitB$B
            String[] split = value.toString().split("\t");
            String[] str_arr = split[0].split("-");

            Text referee = new Text(split[1]);

            if (str_arr.length > 1){
                Arrays.sort(str_arr);

                for (int i = 0; i < str_arr.length-1; i++){
                    for (int j = i+1; j < str_arr.length; j++) {
                        // digitA$A#count
                        String[] check1 = str_arr[i].split("\\$");
                        String[] check11 = check1[1].split("#");

                        String[] check2 = str_arr[j].split("\\$");
                        String[] check21 = check2[1].split("#");

                        if (check11[0].endsWith("8707") || check21[0].endsWith("8707")){
                            // digitA$A#count:digitC$C#count    digitB$B
                            context.write(new Text(str_arr[i]+":"+str_arr[j]), referee);
                        }
                    }
                }
            }
        }
    }

    public static class Q1eS3Reducer extends Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // digitA$A#count:digitB$B#count    <digitC$C, digitD$D, ...>

            //key = digitA$A#count:digitB$B#count
            String[] split = key.toString().split(":");
            // split[0] == digitA$A#count split[1] == digitB$B#count
            String[] num = split[0].split("#");
            // num[0] == digitA$A, num[1] == numbersOf(digitA$A)
            int num1 = Integer.parseInt(num[1]);

            String[] numb = split[1].split("#");
            // numb[0] == digitB$B, numb[1] == numbersOf(digitB$B)
            int num2 = Integer.parseInt(numb[1]);

            Text index = new Text(num[0]);

            // sb = ::digitB$B, {
            StringBuilder sb = new StringBuilder();
            sb.append(":").append(numb[0]).append(". {");
            int count = 0;
            for (Text value : values){
                String[] temp = value.toString().split("\\$");
                sb.append(temp[1]).append(",");
                count++;
            }
            sb.deleteCharAt(sb.length()-1).append("}");

            double similarity = (double) count / ( (double)num1 + (double)num2 - (double) count);
            sb.append(similarity);

            // digitA$A :digitB$B. {C,D,...}similarity
            context.write(index, new Text(sb.toString()));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Q1eS3 <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(Q1eS3.class);

        job.setMapperClass(Q1eS3Mapper.class);
        // job.setCombinerClass(Q1eS3Reducer.class);
        job.setReducerClass(Q1eS3Reducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        job.setNumReduceTasks(16);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}