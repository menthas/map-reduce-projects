package edu.neu.cs6260.a2;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper class to extract the product and price from the input
 * Should be used with a reducer that sorts the keys.
 *
 * @author behrooz.af
 */
public class SalesMedianMapper
        extends Mapper<LongWritable, Text, Text, DoubleWritable> {
    /**
     * Enum used for counters
     */
    enum SalesDataStats {
        MALFORMED
    }

    /**
     * Map of remedian calculations per category
     */
    HashMap<String, Remedian> remedians;

    /**
     * Called when this mapper is initialized. used to init variables.
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        remedians = new HashMap<>();
    }

    /**
     * The map function called for each input row
     * @param key offset in file
     * @param value line in file
     * @param context
     * @throws java.io.IOException
     * @throws InterruptedException
     */
    @Override
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        SaleItem item = readRow(value, context);
        if (item == null)
            return;
        if (!remedians.containsKey(item.getCategory())) {
            remedians.put(item.getCategory(), new Remedian(101));
        }
        remedians.get(item.getCategory()).add(item.getPrice());
    }

    /**
     * Parse a single row of input and return a parsed object
     * @param value single row of input
     * @param context
     * @return object populated with information we need.
     */
    private SaleItem readRow(Text value, Context context) {
        // rows are in the format:
        // col1 col2 col3 product_cat product_price
        String[] split = value.toString().split("\t+");
        if (split.length > 4) {
            try {
                return new SaleItem(split[3], Double.parseDouble(split[4]));
            } catch (NumberFormatException e) {
//                context.getCounter(SalesDataStats.MALFORMED).increment(1);
            }
        } else {
//            context.getCounter(SalesDataStats.MALFORMED).increment(1);
        }
        return null;
    }

    /**
     * Called when this mapper is about to finish. Emits all the estimated medians
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        for (Map.Entry<String, Remedian> entry : remedians.entrySet()) {
            // write a record per category
            context.write(new Text(entry.getKey()),
                          new DoubleWritable(entry.getValue().getRemedian()));
        }
        super.cleanup(context);
    }
}
