import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class APrioriTripleCheck {
    public static class APrioriTripleCheckMapper extends Mapper<Object, Text, Text, IntWritable> {
        public static Set<String> set = new HashSet<>();
        @Override
        protected void setup(Context context) throws IOException {
            // Get Cache from DistributedCache
            Path[] files = DistributedCache.getLocalCacheFiles(context.getConfiguration());
            for (Path file : files) {
                // Process the file as needed
                BufferedReader reader = new BufferedReader(new FileReader(file.toString()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] temp = line.split("\t");
                    set.add(temp[0]);
                }
                reader.close();
            }
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("\\$");

            for (String s : split) {
                String[] singleline = s.split(" ");
                for (int i = 0; i < singleline.length-2; i++){
                    for (int j = i+1; j < singleline.length-1; j++) {
                        for (int k= j+1; k < singleline.length; k++) {
                            String[] temp = new String[]{singleline[i], singleline[j], singleline[k]};
                            Arrays.sort(temp);
                            String pair = "{" + temp[0] + "," + temp[1] + "," + temp[2] + "}";
                            if (set.contains(pair)) {
                                Text word = new Text(pair);
                                context.write(word, new IntWritable(1));
                            }
                        }
                    }
                }
            }
        }
    }

    public static class APrioriTripleCheckReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public static final BigDecimal THRESHOLD = new BigDecimal("0.01");
        public static final int N = 4628551;
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int count = 0;
            for (IntWritable value : values){
                count++;
            }

            BigDecimal valueBD = BigDecimal.valueOf(count);
            BigDecimal ratio = valueBD.divide(new BigDecimal(N), 8, BigDecimal.ROUND_HALF_UP);

            if (ratio.compareTo(THRESHOLD) > 0){
                context.write(key, new IntWritable(count));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 3) {
            System.err.println("Usage: APrioriTripleCheck <dataset> <Candidate> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf);
        job.setJarByClass(APrioriTripleCheck.class);

        job.setMapperClass(APrioriTripleCheckMapper.class);
        job.setReducerClass(APrioriTripleCheckReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        DistributedCache.addCacheFile(new Path(otherArgs[1]).toUri(), job.getConfiguration());
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));

        job.setNumReduceTasks(4);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}