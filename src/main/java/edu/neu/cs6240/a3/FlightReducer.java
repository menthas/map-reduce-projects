package edu.neu.cs6240.a3;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/** MedianReducer is reducer class of Assignment1 Version4 program. 
* @author Sahil Mehta
*/
public class FlightReducer extends
		Reducer<Text, IntWritable, Text, FloatWritable> {

	/**
     * Calculate the probability for the given flight detail and emit it.
	 * @param key is the flight detail identifier
	 * @param values an Iterable containing all the labels
	 * @param context Job context
	 */
	@Override
	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		
		int delayCount = 0;
		int notDelayCount = 0;
		for (IntWritable value : values) {
			if (value.get() == 1) {
				delayCount++; 
			} else {
				notDelayCount++;
			}
		}
		float delayPercentage = ((float)delayCount/((float)delayCount + (float)notDelayCount))*100;
		
		context.write(key, new FloatWritable(delayPercentage));
	}
}
