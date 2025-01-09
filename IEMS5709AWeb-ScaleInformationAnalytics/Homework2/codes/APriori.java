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
import java.math.BigDecimal;
import java.util.*;

public class APriori {
    public static final BigDecimal THRESHOLD = new BigDecimal("0.01");
    public static class APrioriMapper extends Mapper<Object, Text, Text, IntWritable> {
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\\$");
            int line = split.length;
            BigDecimal countDB = BigDecimal.valueOf(line);

            Map<String, Integer> map = new HashMap<>();
            for (String s : split){
                String[] singleline = s.split(" ");
                for (String string : singleline) {
                    map.put(string, map.getOrDefault(string, 0) + 1);
                }
            }

            Set<String> frequentWords = new HashSet<>();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String str = entry.getKey();
                int singleCount = entry.getValue();

                BigDecimal valueBD = new BigDecimal(singleCount);
                BigDecimal ratio = valueBD.divide(countDB, 8, BigDecimal.ROUND_HALF_UP);
                if (ratio.compareTo(THRESHOLD) > 0){
                    frequentWords.add(str);
                }
            }
            map.clear();
            List<String> stringList = new ArrayList<>(frequentWords);
            Collections.sort(stringList);
            frequentWords = null;
            System.gc();

            for (int i = 0; i < stringList.size()-1; i++) {
                for (int j = i + 1; j < stringList.size(); j++) {
                    String word1 = stringList.get(i);
                    String word2 = stringList.get(j);
                    String pair = "{"+ word1 + "," + word2 + "}";
                    map.put(pair,0);
                }
            }

            for (String s : split) {
                String[] singleline = s.split(" ");
                for (int i = 0; i < singleline.length-1; i++){
                    for (int j = i+1; j <singleline.length; j++) {
                        String[] temp = new String[]{singleline[i], singleline[j]};
                        Arrays.sort(temp);
                        String pair = "{" + temp[0] + "," + temp[1] + "}";
                        if (map.containsKey(pair)) {
                            map.put(pair, map.get(pair) + 1);
                        }
                    }
                }
            }

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String str = entry.getKey();
                int singleCount = entry.getValue();

                BigDecimal valueBD = new BigDecimal(singleCount);
                BigDecimal ratio = valueBD.divide(countDB, 8, BigDecimal.ROUND_HALF_UP);
                if (ratio.compareTo(THRESHOLD) > 0){
                    Text word = new Text(str);
                    context.write(word, new IntWritable(1));
                }
            }
        }
    }

    public static class APrioriReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            context.write(key, new IntWritable(1));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: APriori <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(APriori.class);

        job.setMapperClass(APrioriMapper.class);
        job.setReducerClass(APrioriReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        job.setNumReduceTasks(4);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}