
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/** MedianReducer is reducer class of Assignment1 Version2 program. 
* 	@author Sahil Mehta
*/
public class MedianReducer extends
		Reducer<Text, FloatWritable, Text, FloatWritable> {

	/** calculates median of input ArrayList values. 
	* @param priceList:	An array list of prices of product category whose median is to be calculated.
	* @return median of input values of array list 
	*/
	private float calculateMedian(ArrayList<Float> priceList){
		int size = priceList.size();
		int mid = size/2;
		return (size%2 == 0) ? (priceList.get(mid-1) + priceList.get(mid))/2 : priceList.get(mid);
    }

	/** reduce function of Assignment1 Version2. 
	* @param key is product category in Text format
	* @param values an Iterable containing all the prices for each category.
	* @param output is reducer output which contains category and its median price		 
	*/
	@Override
	public void reduce(Text key, Iterable<FloatWritable> values, Context output)
			throws IOException, InterruptedException {
		
			//System.out.println("Medians of " + key + " are: ");  
			ArrayList<Float> medianList = new ArrayList<Float>();
			for (FloatWritable value : values) {
				//System.out.println(value);
				medianList.add(value.get());
			}
			Collections.sort(medianList);
			float median = calculateMedian(medianList);
			output.write(key, new FloatWritable(median));
	}
}
