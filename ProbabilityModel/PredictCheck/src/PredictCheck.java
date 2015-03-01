import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Random;


public class PredictCheck {

	public static String dayOrNight(int time) {
		String dayOrNight;
		if (time >= 600 && time <= 1800) {
			dayOrNight = "Day";
		} else {
			dayOrNight = "Night";
		}
		return dayOrNight;
	}

	public static String createKey(String recordSplitArray[]) {
		
		String key = null;
		try{
			//Extracting features from data.
			String day = recordSplitArray[4];
			String airlinesID = recordSplitArray[7];
			String originCityName = recordSplitArray[15];
			String destCityName = recordSplitArray[25];
			String depTime = recordSplitArray[31];
			String arrTime = recordSplitArray[42];
			int depTimeInt = Integer.parseInt(depTime);
			int arrTimeInt = Integer.parseInt(arrTime);
			depTime = dayOrNight(depTimeInt);
			arrTime = dayOrNight(arrTimeInt);

			//Concatenating features
			key = day.concat(airlinesID).concat(originCityName)
					.concat(destCityName)
					.concat(depTime).concat(arrTime);
			
		}catch(Exception e){
			key = "No Key, bad data.";
			System.out.println(e);
		}
		return key;
	}

	public static void main(String[] args) throws Exception {

		try {
			double THRESHOLD = 50.0;
			
			// reading from model and creating hash.
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(args[0]));
			String sCurrentLine;
			String recordSplitArray[];
			Hashtable<String, Float> keyDelay = new Hashtable<String, Float>();
			int numberOfRecords = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				recordSplitArray = sCurrentLine.split("\t");
				keyDelay.put(recordSplitArray[0],
						Float.parseFloat(recordSplitArray[1]));
			}
			br.close();
			
			
			// Predicting delays using hash
			BufferedReader brPredict = null;
			String sCurrentLinePredict;
			String recordSplitArrayPredict[];
			BufferedReader brCheck = null;
			String sCurrentLineCheck;
			String recordSplitArrayCheck[];
			brPredict = new BufferedReader(new FileReader(args[1]));
			brCheck = new BufferedReader(new FileReader(args[2]));
			int KeysMatchCount = 0;
			int correctPrediction = 0;
			int delayCheck;
			while ((sCurrentLinePredict = brPredict.readLine()) != null) {
				numberOfRecords++;
				//Reading predict.csv and check.csv
				sCurrentLineCheck = brCheck.readLine();
				recordSplitArrayCheck = sCurrentLineCheck.split(",");
				delayCheck = Integer.parseInt(recordSplitArrayCheck[46]);
				recordSplitArrayPredict = sCurrentLinePredict.split(",");
				
				//Predict and Check logic
				String keyPredict = createKey(recordSplitArrayPredict);
				if (keyDelay.containsKey(keyPredict)) {
					KeysMatchCount++;
					if ((keyDelay.get(keyPredict) > THRESHOLD && delayCheck == 1) 
							|| (keyDelay.get(keyPredict) <= THRESHOLD && delayCheck == 0)) {
						correctPrediction++;
					}
				}else{ //If key doesn't match, use baseline prediction.
					int pred = (new Random()).nextFloat() >= 0.2382 ? 0 : 1;
		            if(pred == delayCheck){
		            	correctPrediction++;
		            }
				}
			}

			System.out.println("Accuracy: ");
			System.out.println( ((float)correctPrediction * 100)/((float)KeysMatchCount));

			brPredict.close();
			brCheck.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
