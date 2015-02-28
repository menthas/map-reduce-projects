package edu.neu.cs6240.a3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.DataPoint;
import jsat.classifiers.trees.RandomDecisionTree;
import jsat.linear.DenseVector;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class Main {

    protected static CategoricalData[] cats = new CategoricalData[] {
            new CategoricalData(2)
    };

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.printf("No Args Specified\n");
            args = new String[]{
                    "/home/menthas/Classes/MR/datasets/out/a3_t4/part-r-00000",
                    "/home/menthas/Classes/MR/datasets/A3/predict.csv",
                    "/home/menthas/Classes/MR/datasets/A3/check.csv"
            };
        }

        // TODO: check command and run the appropriate section
        predictProbability(args);

    }

    protected static void predictRandomForest(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String sCurrentLine;
        ArrayList<RandomDecisionTree> forest = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .create();
        while ((sCurrentLine = br.readLine()) != null) {
            forest.add(gson.fromJson(sCurrentLine.split("\t")[1], RandomDecisionTree.class));
        }
        br.close();

        for (double i=0.0; i<= 1; i+=0.05)
            runWithThreshold(forest, args, i);
    }

    protected static void runWithThreshold(ArrayList<RandomDecisionTree> forest, String[] args,
                                          double threshold) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[2]));
        String sCurrentLine;
        TravelData td = new TravelData(true);
        int errors = 0;
        int total = 0;
        int false_positive = 0;
        int false_negative = 0;
        int true_positive = 0;
        int true_negative = 0;
        while ((sCurrentLine = br.readLine()) != null) {
            if (!td.setParams(sCurrentLine, true)) {
                continue;
            }
            DataPoint dataPoint = new DataPoint(
                    new DenseVector(new double[]{
                            td.distance, td.elapsedTime, td.arrivalTime, td.departureTime,
                            td.airlineID, td.originAirportIdNum, td.destinationAirportIdNum,
                            td.year, td.month, td.day, td.dayOfWeek, td.canceled
                    }),
                    new int[]{
                            0 // not even adding the label
                    },
                    cats
            );
            int truth = td.ArrivalDelay15();
            int prediction = 0;
            for (RandomDecisionTree tree : forest) {
                prediction += tree.classify(dataPoint).getProb(1) > threshold ? 1 : 0;
            }
            prediction = prediction > forest.size() / 2.0 ? 1 : 0;

//            double delayed = 0;
//            double not_delayed = 0;
//            for (RandomDecisionTree tree : forest) {
//                delayed += tree.classify(dataPoint).getProb(1);
//                not_delayed += tree.classify(dataPoint).getProb(0);
//            }
//            int prediction = delayed / (not_delayed+not_delayed) > threshold ? 1 : 0;

            if (prediction != truth) {
                errors++;
                if (prediction == 1)
                    false_positive++;
                else
                    false_negative++;
            } else if (prediction == 1) {
                true_positive++;
            } else {
                true_negative++;
            }
            total++;
        }
        br.close();

        System.out.println("Errors: " + errors + " - Total: " + total + " - " + ((float) errors /
                total) + "%" );
        System.out.println("(FP: " + false_positive + " | FN: " + false_negative + ")\n"+
                "(TP: " + true_positive + " | TN: " + true_negative + ")");
    }

    protected static void predictProbability(String[] args) throws IOException {
        // reading from model and creating hash.
//        BufferedReader br = new BufferedReader(new FileReader(args[0]));
//
//        String sCurrentLine;
//        String recordSplitArray[];
//        Hashtable<String, Float> keyDelay = new Hashtable<>();
//        while ((sCurrentLine = br.readLine()) != null) {
//            recordSplitArray = sCurrentLine.split("\t");
//            keyDelay.put(recordSplitArray[0],
//                    Float.parseFloat(recordSplitArray[1]));
//        }
//        br.close();

        // Predicting delays using hash
        BufferedReader brPredict = new BufferedReader(new FileReader(args[1]));
        String sCurrentLinePredict;
        BufferedReader brCheck = new BufferedReader(new FileReader(args[2]));
        TravelData checkData = new TravelData(false);
        TravelData predictData = new TravelData(false);

        int count = 0;
        int correctPrediction = 0;
        int pred;
//        String predictKey = predictData.concatenateKeys();
        while ((sCurrentLinePredict = brPredict.readLine()) != null) {
            checkData.setParams(brCheck.readLine(), true);
            predictData.setParams(sCurrentLinePredict, true);
            count++;
            pred = (new Random()).nextFloat() >= 0.2382 ? 0 : 1;
            if(pred == checkData.arrivalDelay)
                correctPrediction++;

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