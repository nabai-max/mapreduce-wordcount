package com.drkiettran.mapreduce;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * 
 * Extracted from Hadoop for Dummies (2014)
 *
 */
public class FlightsByCarrier {
	public static void main(String[] args) throws Exception {
		Job job = Job.getInstance();
		job.setJarByClass(FlightsByCarrier.class);
		job.setJobName("FlightsByCarrier");

		TextInputFormat.addInputPath(job, new Path(args[0]));
		job.setInputFormatClass(TextInputFormat.class);

		job.setMapperClass(FlightsByCarrierMapper.class);
		job.setReducerClass(FlightsByCarrierReducer.class);

		TextOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.waitForCompletion(true);
	}
}
