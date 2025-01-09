# Homework 0

[TOC]

## Statement
I declare that the assignment submitted on Elearning system is original except for source material explicitly acknowledged, and that the same or related material has not been previously submitted for another course. I also acknowledge that I am aware of University policy and regulations on honesty in academic work, and of the disciplinary guidelines and procedures applicable to breaches of such policy and regulations, as contained in the website [https://www.cuhk.edu.hk/policy/academichonesty/](https://www.cuhk.edu.hk/policy/academichonesty/).

<div align=center style="font-size: 18px;">
    Name<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>YourName</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>SID<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>1155xxxxxx</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><br/>Date<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>05/09/2024</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>Signature <u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>
</div>


<div style="page-break-after: always;"></div>
## Q0 [10 marks]: Secure Virtual Machines Setup on the Cloud

Select the VM's type & disk size on the Google Cloud and set up the machines.

<div align=center>
    <img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409051700540.png" width=50%><br>
    <img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409051701566.png" width=50%>
</div>

Set root's password by

```bash
sudo passwd root
```

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409052008839.png)

Open the local PC's cmd to generate the key by

```bash
ssh-keygen -C 5709master
```

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409062000065.png)

Check the key via terminal by

```bash
cat .\id_rsa.pub
```

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409062002976.png)

Link the key to the instance in Google Cloud

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409062013188.png)

Also, there lies a private key named "id_rsa" as shown in screenshot, which can be used in 3rd-parties SSH tools to access the server.

<div align=center>
    <img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409062023657.png" width=20%><img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409062027448.png" width=80%>
</div>

### a. [10 marks] Secure Virtual Machine Setup 

Updating the firewall that only my pc and pc from CUHK network can log in.

![](https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409051907741.png)

## Q1 [90 marks + 20 bonus marks]: Hadoop Cluster Setup

### a. [20 marks] Single-node Hadoop Setup 

Install the required env and check by<a id="JavaInstall"></a>

```bash
sudo apt install openjdk-8-jdk -y
java -version
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409070020298.png"/>

Get Hadoop package by

```bash
wget https://archive.apache.org/dist/hadoop/common/hadoop-2.9.2/hadoop-2.9.2.tar.gz
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409062120560.png"/>

Extracting the Content and move it to "/usr/local/hadoop"

```bash
tar -xzvf hadoop-2.9.2.tar.gz
sudo mv hadoop-2.9.2 /usr/local/hadoop
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409062122655.png"/>

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409062132726.png"/>

Finding the Java Path by

```bash
readlink -f /usr/bin/java | sed "s:bin/java::"
```
Get the Java Path as

```bash
/usr/lib/jvm/java-8-openjdk-amd64/jre/
```

Editing Hadoop environment Config by

```bash
sudo nano /usr/local/hadoop/etc/hadoop/hadoop-env.sh
```

Replace the JAVA_HOME setting

```sh
export JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409070024214.png"/>

Check the Hadoop installation by

```bash
/usr/local/hadoop/bin/hadoop version
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409062156236.png"/>

Create folders for namenode and datanode by

```bash
mkdir hadoop_data
cd hadoop_data
mkdir name
mkdir data
cd ..
```

Enter the dir of hadoop and edit the .xml files respectively by

```bash
cd /usr/local/hadoop
nano ./etc/hadoop/core-site.xml
nano ./etc/hadoop/hdfs-site.xml
```

```xml
<!-- core-site.xml -->
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
```

```xml
<!-- hdfs-site.xml -->
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    
    <!-- 
		Datanode data is stored by default in /tmp/hadoop-5709master/dfs/data.
    	Hadoop was unable to completely empty this default directory, making it impossible to create it a second time.
    	Unless manually delete all data in default directory after each run. 
	-->
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>~/hadoop_data/name</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>~/hadoop_data/data</value>
    </property>
</configuration>
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409070228855.png"/>

Let go Port 50070
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409062201348.png"/>

#### i. Set up a single-node Hadoop cluster

Format the filesystem by
```bash
bin/hdfs namenode -format
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409062317162.png"/>

Start NameNode daemon and DataNode daemon:
```bash
sbin/start-dfs.sh
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409070127464.png"/>

Check the WebView
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409070128992.png"/>

#### ii. Run the Terasort  example

Make the HDFS directories required to execute MapReduce jobs

