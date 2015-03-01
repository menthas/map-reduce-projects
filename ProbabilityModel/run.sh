#!/usr/bin/env bash

echo "Welcome To Map Reduce Assignment3"

ant
hadoop jar Assignment3.jar $1 $2

cd PredictCheck/src

javac PredictCheck.java

for i in `ls -a $2*`
do
        echo "$i"
        break
done

echo "$3"
echo "$4"

java PredictCheck $i $3 $4


