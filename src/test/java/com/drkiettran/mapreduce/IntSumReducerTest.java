package com.drkiettran.mapreduce;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class IntSumReducerTest {
	private final String SHAKESPEARE_DATA = "    We cannot all be masters, nor all masters\n";

	private Context context;
	private Text text;
	private LongWritable key;
	private Iterable<IntWritable> valuesMapped;
	private ArgumentCaptor<Text> textCaptor;
	private ArgumentCaptor<IntWritable> intWritableCaptor;
	private IntSumReducer isr;

	@BeforeEach
	public void init() {
		BasicConfigurator.configure();
		context = Mockito.mock(Context.class);
		text = new Text("");
		key = new LongWritable(0);
		isr = new IntSumReducer();
		valuesMapped = new ArrayList<IntWritable>();
	}

	@Test
	public void shouldInvokeContextWrite() throws IOException, InterruptedException {
		LongWritable key = new LongWritable(0);

		isr.reduce(text, valuesMapped, context);
		Mockito.verify(context, Mockito.times(1)).write(Mockito.any(Text.class), Mockito.any(IntWritable.class));
	}

	@Test
	public void shouldWriteNonZeroSum() throws IOException, InterruptedException {
		text = new Text("Hello");
		List<IntWritable> values = new ArrayList<IntWritable>();
		values.add(new IntWritable(1));
		isr.reduce(text, values, context);

		Mockito.verify(context).write(textCaptor.capture(), intWritableCaptor.capture());
		assertThat(textCaptor.getValue(), is(text));
		assertThat(intWritableCaptor.getValue(), is(new IntWritable(1)));
	}

}