```bash
bin/hdfs dfs -mkdir /user
bin/hdfs dfs -mkdir /user/5709master
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409070026385.png"/>

Generate the data and put them in "terasort/input" by

```bash
bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.9.2.jar teragen 120000 terasort/input
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409070129713.png"/>

Sort the data from "terasort/input" and put them in "terasort/output" by

```bash
bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.9.2.jar terasort terasort/input terasort/output
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409070130276.png"/>

Use the sorted data from "terasort/output" to calculate the checksum to check if it is sorted, then store the checksum in "terasort/check" by

```bash
bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.9.2.jar teravalidate terasort/output terasort/check 
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409070131622.png"/>

Output the checksum by

```bash
bin/hdfs dfs -cat terasort/check/*
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409070158687.png"/>

End the program by

```bash
sbin/stop-dfs.sh
```

### b. [40 marks] Multi-node Hadoop Cluster Setup
#### i.Install and set up a multi-node Hadoop cluster with 4 VMs (1 Master and 3 
Slaves).

Create the instances by the exist instance‘s image

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409080113825.png"/>

Back to master VM, change the hostname to "hadoop-namenode" by

```bash
sudo hostnamectl set-hostname hadoop-namenode
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409082233786.png"/>

Exit and reopen to see the change in name

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409082235273.png"/>

Get the VM's ip and name by

```bash
hostname -i
hostname -f
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409082241912.png"/>

Get the just created instances' ip and name

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409082256814.png"/>

Thus we know

| IP Address | Hostname                                                     |
| ---------- | ------------------------------------------------------------ |
| 10.170.0.2 | hadoop-namenode                                              |
| 10.170.0.3 | hadoop-datanode1.asia-east2-c.c.vivid-alchemy-434705-a4.internal |
| 10.170.0.5 | hadoop-datanode2.asia-east2-c.c.vivid-alchemy-434705-a4.internal |
| 10.170.0.4 | hadoop-datanode3.asia-east2-c.c.vivid-alchemy-434705-a4.internal |

Enter the hosts by

```bash
sudo vim /etc/hosts
```

Paste the text below in every VM.

```
10.170.0.2 hadoop-namenode
10.170.0.3 hadoop-datanode1.asia-east2-c.c.vivid-alchemy-434705-a4.internal
10.170.0.5 hadoop-datanode2.asia-east2-c.c.vivid-alchemy-434705-a4.internal
10.170.0.4 hadoop-datanode3.asia-east2-c.c.vivid-alchemy-434705-a4.internal
```

In the namenode VM, generate the ssh key without password by

```bash
ssh-keygen -t rsa -P ""
```

Saves to

```bash
/home/5709master/.ssh/id_master
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409082344847.png"/>

Then transport the key generated to datanode VMs by

```bash
ssh-copy-id -i /home/5709master/.ssh/id_master.pub 5709master@hadoop-datanode1
yes
ssh-copy-id -i /home/5709master/.ssh/id_master.pub 5709master@hadoop-datanode2
yes
ssh-copy-id -i /home/5709master/.ssh/id_master.pub 5709master@hadoop-datanode3
yes
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409082351216.png"/>

Give the namenode VM Read and Write permission and Try to connect the datanode to see if can log without password by

```bash
chmod 0600 ~/.ssh/authorized_keys
ssh hadoop-datanode1
exit
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409082357788.png"/>

Create folder for secondarynamenode and temp by

```bash
cd hadoop_data
mkdir secondaryname
mkdir temp
cd ..
```

Enter the dir of hadoop and edit the .xml files respectively by

```bash
cd /usr/local/hadoop
nano ./etc/hadoop/core-site.xml
nano ./etc/hadoop/hdfs-site.xml
nano ./etc/hadoop/mapred-site.xml
nano ./etc/hadoop/yarn-site.xml
```

```xml
<!-- core-site.xml -->
<configuration>
    <property>
        <name>fs.default.name</name>
        <value>hdfs://hadoop-namenode:54310</value>
    </property>
    <property>
        <name>hadoop.temp.dir</name>
        <value>~/hadoop_data/temp</value>
    </property>
</configuration>
```

```xml
<!-- hdfs-site.xml -->
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>4</value>
    </property>
    
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>~/hadoop_data/name</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>~/hadoop_data/data</value>
    </property>
    <property>
        <name>dfs.datanode.checkpoint.dir</name>
        <value>~/hadoop_data/secondaryname</value>
    </property>
    <property>
        <name>dfs.datanode.checkpoint.period</name>
        <value>3600</value>
    </property>
</configuration>
```

