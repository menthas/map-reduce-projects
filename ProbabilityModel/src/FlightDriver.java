import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Random;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/** FlightDriver is driver class 
 * 
 * @author Sahil Mehta
 */
public class FlightDriver {

		public static void main(String[] args) throws Exception {
		// Map Reduce Job for creating the model.
		Job job = new Job();
		job.setJarByClass(FlightDriver.class);
		job.setJobName("Flight Delay Prediction");
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setMapperClass(FlightMapper.class);
		job.setReducerClass(FlightReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputValueClass(IntWritable.class);
		job.waitForCompletion(true);
	}
}
