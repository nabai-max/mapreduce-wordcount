package com.drkiettran.mapreduce;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Let's see if we could wordcount to work. This is a classic program that is
 * used for concept of mapreduce programming.
 * 
 * Added comments.
 * <p>
 * https://hadoop.apache.org/docs/stable/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html
 */
public class WordCount {
	private static final IntWritable ONE = new IntWritable(1);

	/**
	 * Reading result file store as part-r-00000
	 *
	 * @param outputPath - expects to be a directory path localOutputPath - expects
	 *                   to be a file name.
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
