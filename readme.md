# MapReduce project
This is a Java project that is used for a simple MapReduce Word Count problem.

## Running Hadoop
````shell script
cd /usr/local/hadoop/sbin
rm -rf /tmp/hadoop-student
hdfs namenode -format
./start-all.sh
````

## Prepare data
```shell script
hdfs dfs -mkdir -p /user/student/airline
hdfs dfs -mkdir -p /user/student/shakespeare
hdfs dfs -mkdir /tmp
hdfs dfs -copyFromLocal ~/dev/airline/* /user/student/airline
hdfs dfs -copyFromLocal ~/dev/shakespeare/tragedy /user/student/shakespeare
```
## Word Count application
The Word Count program is from the tutorial on Apache Hadoop website 
`https://hadoop.apache.org/docs/stable/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html`

## Build
```shell script
mvn clean install
```

## Running the MapReduce application
MapReduce on an input text file: `/user/student/shakespeare/tragedy/othello.txt`
and ouput the result to `/tmp/othello` directory. Both items are part of HDFS.
```shell script
hadoop jar target/cisc-525-mapreduce-jar-with-dependencies.jar com.drkiettran.mapreduce.WordCount \
       /user/student/shakespeare/tragedy/othello.txt /tmp/othello
```

The output is stored in a file inside `/tmp/othello` directory.
```shell script
hdfs dfs -ls /tmp/othello
```
Output:
```shell script
Found 2 items
-rw-r--r--   1 student supergroup          0 2019-12-26 05:15 /tmp/othello/_SUCCESS
-rw-r--r--   1 student supergroup      61836 2019-12-26 05:15 /tmp/othello/part-r-00000
```
To show the content on screen:
```shell script
hdfs dfs -cat /tmp/othello/part-r-00000
```

If you want to copy the result into a local file, provide a location of the local file to be copied.
```shell script
hadoop jar target/cisc-525-mapreduce-jar-with-dependencies.jar com.drkiettran.mapreduce.WordCount \
       /user/student/shakespeare/tragedy/othello.txt /tmp/othello /tmp/othello_result.txt
```
The local file that hold the result is located at `/tmp/othello_result.txt`. To show the content on screen,

```shell script
cat /tmp/othello_result.txt
```
Another way to pull down the result stored in HDFS to a local file,
```shell script
hdfs dfs -copyToLocal /tmp/othello/part-r-00000 /tmp/othello_result.txt
```
## Coding explanation
The following section inspects the program and describes its process flow.
### The Main
The Main program provides the following activities:
- Prepare input/output paths for the application.
- Provide appropriate configuration of this application with the following information:
    - Mapper class
    - Reducer class
    - Data types of the output key and value fields
    - The input and output paths for the application.
- If a local path is provided, a copy of the result / ouput is stored there.

### The Mapper
The main code is located in the `map` method. The input for the mapper is a set of key-value pairs. The
key is the index of the input file and the value is a text line at that location. The main activities are;
- split the text line into tokens/words
- for each token/words, write it out using the `context.write` method that has a key-value pair of
the `token/word`and number `1`

### The Reducer
The reduce code is located in the `reduce` method. The input for the reducer is a set of key-value pairs. 
The key is the unique `word` and the value is a list of values `1`. The reducer in this example sums up
all the `1s` and writes out the unique `word` along with the sum or the number of occurrences of that `word`.

## Troubleshooting tools
Hadoop provides access to the console output/error (via `System.out.` and `System.err`) and 
system log (i.e., `slf4j` logging framework) to assist
the developer in troubleshooting and/or diagnosing run time problems.

### Locating the job number for a run of a MR application
Visit this URL `http://localhost:8088/cluster`. Applications are listed in a reverse chronological order. This
is the main page of the ResourceManager component.

- Select your latest application from the top of the list of applications.
- In the Attempt ID section toward the bottom of the status of the application, select your application
- You will notice that there are '3' containers were allocating for this application.
- Go back to the previous screen where it displays the status of your application, choose the URL under
the `Node` column toward the bottom of the screen. This is a HDFS datanode where the jobs were executed to complete
your application. In our example, it is `http://student-VirtualBox:8042`

### Locating the logs (consoles and system) for the application
Visit the DataNode where the jobs were executed for your application, `http://student-VirtualBox:8042`

- On the left panel, click on the dropdown `Tools` and select the `Local logs` option. A direct link would be
`http://student-VirtualBox:8042/logs`.
- Go toward the bottom of the screen and select `Userlogs`. A direct link would be 
`http://student-VirtualBox:8042/logs/userlogs`
- You see a list of applications displayed in a chronological order. Select the last application 
(or your latest). You will see a list of three (3) items, one for the AppMaster, one for the Mapper, 
and one for the Reducer. 
- select the first item (ends with _01). This is a list of all the logs for the `AppMaster`:
```shell script
Directory: /logs/userlogs/application_1577283919116_0003/container_1577283919116_0003_01_000001/
Parent Directory		
directory.info        	2356 bytes 	Dec 26, 2019 5:28:31 AM
launch_container.sh 	5282 bytes 	Dec 26, 2019 5:28:31 AM
prelaunch.err 	           0 bytes 	Dec 26, 2019 5:28:31 AM
prelaunch.out 	         100 bytes 	Dec 26, 2019 5:28:31 AM
stderr 	                2263 bytes 	Dec 26, 2019 5:28:51 AM
stdout 	                   0 bytes 	Dec 26, 2019 5:28:31 AM
syslog 	               54502 bytes 	Dec 26, 2019 5:28:51 AM
```
- select the second item (ends with _02). This is a list of all the logs for the `Mapper`: 
```shell script
Directory: /logs/userlogs/application_1577283919116_0003/container_1577283919116_0003_01_000002/
Parent Directory		
directory.info 	        2007 bytes 	Dec 26, 2019 5:28:37 AM
launch_container.sh 	5261 bytes 	Dec 26, 2019 5:28:37 AM
prelaunch.err 	           0 bytes 	Dec 26, 2019 5:28:37 AM
prelaunch.out 	         100 bytes 	Dec 26, 2019 5:28:37 AM
stderr 	                 541 bytes 	Dec 26, 2019 5:28:37 AM
stdout 	              206793 bytes 	Dec 26, 2019 5:28:40 AM
syslog 	             2023434 bytes 	Dec 26, 2019 5:28:40 AM
```
- select the third item (ends with _03). This is a list of all the logs for the `Reducer`:
```shell script
Directory: /logs/userlogs/application_1577283919116_0003/container_1577283919116_0003_01_000003/
Parent Directory		
directory.info 	        2007 bytes 	Dec 26, 2019 5:28:42 AM
launch_container.sh 	5452 bytes 	Dec 26, 2019 5:28:42 AM
prelaunch.err 	           0 bytes 	Dec 26, 2019 5:28:42 AM
prelaunch.out 	         100 bytes 	Dec 26, 2019 5:28:42 AM
stderr 	                 541 bytes 	Dec 26, 2019 5:28:42 AM
stdout 	              146193 bytes 	Dec 26, 2019 5:28:44 AM
syslog 	              739594 bytes 	Dec 26, 2019 5:28:45 AM
syslog.shuffle     	    2686 bytes 	Dec 26, 2019 5:28:44 AM
```
### Important URLs
The following is a list of importan URLs you should know:
- YARN: `http://localhost:8088`
- NameNode: `http://localhost:9870`
- Job History Server: `http://localhost:19888`. You can start/stop the history server as follows:
```shell script
./mapred --daemon start historyserver
```
To stop:
```shell script
./mapred --daemon stop historyserver
```