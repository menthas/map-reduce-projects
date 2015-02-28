package edu.neu.cs6240.a3;

/**
 * This key is a composite key. The "actual" key is the category. The secondary sort
 * will be performed against the price.
 */
public class TravelData {

    int year;
    int month;
    int day;
    int dayOfWeek;

    int originAirportID;
    int destinationAirportId;

    int airlineID;
    int distance;
    int elapsedTime;
    int canceled;

    int departureDelay; // Can't use for prediction
    int arrivalDelay; // Can't use for prediction

    int arrivalTime; // Can't use for prediction
    int departureTime; // Can't use for prediction

    public TravelData() {}

    public Boolean setParams(String line) {
        try {
            String recordSplitArray[] = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            year = Integer.parseInt(recordSplitArray[0]);
            month = Integer.parseInt(recordSplitArray[2]);
            day = Integer.parseInt(recordSplitArray[3]);
            dayOfWeek = Integer.parseInt(recordSplitArray[4]);

            originAirportID = Integer.parseInt(recordSplitArray[11]);
            destinationAirportId = Integer.parseInt(recordSplitArray[20]);

            airlineID = Integer.parseInt(recordSplitArray[7]);
            distance = Integer.parseInt(recordSplitArray[54]);
            elapsedTime = Integer.parseInt(recordSplitArray[51]);
            canceled = Integer.parseInt(recordSplitArray[47]);

            departureDelay = Integer.parseInt(recordSplitArray[31]);
            arrivalDelay = Integer.parseInt(recordSplitArray[42]);

            String arrTime = recordSplitArray[41];
            arrivalTime = Integer.parseInt(arrTime.substring(0, 2)) +
                    (Integer.parseInt(arrTime.substring(2)) < 30 ? 0 : 1);

            String depTime = recordSplitArray[30];
            departureTime = Integer.parseInt(depTime.substring(0, 2)) +
                    (Integer.parseInt(depTime.substring(2)) < 30 ? 0 : 1);

            return true;
        } catch (NumberFormatException e) {
            System.out.println("Exception caught in map: " + e);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception caught in map: " + e);
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Exception caught in map: " + e);
        }
        return false;
    }
	
    public String dayOrNight(int time){
        String dayOrNight;
        if(time >= 7 && time <= 18){
            dayOrNight = "Day";
        }else{
            dayOrNight = "Night";
        }
        return dayOrNight;
    }
	
	public String concatenateKeys(){
		return Integer.toString(dayOfWeek)
                .concat(Integer.toString(airlineID))
                .concat(Integer.toString(originAirportID))
                .concat(Integer.toString(destinationAirportId));
	}
}