# MapReduce - Assignment 1
This project was written using Java version 1.7 and was tested using that version.
Any lower version will fail to compile the source code.
To create the JAR file while handling the dependencies you need to have gradle
installed on your system (see: http://gradle.org/installation)

**Note:** lines starting with `$` are commands.

### Compiling
Unzip, `cd` to `map-reduce-projects` and run maven as follows:
$ cd map-reduce-projects
$ gradle dist build
afterwards locate and `cd` into the directory of the JAR file.
(probably called `build/libs/map-reduce-projects-1.0.jar`).

**Note:** And already compiled version is included in `build/libs/map-reduce-projects-1.0.jar`

### Running the experiments
To run the experiments in a single mode hadoop distribution do:
$ hadoop jar map-reduce-projects-1.0.jar edu.neu.cs6260.a2.SalesMedian [INPUT_DATASET_FILE] [OUTPUT_DIRECTORY]

**Note:** You can also fix the paths in file `run_experiments.py` and run that
file directly to run the tests that produced the numbers in the report.
