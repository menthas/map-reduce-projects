package edu.neu.cs6240.a3;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * MedianMapper is mapper class of Assignment1 Version4 program.
 * @author Sahil Mehta
 */
public class FlightMapper extends
		Mapper<LongWritable, Text, Text, IntWritable> {

    TravelData data = new TravelData();
	
	/**
     * Read a single line and create the corresponding values.
	 * @param key: is byte offset of each record read from input file.
	 * @param value: is the content of each line as Text.
	 * @param output: is mapper output.
	 */
	@Override
	public void map(LongWritable key, Text value, Context output)
			throws IOException, InterruptedException {

        if (data.setParams(value.toString(), false)) {
            output.write(new Text(data.concatenateKeys()), new IntWritable(data.arrivalDelay));
        }
	}
}