```xml
<!-- mapred-site.xml -->
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    <property>
        <name>mapred.job.tracker</name>
        <value>hadoop-namenode:54311</value>
    </property>
</configuration>
```

```xml
<!-- yarn-site.xml -->
<configuration>
    <!-- Site specific YARN configuration properties-->
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>hadoop-namenode</value>
    </property>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.nodemanager.aux-services,mapreduce.shuffle.class</name>
        <value>org.apache.hadoop.mapred.ShuffleHandler</value>
    </property>
</configuration>
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409090045329.png"/>

Copy those 4 .xml files to local machine and push them to every datanode VMs.

<div align=center>
    <img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409090220666.png" width=45%/>
    <img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409090221933.png" width=45%/>
</div>

Backing to the hadoop dir and adding the datanodes to slave by

```bash
cd /usr/local/hadoop
nano etc/hadoop/slaves
```

Paste

```
#localhost
hadoop-namenode
hadoop-datanode1
hadoop-datanode2
hadoop-datanode3
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409090111144.png"/>

Reformat and start dfs to see if namenode can run by

```bash
bin/hdfs namenode -format
sbin/start-dfs.sh
jps
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409090224880.png"/>

Start yarn and check by

```bash
sbin/start-yarn.sh
jps
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409090224717.png"/>

Checking the DataNode & Nodemanager running status in slave VM by

```bash
ssh hadoop-datanode1
jps
exit
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409090226529.png"/>


Check the WebUI to view.
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409090228315.png"/>

#### ii.Terasort program
Calculate the datasets' size by rules.
```Java
public class Main {
    public static void main(String[] args) {
        int student_ID = 1155218707;
        // Size of dataset 1: (Your student ID % 3 + 1) GB
        int size_1 = student_ID % 3 +1;
        // Size of dataset 2: (Your student ID % 20 + 10) GB
        int size_2 = student_ID % 20 +10;
        System.out.println("size_1 = " + size_1); // size_1 = 2
        System.out.println("size_2 = " + size_2); // size_2 = 17
    }
}
```
Create space for program
```bash
bin/hdfs dfs -mkdir /user
bin/hdfs dfs -mkdir /user/5709master
```
For teragen, every output line's size equals 100 byte, thus lines/GB = 10^9/100 = 10^7.
Thus input of dataset1 is 20,000,000, input of dataset2 is 170,000,000.
```bash
bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.9.2.jar teragen 20000000 terasort/dataset1

bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.9.2.jar teragen 170000000 terasort/dataset2
```

Then use these two dataset to terasort and record the time consumed.
```bash
bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.9.2.jar terasort terasort/dataset1 terasort/output1

bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.9.2.jar terasort terasort/dataset2 terasort/output2
```
| DataSet_Size | Time                                                     |
| :----------: | :-------------------------------------------------------:|
| 2G | <img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409090318488.png"/> |
| 17G | <img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409090331504.png"/> |

Then use these two dataset to teravalidate and record the time consumed.
```bash
bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.9.2.jar teravalidate terasort/output1 terasort/check1 
bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.9.2.jar teravalidate terasort/output2 terasort/check2 
```
| DataSet_Size | Time                                                     |
| :----------: | :-------------------------------------------------------:|
| 2G | <img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409090335261.png"/> |
| 17G | <img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409090340990.png"/> |

Obviously, the change in dataset size has a minor impact on the time consumed by reduce tasks, the main impact is on map tasks.

Clean the data and shutdown the program by command below
```bash
hadoop fs -rm -r terasort
sbin/stop-yarn.sh
sbin/stop-dfs.sh
```
### c. [30 marks] Running Python Code on Hadoop 
Donload the database from [here](https://mobitec.ie.cuhk.edu.hk/ierg4300Fall2023/static_files/shakespeare.zip).

#### i. Download the scripts

Download the scripts from [here](https://www.dropbox.com/s/kdhlzkcajq1g5h1/MapReduce_WordCount.zip?dl=0).

Enter the hadoop dir and create dataset dir and upload the downloaded dataset in via SFTP.
```bash
cd /usr/local/hadoop
mkdir dataset
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409091531537.png"/>
#### ii. Run the Python wordcount script and record the running time.

