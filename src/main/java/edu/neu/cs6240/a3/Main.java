package edu.neu.cs6240.a3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.DataPoint;
import jsat.classifiers.trees.RandomDecisionTree;
import jsat.linear.DenseVector;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

/**
 * Main class that handles are different commands and components.
 *
 * @author Behrooz Afghahi, Sahil Mehta
 */
public class Main {

    protected static CategoricalData[] cats = new CategoricalData[] {
            new CategoricalData(2)
    };

    public static void main(String[] args) throws IOException {
        CommandLine cli = createParser(args, false);
        if (cli == null)
            return;

        if (cli.hasOption("learn")) {
            learn(cli);
        } else if (cli.hasOption("predict")) {
            if (cli.getArgs().length != 2) {
                System.out.println("-predict needs two arguments: [model.m] [predict.csv]");
            }
            String algo = cli.hasOption("algo") ? cli.getOptionValue("algo") : "probability";
            if (algo.equals("probability")) {
                predictProbability(cli);
            } else if (algo.equals("random_forest")) {
                predictRandomForest(cli);
            }
        } else if (cli.hasOption("check")) {
            check(cli);
        } else {
            createParser(args, true);
        }
    }

    public static CommandLine createParser(String[] args, boolean show_options) {
        CommandLineParser parser = new BasicParser();
        Options options = new Options();
        options.addOption("learn", false, "Run the learning phase")
                .addOption("predict", false, "Run prediction on model")
                .addOption("check", false, "Check predicted results")
                .addOption(OptionBuilder.withArgName("algorithm")
                        .hasArg()
                        .withDescription("Algorithm to use for this phase")
                        .create("algo"))
                .addOption(OptionBuilder.withArgName("command")
                        .hasArg()
                        .withDescription("The Command to use to run hadoop jobs")
                        .create("hadoop_command"));

        if (show_options) {
            (new HelpFormatter()).printHelp("a3", options, true);
            return null;
        }

        try {
            return parser.parse( options, args );
        }
        catch( ParseException exp ) {
            (new HelpFormatter()).printHelp("a3", options, true);
        }
        return null;
    }

    protected static void learn(CommandLine cli) {
        if (cli.getArgs().length == 0) {
            System.out.println("-learn needs a dataset file as input");
            return;
        }
        String cmd = cli.hasOption("hadoop_command") ?
                cli.getOptionValue("hadoop_command") : "hadoop";
        String algo = cli.hasOption("algo") ? cli.getOptionValue("algo") : "probability";
        String jar_path;
        try {
            jar_path = Main.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            jar_path = "map-reduce-projects-all-1.0.jar";
        }
        try {
            cmd += " jar " + jar_path +
                    " edu.neu.cs6240.a3.FlightDriver -Dalgo=" + algo + " " + cli.getArgs()[0] +
                    " ./mr.out";
            System.out.println("Running: " + cmd);
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
        } catch (IOException e) {
            System.out.println("Failed to run command:\n" + e);
        } catch (InterruptedException e) {
            System.out.println("Training phase interrupted:\n" + e);
        }

        File model = new File("./mr.out/part-r-00000");
        if (model.exists() && model.renameTo(new File("./model.m"))) {
            System.out.println("New model file generated and saved as `model.m`");
        } else {
            System.out.println("Failed to generate new model file. please check Hadoop logs");
        }

        try {
            FileUtils.deleteDirectory(new File("./mr.out"));
        } catch (IOException e) {
            System.out.println("Failed to delete MapReduce output directory. please remove" +
                " the ./mr.out directory manually.");
        }
    }

    protected static void predictRandomForest(CommandLine cli) throws IOException{
        String[] args = cli.getArgs();
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

        PrintWriter writer = new PrintWriter("./.tmp_predict", "UTF-8");
        br = new BufferedReader(new FileReader(args[1]));
        TravelData td = new TravelData(true);
        while ((sCurrentLine = br.readLine()) != null) {
            if (!td.setParams(sCurrentLine, true)) {
                writer.println(sCurrentLine);
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
            int prediction = 0;
            for (RandomDecisionTree tree : forest) {
                prediction += tree.classify(dataPoint).getProb(1) > 0.25 ? 1 : 0;
            }
            String[] parts = sCurrentLine.split(",");
            parts[46] = prediction > forest.size() / 2.0 ? "1" : "0";
            writer.println(strJoin(parts, ","));
        }
        br.close();
        writer.close();

        FileUtils.forceDelete(new File(args[1]));
        (new File("./.tmp_predict")).renameTo(new File(args[1]));
        System.out.println("File `" + args[1] + "` was updated with predictions");
    }

    protected static void predictProbability(CommandLine cli) throws IOException {
        String[] args = cli.getArgs();

        // reading from model and creating hash.
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String sCurrentLine;
        String recordSplitArray[];
        Hashtable<String, Float> keyDelay = new Hashtable<>();
        while ((sCurrentLine = br.readLine()) != null) {
            recordSplitArray = sCurrentLine.split("\t");
            keyDelay.put(recordSplitArray[0],
                    Float.parseFloat(recordSplitArray[1]));
        }
        br.close();

        // Predicting delays using hash
        BufferedReader brPredict = new BufferedReader(new FileReader(args[1]));
        String sCurrentLinePredict;
        TravelData predictData = new TravelData(false);
        PrintWriter writer = new PrintWriter("./.tmp_predict", "UTF-8");

        while ((sCurrentLinePredict = brPredict.readLine()) != null) {
            if (!predictData.setParams(sCurrentLinePredict, true)) {
                writer.println(sCurrentLinePredict);
                continue;
            }
            String predictKey = predictData.concatenateKeys();
            int pred = 0;
            if (keyDelay.containsKey(predictKey)) {
                pred = keyDelay.get(predictKey) >= 43.0 ? 1 : 0;
            }

            String[] parts = sCurrentLinePredict.split(",");
            parts[46] = Integer.toString(pred);
            writer.println(strJoin(parts, ","));
        }
        brPredict.close();
        writer.close();

        FileUtils.forceDelete(new File(args[1]));
        (new File("./.tmp_predict")).renameTo(new File(args[1]));
        System.out.println("File `" + args[1] + "` was updated with predictions");
    }

    public static void check(CommandLine cli) throws IOException{
        String[] args = cli.getArgs();
        if (args.length != 2) {
            System.out.println("-check needs two arguments: [predict.csv] [check.csv]");
        }

        BufferedReader brPredict = new BufferedReader(new FileReader(args[0]));
        BufferedReader brCheck = new BufferedReader(new FileReader(args[1]));
        String predictRecord;
        String[] predictSplit, checkSplit;

        int correct = 0;
        int incorrect = 0;
        while ((predictRecord = brPredict.readLine()) != null) {
            checkSplit = brCheck.readLine().split(",");
            predictSplit = predictRecord.split(",");
            if (checkSplit[46].equals(predictSplit[46]))
                correct++;
            else
                incorrect++;
        }

        System.out.println("Correct: "+correct+" - Incorrect:"+incorrect+" - Acc%:"+
                (((float) correct / (correct+incorrect)) * 100));
    }

    public static String strJoin(String[] aArr, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = aArr.length; i < il; i++) {
            if (i > 0)
                sbStr.append(sSep);
            sbStr.append(aArr[i]);
        }
        return sbStr.toString();
    }
}