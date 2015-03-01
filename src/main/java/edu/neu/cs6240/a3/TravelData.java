package edu.neu.cs6240.a3;

/**
 * This key is a composite key. The "actual" key is the category. The secondary sort
 * will be performed against the price.
 */
public class TravelData {

    boolean split_csv;

    int year;
    int month;
    int day;
    int dayOfWeek;

    String originAirportID;
    String destinationAirportId;

    int originAirportIdNum;
    int destinationAirportIdNum;

    int airlineID;
    int distance;
    int elapsedTime;
    int canceled;

    int departureDelay; // Can't use for prediction
    int arrivalDelay; // Can't use for prediction

    int arrivalTime;
    int departureTime;

    public TravelData(boolean split_csv) {
        this.split_csv = split_csv;
    }

    public Boolean setParams(String line, boolean for_pred) {
        try {
            String recordSplitArray[] = line.split(
                    split_csv ? ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)" : ","
            );

            if (split_csv) {
                year = Integer.parseInt(recordSplitArray[0]);
                month = Integer.parseInt(recordSplitArray[2]);
                day = Integer.parseInt(recordSplitArray[3]);
            }
            dayOfWeek = Integer.parseInt(recordSplitArray[4]);

            if (split_csv) {
                originAirportIdNum = Integer.parseInt(recordSplitArray[11]);
                destinationAirportIdNum = Integer.parseInt(recordSplitArray[20]);
            } else {
                originAirportID = recordSplitArray[15];
                destinationAirportId = recordSplitArray[25];
            }

            airlineID = Integer.parseInt(recordSplitArray[7]);
            if (split_csv) {
                distance = Integer.parseInt(recordSplitArray[54]);
                elapsedTime = Integer.parseInt(recordSplitArray[51]);
                canceled = Integer.parseInt(recordSplitArray[47]);

                departureDelay = Integer.parseInt(recordSplitArray[31]);
            }
            arrivalDelay = Integer.parseInt(recordSplitArray[split_csv ? 42 : 46]);

            try { // Some records have malformed CRSTime ?? use actual time instead
                String arrTime = recordSplitArray[split_csv ? 40 : 42];
                arrivalTime = Integer.parseInt(arrTime.substring(0, 2)) +
                        (Integer.parseInt(arrTime.substring(2)) < 30 ? 0 : 1);
            } catch (NumberFormatException e) {
                if (for_pred) // don't do it for prediction
                    throw e;
                String arrTime = recordSplitArray[split_csv ? 41 : 43];
                arrivalTime = Integer.parseInt(arrTime.substring(0, 2)) +
                        (Integer.parseInt(arrTime.substring(2)) < 30 ? 0 : 1);
            }

            try { // Some records have malformed CRSTime ?? use actual time instead
                String depTime = recordSplitArray[split_csv ? 29 : 31];
                departureTime = Integer.parseInt(depTime.substring(0, 2)) +
                        (Integer.parseInt(depTime.substring(2)) < 30 ? 0 : 1);
            } catch (NumberFormatException e) {
                if (for_pred) // don't do it for prediction
                    throw e;
                String depTime = recordSplitArray[split_csv ? 30 : 32];
                departureTime = Integer.parseInt(depTime.substring(0, 2)) +
                        (Integer.parseInt(depTime.substring(2)) < 30 ? 0 : 1);
            }

            return true;
        } catch (NumberFormatException e) {
            System.out.println("Bad input:" + line + "\n Exception " + e);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Bad input:" + line + "\n Exception " + e);
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Bad input:" + line + "\n Exception " + e);
        }
        return false;
    }

    public int ArrivalDelay15() {
        return arrivalDelay >= 15 ? 1 : 0;
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
                .concat(originAirportID)
                .concat(destinationAirportId)
                .concat(dayOrNight(departureTime))
                .concat(dayOrNight(arrivalTime));
	}
}