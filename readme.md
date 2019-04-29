# Notes on Hadoop
I used this website to set up stand-alone hadoop: 
`https://hadoop.apache.org/docs/r3.2.0/hadoop-project-dist/hadoop-common/SingleCluster.html`

After setting up and startup the local dfs, visit this url:
`http://localhost:45003` and this url for namenode: 
`localhost:9870` and this url for yarn: 
`http://localhost:8088` 

I used the following information to create user for mapreduce jobs:

```
  cd /usr/local/hadoop

  bin/hdfs dfs -mkdir /user
  bin/hdfs dfs -mkdir /user/student

```

Trouble with the run.

updated mapred-site.xml

hdfs dfs -rm input/shellprofile.d
