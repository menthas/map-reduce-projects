import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/** FlightMapper is mapper class 
* 	@author Sahil Mehta
*/
public class FlightMapper extends
		Mapper<LongWritable, Text, Text, IntWritable> {

	public String dayOrNight(int time){
		String dayOrNight;
		if(time >= 600 && time <=1800){
			dayOrNight = "Day";
		}else{
			dayOrNight = "Night";
		}
		return dayOrNight;
	}
	

	/** map function  
	* @param key: is byte offset of each record read from input file.
	* @param value: is the content of each line as Text.
	* @param output: is mapper output.	 
	*/
	@Override
	public void map(LongWritable key, Text value, Context output)
			throws IOException, InterruptedException {
		try{
			
			//Extracting features from data. 
			String record = value.toString();
			String recordSplitArray[] = record.split(",");
			String day = recordSplitArray[4];
			String airlinesID = recordSplitArray[7];
			String originCityName = recordSplitArray[15];
			String destCityName = recordSplitArray[25];
			String depTime = recordSplitArray[31];
			String arrTime = recordSplitArray[42];
			String delay = recordSplitArray[46];
			int depTimeInt = Integer.parseInt(depTime);
			int arrTimeInt = Integer.parseInt(arrTime);
			int delayBool = Integer.parseInt(delay);
			depTime = dayOrNight(depTimeInt);
			arrTime = dayOrNight(arrTimeInt);
			
			//Concatenating features
			String concatenatedKey = day.concat(airlinesID).concat(originCityName)
					   .concat(destCityName)
					   .concat(depTime).concat(arrTime);
			
			output.write(new Text(concatenatedKey) , new IntWritable(delayBool));
			
		}catch(NumberFormatException e){
			System.out.println("Exception caught in map: " + e);
		}
	}
}
