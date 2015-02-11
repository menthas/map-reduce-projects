package edu.neu.cs6260.a2;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Reducer class that also sorts the values in memory.
 *
 * @author behrooz, sahil
 */
public class SalesMedianReducer
        extends
        Reducer<Text, DoubleWritable, Text, DoubleWritable> {

    /**
     * Outputs the median for each product.
     * @param key product category
     * @param values order doesn't matter.
     * @param context
     * @throws java.io.IOException
     * @throws InterruptedException
     */
    @Override
    public void reduce(Text key, Iterable<DoubleWritable> values,
                       Context context) throws IOException, InterruptedException {
        ArrayList<Double> product_details = new ArrayList<>();
        for (DoubleWritable value : values) {
            product_details.add(value.get());
        }
        Collections.sort(product_details);

        Double median;
        int size = product_details.size();
        if (size % 2 == 0)
            median = (product_details.get((size / 2) - 1) + product_details.get(size / 2)) / 2.0;
        else
            median = product_details.get(size / 2);
        context.write(key, new DoubleWritable(median));
    }
}
