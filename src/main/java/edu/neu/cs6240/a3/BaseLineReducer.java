package edu.neu.cs6240.a3;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/** MedianReducer is reducer class of Assignment1 Version4 program. 
* @author Sahil Mehta
*/
public class BaseLineReducer extends
		Reducer<Text, IntWritable, Text, IntWritable> {

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
		int total = 0;
		for (IntWritable value : values) {
			if (value.get() == 1) {
				delayCount++; 
			}
            total++;
		}
		context.write(new Text("Delay Count"), new IntWritable(delayCount));
        context.write(new Text("Total"), new IntWritable(total));
        context.write(new Text("Delay %"), new IntWritable((int) ((float) delayCount / total) *
                100));
	}
}
