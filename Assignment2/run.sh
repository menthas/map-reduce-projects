#!/usr/bin/env bash

echo "Welcome To Map Reduce Assignment1"
if [ $# != 2 ]
then
	echo "Invalid Usage. Use it in following way:"
	echo "./run.sh input_path output_path"
else
	ant
	hadoop jar Assign1Version2.jar $1 $2
fi