For downloaded scripts, mapper.py & reducer.py are written in Python2, Python compiler in server is python3.
Thus we need to re-write in python3 version.
```python
#!/usr/bin/env python
# mapper.py

import sys

# input comes from STDIN (standard input)
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()
    # split the line into words
    words = line.split()
    # increase counters
    for word in words:
        # write the results to STDOUT (standard output);
        # what we output here will be the input for the
        # Reduce step, i.e. the input for reducer.py
        #
        # tab-delimited; the trivial word count is 1

        # print '%s\t%s' % (word, 1)
        print('{}\t{}'.format(word, 1))
```
```python
#!/usr/bin/env python
# reducer.py

from operator import itemgetter
import sys

current_word = None
current_count = 0
word = None

# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()

    # parse the input we got from mapper.py
    word, count = line.split('\t', 1)

    # convert count (currently a string) to int
    try:
        count = int(count)
    except ValueError:
        # count was not a number, so silently
        # ignore/discard this line
        continue

    # this IF-switch only works because Hadoop sorts map output
    # by key (here: word) before it is passed to the reducer
    if current_word == word:
        current_count += count
    else:
        if current_word:
            # write result to STDOUT
            # print '%s\t%s' % (current_word, current_count)

            print('{}\t{}'.format(current_word, current_count))
        current_count = count
        current_word = word

# do not forget to output the last word if needed!
if current_word == word:
    # print '%s\t%s' % (current_word, current_count)
    print('{}\t{}'.format(current_word, current_count))
```

Create scripts dir and upload the scripts in via SFTP.<a id="Upload"></a>
```bash
mkdir scripts
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409091527020.png"/>

Run the following command to start the program
```bash
sbin/start-dfs.sh
sbin/start-yarn.sh
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409100228644.png"/>

Create the folder for dataset in HDFS and put the dataset in.
```bash
bin/hdfs dfs -mkdir /user
bin/hdfs dfs -mkdir /user/5709master
bin/hdfs dfs -mkdir /user/5709master/dataset
bin/hadoop fs -put /usr/local/hadoop/dataset/shakespeare hdfs://hadoop-namenode:54310/user/5709master/dataset
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409100230969.png"/>

Run the Mapreducer by
```bash
./bin/hadoop jar share/hadoop/tools/lib/hadoop-streaming-2.9.2.jar \
-file ./scripts/mapper.py -mapper "python3 mapper.py" \
-file ./scripts/reducer.py -reducer "python3 reducer.py" \
-input ./dataset/* \
-output output
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409100233339.png"/>

The detailed runing times are
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409100234434.png"/>

### d. [Bonus 20 marks] Compiling the Java WordCount program for MapReduce

Java path should already been set through the install process of java(<a href="#JavaInstall">click here to find the location</a>).Double check by
```bash
echo $JAVA_HOME
echo $PATH
java -version
javac -version
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409100257832.png"/>

Edit .bashrc file
```bash
nano ~/.bashrc
```
Add hadoop's class path in the end
```sh
export CLASSPATH=$(hadoop classpath)
export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar
```
Exit refresh and check if it is successfully added
```bash
source ~/.bashrc
echo $CLASSPATH
echo $HADOOP_CLASSPATH
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409100340115.png"/>

Compile the java file to class(java file has already been uploaded in step <a href="#Upload">here</a>)
```bash
bin/hadoop com.sun.tools.javac.Main ./scripts/WordCount.java
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409100342915.png"/>

Packaging to jar by
```bash
jar cf ./scripts/wc.jar ./scripts/WordCount*.class
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409100344167.png"/>

As write in the java

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409100354479.png"/>

The Fully Qualified Class Name is
```java
org.apache.hadoop.examples.WordCount
```
Thus, we can run the java program by
```bash
bin/hadoop jar ./scripts/wc.jar org.apache.hadoop.examples.WordCount ./dataset/* output_java
```
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409100357124.png"/>
The detailed runing times are
<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202409100358930.png"/>

Compared with python version.
|  Task Type   |  Python |  Java  |
| :----------: | :-----: | :----: |
| Map Tasks    | 16293ms | 3888ms |
| Reduce Tasks | 4149ms  | 3832ms |

Compared to Python, Java has higher efficiency in tasks involving multiple concurrent processes.

Clear the data and end the hadoop by
```bash
hadoop fs -rm -r /user
hadoop fs -rm -r /tmp
sbin/stop-yarn.sh
sbin/stop-dfs.sh
```
## References
<ol>
    <li>https://mobitec.ie.cuhk.edu.hk/iems5709Fall2024/</li>
</ol>
