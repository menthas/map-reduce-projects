# MapReduce - Assignment 3
This project was written using Java version 1.7 and was tested using that version.
Any lower version will fail to compile the source code.

This project includes an easy to use bash script. Here are some examples:

### Learn
$ ./a3.sh -learn [path/to/dataset]

### predict
$ ./a3.sh -predict [path/to/model.m] [path/to/predict/dataset]

### check
$ ./a3.sh -check [path/to/predicted/file] [path/to/check/dataset]

## changing the model
$ ./a3.sh -learn -algo random_forest ...

## changing the hadoop executable path
$ ./a3.sh -learn -hadoop_command [path/to/hadoop] ...
