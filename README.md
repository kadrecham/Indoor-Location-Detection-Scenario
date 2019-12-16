# Indoor-Location-Detection-Scenario
## Indoor location detection using Android mobile device, Kafka, SAP Hana DB and Machine Learning techniques

### Summary:
Getting the indoor location of the user based on measuring the signal strength of the many distributed access points.

![alt tag](https://user-images.githubusercontent.com/25906706/28717451-27dfec80-73a2-11e7-934a-db7435727a73.png)

### Tools:
* [Java 1.8+](https://java.com/en/)
* [Maven 3.0+](https://maven.apache.org/)
* [Wifi-Signal-Strength-Collector Android Mobile Application](https://github.wdf.sap.corp/D069020/Wifi-Signal-Strength-Collector)
* [Apache Kafka](https://kafka.apache.org/) as a streaming platform
* [Spring RESTful Web Service](https://spring.io/guides/gs/rest-service/).
* [Nielsutrecht kafka-serializer-example](https://github.com/nielsutrecht/kafka-serializer-example)
* [SAP Hana 2](https://www.sap.com/product/technology-platform/hana.html)
* [SAP HANA - Java Driver](http://gerardnico.com/wiki/hana/jdbc)
* [WEKA](http://weka.wikispaces.com/) as a machine learning workbench.
* [InfluxDB](https://github.com/influxdata/influxdb) as a time series database.
* [Grafana](https://grafana.com/) as a graphics dashboard.

### Details:
This scenario has two phases:
* Creating the radiation map (for the first time only):  
Every five seconds the mobile device scans the signal strength of all available access points and sends the results and the locations (should select the room number from the mobile application before scanning) as JSON object using http request to the Spring RESTful API which runs Kafka producer.  
Kafka consumer gets the scan results as JSON object too, and inserts them into SAP Hana database into initial database which represents the machine learning training samples (should collect as many samples as possible per room by roaming in the room, let's say more than 300 samples) more samples more accuracy!
* Location detection:  
The mobile device sends the scan results to Kafka through the Spring RESTful every five seconds.  
When the Kafka consumer runs in detection mode, it starts reading the prepared samples from the radiation map from SAP Hana database in order to train the classifier. Then it waiting the simultaneous scan results from mobile devices to make the classification and get the locations of those devices then insert those locations into Influxdb.  
Grafana reads the locations from Influxdb during time and visualizes those locations.

### SAP HANA database settings:
* Install SAP HANA studio from [HERE](https://help.sap.com/viewer/a2a49126a5c546a9864aae22c05c3d0e/2.0.00/en-US)
* Add SAP HANA System, more information from [HERE](https://blogs.sap.com/2012/07/03/how-to-create-a-system-on-sap-hana-studio-and-how-to-create-a-user-on-sap-hana-data-base/)
* Create schema.
```
CREATE SCHEMA ABDUL OWNED BY SYSTEM;
```
* In the created schema create a table to insert the samples into it. 
```
CREATE COLUMN TABLE ABDUL.LOCATION_MAP (LOCATION VARCHAR(20));
```
* After collecting the samples for all rooms, you should divide the previous table into two tables, one for training samples (contains the most samples) and the other for evaluation samples.
```
CREATE COLUMN TABLE ABDUL.LOCATION_TRAIN AS (SELECT  * FROM    ( SELECT  *, ROW_NUMBER() OVER (PARTITION BY LOCATION ) RN FROM ABDUL.LOCATION_MAP ) WHERE   RN <= 250);
CREATE COLUMN TABLE ABDUL.LOCATION_EVALUAT AS (SELECT  * FROM    ( SELECT  *, ROW_NUMBER() OVER (PARTITION BY LOCATION ) RN FROM ABDUL.LOCATION_MAP ) WHERE RN BETWEEN 251 AND 300);
```
* Create the last table, this table stores the simultaneous scan result and contains all access points names which we found them from the first step. The data in this table are deleted directly after finishing the detection. 
```
CREATE COLUMN TABLE ABDUL.LOCATION_NOW AS (SELECT TOP 0 * FROM "ABDUL"."LOCATION_MAP");
```  
Note: We can do all the SAP HANA database settings programmatically, but our aim is getting starting with SAP HANA Studio too.

### Weka settings:
* Download Weka machine learning from [HERE](https://sourceforge.net/projects/weka/)
* Add SAP HANA **jdbcDriver** and **jdbcURL** to *weka.experiment.DatabaseUtils.props* file from the weka jar package. You can do that by using **VIM**: `$ vim weka.jar`, choose the file `weka/experiment/DatabaseUtils.props` then at the top of the file insert this two lines:  
```
jdbcDriver=com.sap.db.jdbc.Driver  
jdbcURL=jdbc:sap://YOUR_SAP_HANA_IP:30015/?autocommit=true
```  
![alt tag](https://user-images.githubusercontent.com/25906706/28717450-260c30c6-73a2-11e7-9528-65874e7ad752.png)

### Maven settings:
* Add the [**modified weka jar package**](https://github.wdf.sap.corp/D069020/Indoor-Location-Detection-Scenario/files/2046/weka.jar.zip) and the [**SAP HANA - Java Driver (ngdbc.jar)**](https://github.wdf.sap.corp/D069020/Indoor-Location-Detection-Scenario/files/2045/ngdbc.jar.zip) to the Maven local repository manually:  
```
$ mvn install:install-file -Dfile=PATH_TO_YOUR_JAR.jar -DgroupId=YOUR_GROUP_ID -DartifactId=YOUR_ARTIFACT_ID -Dversion=YOUR_VERSION -Dpackaging=jar
```
* Add the Maven dependency for the two previous packages to pom.xml file from the Maven local repository as below:  
```
<dependency>  
  <groupId>YOUR_SAP_GROUP_ID</groupId>  
  <artifactId>YOUR_SAP_ARTIFACT_ID</artifactId>  
  <version>YOUR_SAP_VERSION</version>  
</dependency>  
<dependency>  
  <groupId>YOUR_WEKA_GROUP_ID</groupId>  
  <artifactId>YOUR_WEKA_ARTIFACT_ID</artifactId>  
  <version>YOUR_WEKA_VERSION</version>  
</dependency>
```

### Running the Location Detection System:
* Clone the project to your local machine.
* Make the previous SAP HANA database, Weka and Maven settings
* Change the SAP HANA URL, user and password from the *com.sap.detectLocation.Consumer.java* according to your SAP Hana system 
* Create the executable jar file by compiling and installing the Maven Package `$ mvn install`
* Download and install Kafka then run it and create a tobic. To do that check the [Quick start guide](https://kafka.apache.org/quickstart).
* Run the producer to produce the mobile result scan to Kafka
```
java -jar indoorLocationDetection-0.0.1.jar YOUR_TOPIC
```
* Run the consumer in mapping mode to consume the data from Kafka and insert it into radiation map table in SAP Hana database.
```
java -jar indoorLocationDetection-0.0.1.jar YOUR_TOPIC mConsumer
```
* Install the [mobile application](https://github.wdf.sap.corp/D069020/Wifi-Signal-Strength-Collector) on your Android mobile device, enter the server IP (wehre the producer running), turn the Mapping mode one and choose the location (the room's number) then start scanning by moving in the room. Now the application send the measurment to Kafka every 5 secound (You should make scanning for all rooms or location you have). Keep in your mind more samples more accuracy!
* Divide the radiation map table into two tables. (See the SAP HANA database settings [above](https://github.wdf.sap.corp/D069020/Indoor-Location-Detection-Scenario#sap-hana-database-settings).)
* Install and run Influxdb then create database has the following properties (URL: "localhost', port: "8086", user: "root", pass: "root" name: "MLD") or you can change it as you wish but should also in *Consumer.java*, more information about installing influxdb from [HERE](https://docs.influxdata.com/influxdb/v1.2/introduction/installation/#installation)
* Install and run Grafana, then add the previous Influx database as data source, import the json [dashboard configuration](https://github.wdf.sap.corp/D069020/Indoor-Location-Detection-Scenario/files/2047/Location.Detection.json.zip) file to Grafana to get our dashboard which visualizes the locations, or create your own. You should modify the device name in matrics query in Graph panel and in Singlestat panel to IP address for users mobile devices. (The mobile application shows you the mobile IP address at starting). More information about Grafana from [HERE](http://docs.grafana.org/)
* Run the consumer in detection mode to make location detection 
```
java -jar indoorLocationDetection-0.0.1.jar YOUR_TOPIC dConsumer
```
* Run the mobile application and turn the Mapping mode off and start scanning. Now the application sends the measurment without the location (the room's number) which our program is supposed to discover it.
* Congratulation! the system is running and the dashboard visualize the users locations.  


Note: In our example, by default the system can classify three rooms. You can simplly add more rooms, by adding more cases to the switch statement from the *Consumer.java* file. So that the order of the array's elements is the same as the order of scanning rooms. That mean the first scanned room has the first index, the second scanned room has the second index and so on.
