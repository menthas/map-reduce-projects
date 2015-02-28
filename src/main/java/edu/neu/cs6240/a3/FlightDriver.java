package edu.neu.cs6240.a3;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * The flight driver job that creates the prediction model to be used for further prediction.
 *
 * @author behrooz.af
 */
public class FlightDriver extends Configured implements Tool {

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
        job.setJobName("Flight model trainer");

        job.setJarByClass(FlightDriver.class);
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(FlightMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setReducerClass(FlightReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FloatWritable.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    /**
     * entry point for this job, using the ToolRunner
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new Configuration(),
                new FlightDriver(), args);
        System.exit(exitCode);
    }
}

