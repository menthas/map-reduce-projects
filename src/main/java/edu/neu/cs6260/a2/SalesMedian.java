package edu.neu.cs6260.a2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * The SalesMedian job class. This class handles the following types of map_reduce jobs:
 *   1. Median using Hadoop's secondary sort (pass -D sorting=secondary)
 *   2. Median, sorting values in the reducer (pass -D sorting=in_reduce)
 *   3. Median with extra CPU load in mapper (pass -D mapper.delay=[load_amount])
 *
 * @author behrooz, sahil
 */
public class SalesMedian extends Configured implements Tool {

    /**
     * Setup and run the job
     * @param args
     * @return 0 on success, 1 on failure
     * @throws Exception
     */
    @Override
    public int run(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.printf(
                "At least two parameters are required for SalesMedian <input dir> <output dir>\n");
            return -1;
        }

        Job job = new Job(getConf());
        job.setJobName("Sales Approximate Median");

        job.setJarByClass(SalesMedian.class);
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(SalesMedianMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);

        job.setReducerClass(SalesMedianReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    /**
     * entry point for this job, using the ToolRunner
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new Configuration(),
                new SalesMedian(), args);
        System.exit(exitCode);
    }
}
