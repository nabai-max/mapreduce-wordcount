package com.drkiettran.mapreduce;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class FlightsByCarriersReducerTest {
	private final String CARRIER_DATA = "PS";

	private Context context;
	private Text text;
	private LongWritable key;
	private Iterable<IntWritable> carriersMapped;

	private FlightsByCarriersReducer fbcr;
	private ArgumentCaptor<Text> textCaptor;
	private ArgumentCaptor<IntWritable> intWritableCaptor;

	@BeforeEach
	public void init() {
		textCaptor = ArgumentCaptor.forClass(Text.class);
		intWritableCaptor = ArgumentCaptor.forClass(IntWritable.class);
		BasicConfigurator.configure();
		context = Mockito.mock(Context.class);
		text = new Text(CARRIER_DATA);
		key = new LongWritable(0);
		carriersMapped = new ArrayList<IntWritable>();
		fbcr = new FlightsByCarriersReducer();
	}

	@Test
	public void shouldInvokeContextWrite() throws IOException, InterruptedException {
		LongWritable key = new LongWritable(0);

		fbcr.reduce(text, carriersMapped, context);
		Mockito.verify(context, Mockito.times(1)).write(Mockito.any(Text.class), Mockito.any(IntWritable.class));
	}

	@Test
	public void shouldWriteZeroSum() throws IOException, InterruptedException {
		fbcr.reduce(text, carriersMapped, context);

		Mockito.verify(context).write(textCaptor.capture(), intWritableCaptor.capture());
		assertThat(textCaptor.getValue(), is(new Text("PS")));
		assertThat(intWritableCaptor.getValue(), is(new IntWritable(0)));
	}

	@Test
	public void shouldWriteNonZeroSum() throws IOException, InterruptedException {
		ArrayList<IntWritable> intWritableList = new ArrayList<IntWritable>();
		intWritableList.add(new IntWritable(1));
		intWritableList.add(new IntWritable(1));

		fbcr.reduce(text, intWritableList, context);
		Mockito.verify(context).write(textCaptor.capture(), intWritableCaptor.capture());

		assertThat(textCaptor.getValue(), is(new Text("PS")));
		assertThat(intWritableCaptor.getValue(), is(new IntWritable(2)));
	}
}
