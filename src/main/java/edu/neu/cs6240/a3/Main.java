package edu.neu.cs6240.a3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Random;


public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.printf(
                    "You need to pass [model] [predict] [check] files in that order\n");
            return;
        } else {
            args = new String[] {
                "/home/menthas/Classes/MR/datasets/out/a3_t2/part-r-00000",
                "/home/menthas/Classes/MR/datasets/A3/predict.csv",
                "/home/menthas/Classes/MR/datasets/A3/check.csv"
            };
        }
        // reading from model and creating hash.
        BufferedReader br = new BufferedReader(new FileReader(args[0]));

        String sCurrentLine;
        String recordSplitArray[];
        Hashtable<String, Float> keyDelay = new Hashtable<String, Float>();
        while ((sCurrentLine = br.readLine()) != null) {
            recordSplitArray = sCurrentLine.split("\t");
            keyDelay.put(recordSplitArray[0],
                    Float.parseFloat(recordSplitArray[1]));
        }
        br.close();

        // Predicting delays using hash
        BufferedReader brPredict = new BufferedReader(new FileReader(args[1]));
        String sCurrentLinePredict;
        BufferedReader brCheck = new BufferedReader(new FileReader(args[2]));
        TravelData checkData = new TravelData();
        TravelData predictData = new TravelData();

        int count = 0;
        int correctPrediction = 0;
        int pred;
        String predictKey = predictData.concatenateKeys();
        while ((sCurrentLinePredict = brPredict.readLine()) != null) {
            checkData.setParams(brCheck.readLine());
            predictData.setParams(sCurrentLinePredict);

//            count++;
//            pred = 0;
//            if(pred == checkData.arrivalDelay)
//                correctPrediction++;

//            if (keyDelay.containsKey(predictKey)) {
//                count++;
//                if ( (keyDelay.get(predictKey) > 90.0 && checkData.arrivalDelay == 1)
//                        || (keyDelay.get(predictKey) <= 90.0 && checkData.arrivalDelay == 0)) {
//                    correctPrediction++;
//                }
//            }
        }
        brPredict.close();
        brCheck.close();

        System.out.println(correctPrediction);
        System.out.println(count);

        System.out.println( ((float)correctPrediction * 100)/((float)count));
    }
}