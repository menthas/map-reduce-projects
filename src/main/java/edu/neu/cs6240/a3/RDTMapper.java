package edu.neu.cs6240.a3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPoint;
import jsat.classifiers.trees.RandomDecisionTree;
import jsat.classifiers.trees.TreePruner;
import jsat.linear.DenseVector;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * MedianMapper is mapper class of Assignment1 Version4 program.
 * @author Behrooz Afghahi, Sahil Mehta
 */
public class RDTMapper extends
		Mapper<LongWritable, Text, Text, Text> {

    protected int RANDOM_FEATURE_COUNT = 8;
    protected int MAX_TREE_DEPTH = 20;
    protected int BRANCHING_THRESHOLD = 100;
    protected double PRUNING_DATASET_SIZE = 0.2;

    protected static TravelData td = new TravelData(true);
    protected static CategoricalData[] cats = new CategoricalData[] {
            new CategoricalData(2)
    };
    ArrayList<DataPoint> dataPointArrayList = new ArrayList<>();

	/**
     * Read a single line and create the corresponding values.
	 * @param key: is byte offset of each record read from input file.
	 * @param value: is the content of each line as Text.
	 * @param output: is mapper output.
	 */
	@Override
	public void map(LongWritable key, Text value, Context output)
			throws IOException, InterruptedException {
        if (!td.setParams(value.toString(), false)) {
//            output.getCounter("org.apache.hadoop.mapred.Task$Counter", "MAP_SKIPPED_RECORDS")
//                    .increment(1);
            return;
        }
        dataPointArrayList.add(new DataPoint(
                new DenseVector(new double[] {
                        td.distance, td.elapsedTime, td.arrivalTime, td.departureTime,
                        td.airlineID, td.originAirportIdNum, td.destinationAirportIdNum,
                        td.year, td.month, td.day, td.dayOfWeek, td.canceled
                }),
                new int[] {
                        td.ArrivalDelay15()
                },
                cats
        ));
	}

    /**
     * Called when this mapper is about to finish. Emits all the estimated medians
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        ClassificationDataSet dataSet = new ClassificationDataSet(dataPointArrayList, 0);
        dataPointArrayList = null;
        RandomDecisionTree tree = new RandomDecisionTree(
                RANDOM_FEATURE_COUNT, MAX_TREE_DEPTH, BRANCHING_THRESHOLD,
                TreePruner.PruningMethod.REDUCED_ERROR, PRUNING_DATASET_SIZE);
        tree.trainC(dataSet);

        Gson gson = new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .create();
        context.write(new Text("tree"), new Text(gson.toJson(tree)));
        super.cleanup(context);
    }
}
