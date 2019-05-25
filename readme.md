# Notes on Hadoop
I used this website to set up stand-alone hadoop: 
`https://hadoop.apache.org/docs/r3.2.0/hadoop-project-dist/hadoop-common/SingleCluster.html`

After setting up and startup the local dfs, visit this url:
`http://localhost:45003` and this url for namenode: 
`localhost:9870` and this url for yarn: 
`http://localhost:8088` 

*** Note: If you run in to 'lock' problem, may want to delete temporary file in /tmp folder

I used the following information to create user for mapreduce jobs:

```
  cd /usr/local/hadoop
  hdfs namenode -format
  hadoop namenode &
  hadoop datanode &
  sbin/start-yarn.sh

  bin/hdfs dfs -mkdir /user
  bin/hdfs dfs -mkdir /user/student
  bin/hdfs dfs -mkdir /user/student/shakespeare
  cd ~/dev/week_8/java-code/mapreduce
  bin/hdfs dfs -put src/main/resources/tragedy/*.txt shakespeare
  bin/hdfs dfs -ls /user/student/shakespeare

```

Trouble with the run.

updated mapred-site.xml

hdfs dfs -rm input/shellprofile.d

## Running Classic WordCount program
Program is located here: `https://hadoop.apache.org/docs/stable/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html`

Text: Romeo and Juliet located here: `http://shakespeare.mit.edu/romeo_juliet/full.html`

## Build

```bash
mvn clean package
```

## Run

```bash
hadoop jar target/cisc-525-mapreduce-jar-with-dependencies.jar /user/student/shakespeare /user/student/shakespeare/output
```

## Clear output:
```
hadoop dfs -rm output/*
hadoop dfs -rmdir output
```

## Report

```bash
hdfs dfs -cat /user/student/shakespeare/output/part-r-00000
```
