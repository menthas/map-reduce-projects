
import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/** FlightReducer is reducer class 
* @author Sahil Mehta
*/
public class FlightReducer extends
		Reducer<Text, IntWritable, Text, FloatWritable> {
	
	/** reduce function 
	* @param key is concatenated key in Text format
	* @param values an Iterable containing all the values for each key.
	* @param output is reducer output 	 
	*/
	@Override
	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		
		//Calculating Delay Percentage
		int delayCount = 0;
		int notDelayCount = 0;
		for (IntWritable value : values) {
			if(value.get()==1){
				delayCount++; 
			}else{
				notDelayCount++;
			}
		}
		float delayPercentage = ((float)delayCount/((float)delayCount + (float)notDelayCount))*100;
	
		context.write(key, new FloatWritable(delayPercentage));
			
	}
}
