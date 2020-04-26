package com.drkiettran.mapreduce;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class FlightsByCarriersMapperTest {
	private final String CSV_DATA = "1987,10,14,3,741,730,912,849,PS,1451,NA,91,79,NA,23,11,SAN,SFO,447,NA,NA,0,NA,0,NA,NA,NA,NA,NA\n";

	private Context context;
	private Text text;
	private LongWritable key;

	private FlightsByCarriersMapper fbcm;

	@BeforeEach
	public void init() {
		BasicConfigurator.configure();
		context = Mockito.mock(Context.class);
		text = new Text(CSV_DATA);
		key = new LongWritable(0);
		fbcm = new FlightsByCarriersMapper();
	}

	@Test
	public void shouldDoNothing() throws IOException, InterruptedException {
		FlightsByCarriersMapper fbcm = new FlightsByCarriersMapper();
		LongWritable key = new LongWritable(0);

		fbcm.map(key, text, context);
	}

	@Test
	public void shouldContextWrite() throws IOException, InterruptedException {
		key = new LongWritable(1);
		fbcm.map(key, text, context);
		Mockito.verify(context, Mockito.times(1)).write(Mockito.any(Text.class), Mockito.any(IntWritable.class));
	}

	@Test
	public void shouldWriteTextAndIntWritable() throws IOException, InterruptedException {
		key = new LongWritable(1);
		ArgumentCaptor<Text> textCaptor = ArgumentCaptor.forClass(Text.class);
		ArgumentCaptor<IntWritable> intWritableCaptor = ArgumentCaptor.forClass(IntWritable.class);
		fbcm.map(key, text, context);

		Mockito.verify(context).write(textCaptor.capture(), intWritableCaptor.capture());

		assertThat(textCaptor.getValue(), is(new Text("PS")));
		assertThat(intWritableCaptor.getValue(), is(new IntWritable(1)));
	}
}
