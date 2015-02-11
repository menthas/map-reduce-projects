import subprocess
import os
from time import time

experiments = [
    {
        'name': 'v4_single',
        # directory where the JAR file is
        'dir': "/home/menthas/Classes/MR/projects_sahil/map-reduce-projects/build/libs",
        'repeat': 5,
        'command': [
            "~/Classes/MR/hadoop-2.6.0/bin/hadoop",  # pseudo hadoop path
            "jar",
            "map-reduce-projects-1.0.jar",
            "edu.neu.cs6260.a2.SalesMedian",
            "-D bin_size={bin_size}",
            # change these paths to point to the input dataset and output dir
            "~/Classes/MR/datasets/A1/purchases4.txt",
            "~/Classes/MR/datasets/out/full_{experiment_name}_"
            "{experiment_run_num}"
        ]
    }
]

def run_bin_size_experiment(exp, lower_bound=5, upper_bound=500):
    for i in range(lower_bound, upper_bound, 10):
        if 'dir' in exp:
            os.chdir(exp['dir'])
        print("### started running {exp_name}_bin_test load={load}".format(
            exp_name=exp['name'], load=i
        ))
        command = " ".join(exp['command']).format(
            experiment_name=exp['name'] + "_load_test",
            experiment_run_num=i,
            bin_size=i)
        print("### COMMAND: {}".format(command))
        try:
            start = time()
            output = subprocess.check_output(command, shell=True)
            end = time() - start
        except subprocess.CalledProcessError:
            end = time() - start
            output = "ERROR RUNNING COMMAND"

        print("### JOB {exp_name}_load_test executed in {elapsed} secs".format(
            exp_name=exp['name'], elapsed=end
        ))
        print("### JOB output")
        print(output.decode("ascii"))
        print("################################################## END")


if __name__ == "__main__":
    run_bin_size_experiment(experiments[0])
