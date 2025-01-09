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

public class APrioriTriple {
    public static final BigDecimal THRESHOLD = new BigDecimal("0.01");
    public static class APrioriTripleMapper extends Mapper<Object, Text, Text, IntWritable> {
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

            Set<String> couple = new HashSet<>();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String str = entry.getKey();
                int singleCount = entry.getValue();

                BigDecimal valueBD = new BigDecimal(singleCount);
                BigDecimal ratio = valueBD.divide(countDB, 8, BigDecimal.ROUND_HALF_UP);
                if (ratio.compareTo(THRESHOLD) > 0){
                    couple.add(str);
                }
            }

            map.clear();
            List<String> coupleList = new ArrayList<>(couple);

            for (int i = 0; i < coupleList.size()-1; i++) {
                for (int j = i + 1; j < coupleList.size(); j++) {
                    String coupleWord1 = coupleList.get(i);
                    String coupleWord2 = coupleList.get(j);
                    coupleWord1 = coupleWord1.substring(1, coupleWord1.length()-1);
                    coupleWord2 = coupleWord2.substring(1, coupleWord2.length()-1);

                    String[] temp1 = coupleWord1.split(",");
                    String[] temp2 = coupleWord2.split(",");

                    String[] toCheck = new String[]{temp1[0], temp2[0], temp1[1], temp2[1]};
                    Arrays.sort(toCheck);

                    String pair;
                    if (toCheck[0].equals(toCheck[1])){
                        pair = "{"+ toCheck[1] + "," + toCheck[2] + "," + toCheck[3] + "}";
                        map.put(pair,0);
                    }else if (toCheck[1].equals(toCheck[2])){
                        pair = "{"+ toCheck[0] + "," + toCheck[1] + "," + toCheck[3] + "}";
                        map.put(pair,0);
                    }else if (toCheck[2].equals(toCheck[3])){
                        pair = "{"+ toCheck[0] + "," + toCheck[1] + "," + toCheck[2] + "}";
                        map.put(pair,0);
                    }
                }
            }

            for (String s : split) {
                String[] singleline = s.split(" ");
                for (int i = 0; i < singleline.length-2; i++){
                    for (int j = i+1; j < singleline.length-1; j++) {
                        for (int k= j+1; k < singleline.length; k++) {
                            String[] temp = new String[]{singleline[i], singleline[j], singleline[k]};
                            Arrays.sort(temp);
                            String pair = "{"+ temp[0] + "," + temp[1] + "," + temp[2] + "}";
                            if (map.containsKey(pair)){
                                map.put(pair, map.get(pair)+1);
                            }
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

    public static class APrioriTripleReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            context.write(key, new IntWritable(1));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: APrioriCheck <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(APrioriTriple.class);

        job.setMapperClass(APrioriTripleMapper.class);
        job.setReducerClass(APrioriTripleReducer.class);

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
