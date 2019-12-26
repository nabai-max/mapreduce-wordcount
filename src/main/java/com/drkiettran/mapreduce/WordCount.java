package com.drkiettran.mapreduce;

import java.io.*;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Let's see if we could wordcount to work. This is a classic program that is
 * used for concept of mapreduce programming.
 * <p>
 * https://hadoop.apache.org/docs/stable/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html
 */
public class WordCount {
    private static final IntWritable ONE = new IntWritable(1);

    public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
        private static final Logger LOGGER = LoggerFactory.getLogger(TokenizerMapper.class);
        private Text word = new Text();

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            LOGGER.info("input: {}", context.getInputSplit());
            System.out.println(String.format("%d words", itr.countTokens()));
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                context.write(word, ONE);
            }
        }
    }

    public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private static final Logger LOGGER = LoggerFactory.getLogger(TokenizerMapper.class);
        private IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
            LOGGER.info("{} occurs {} times", key, sum);
            System.out.println(String.format("%s occurs %d times", key, sum));
        }
    }

    /**
     * Reading result file store as part-r-00000
     *
     * @param outputPath - expects to be a directory path
     *        localOutputPath - expects to be a file name.
     * @throws IOException
     */
    private static void copyToLocalFile(String outputPath, String localOutputPath) throws IOException {
        String partFile = String.format("hdfs:%s/part-r-00000", outputPath);
        Path pt = new Path(partFile);// Location of file in HDFS
        FileSystem fs = FileSystem.get(new Configuration());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(pt)));
             BufferedWriter bw = new BufferedWriter(new FileWriter(localOutputPath))) {
            String line = br.readLine();
            while (line != null) {
                bw.write(line);
                bw.write('\n');
                line = br.readLine();
            }
        } finally {
            fs.close();
        }
    }

    private static void deleteOutputFolder(String folder) throws IOException {
        FileSystem fs = FileSystem.get(new Configuration());
        Path path = new Path(folder);
        fs.delete(path, true);
    }

    public static void main(String[] argv) throws Exception {
        String localOutputPath = null;
        if (argv.length < 2) {
            System.out.println("at least input file/directory and output directory");
            System.exit(-1);
        } else if (argv.length > 2) {
            localOutputPath = argv[2];
        }
        String inputPath = argv[0];
        String outputPath = argv[1];
        deleteOutputFolder(outputPath);

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Word Count");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        int result = job.waitForCompletion(true) ? 0 : 1;
        if (null != localOutputPath) {
            copyToLocalFile(outputPath, localOutputPath);
        }
        System.exit(result);
    }
}