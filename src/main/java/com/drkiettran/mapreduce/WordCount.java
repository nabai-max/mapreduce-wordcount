package com.drkiettran.mapreduce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * 
 * https://hadoop.apache.org/docs/stable/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html
 * 
 */
public class WordCount {
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenizerMapper.class);
	private static final IntWritable one = new IntWritable(1);
	
	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
		private Text word = new Text();

		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			LOGGER.info("context: {}", context.getInputSplit());
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken());
				context.write(word, one);
			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
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
		}
	}

	/**
	 * Reading result file store as part-r-0000
	 * 
	 * @param resultFile
	 * @throws IOException
	 */
	private static void printResult(String resultFile) throws IOException {
		String partFile = String.format("hdfs:%s/part-r-00000", resultFile);
		Path pt = new Path(partFile);// Location of file in HDFS
		FileSystem fs = FileSystem.get(new Configuration());

		try (BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(pt)))) {
			String line;
			line = br.readLine();
			while (line != null) {
				System.out.println(line);
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

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		deleteOutputFolder(args[1]);

		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(WordCount.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		int result = job.waitForCompletion(true) ? 0 : 1;
//		printResult(args[1]);
		System.exit(result);
	}
}