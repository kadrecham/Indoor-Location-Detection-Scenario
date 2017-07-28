package com.sap.detectLocation;

import java.util.Properties;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Enumeration;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.LMT;
import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.InstanceQuery;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class Consumer {
	
	static final String JDBC_DRIVER = "com.sap.db.jdbc.Driver";  
    static final String DB_URL = "jdbc:sap://wdfl30010592a:30015/?autocommit=true";
    static final String USER = "ABDUL";
    static final String PASS = "Abdul123";
    
	
	public Consumer (String topic, boolean flag) throws Exception {
		if (flag == true) {
			String location = "";
		    
		    LocationScanResult jArray [] ;
			Connection conn = null;
		    Statement stmt = null;
		    Class.forName( JDBC_DRIVER );
		    System.out.println("Connecting to database...");
	        conn = DriverManager.getConnection(DB_URL,USER,PASS);
	        System.out.println("Creating statement...");
	        stmt = conn.createStatement();
	        String sql;
	        String column;
	        String value;
		    
			Properties props = new Properties();
			props.put("bootstrap.servers", "localhost:9092");
			props.put("group.id", "test");
			props.put("enable.auto.commit", "true");
			props.put("auto.commit.interval.ms", "1000");
			props.put("session.timeout.ms", "30000");
			props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
			props.put("value.deserializer","com.sap.detectLocation.JacksonSerializer");
			KafkaConsumer<String, PojoLocationScanResult> consumer = new KafkaConsumer<>(props);
			consumer.subscribe(Arrays.asList(topic));
			while (flag) {
				ConsumerRecords<String, PojoLocationScanResult> records = consumer.poll(100);
				for (ConsumerRecord<String, PojoLocationScanResult> record : records) {
					System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
					location = record.key();
					column = "INSERT INTO ABDUL.LOCATION_MAP (LOCATION";
					value = ") VALUES ('" + location + "'" ;
					jArray = record.value().getLocationScanResult();
					for (LocationScanResult ja:jArray) {
                        
						//***SAP_HANA_DB Procedure
						sql = "ALTER TABLE ABDUL.LOCATION_MAP ADD (\"" + ja.getBSSID() + "\" Integer Default 0)";
						try {
							stmt.executeUpdate(sql);
						} catch (SQLException e) {
							System.out.println("The column alrady exists!");
						}
						
						column = column + ", \"" +ja.getBSSID() + "\"";
						value =  value + ", " + ja.getLevel();
						
                    }
					sql =column + value + ")";
					System.out.println(sql);
			    	try {
						stmt.executeUpdate(sql);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			}
			stmt.close();
	        conn.close();
            
			consumer.close();
            
		}
		
		if (flag == false) {
			String location = "";
		    
		    LocationScanResult jArray [] ;
			Connection conn = null;
		    Statement stmt = null;
		    ToInfluxDB toInfluxDB ;
		    Class.forName( JDBC_DRIVER );
		    System.out.println("Connecting to database...");
	        conn = DriverManager.getConnection(DB_URL,USER,PASS);
	        toInfluxDB = new ToInfluxDB("localhost", "8086", "root", "root", "MLD");
	        System.out.println("Creating statement...");
	        stmt = conn.createStatement();
	        String sql;
	        String column;
	        String value;
	        int decision ;
		    double temp ;
		    int count ;
		    
		    InstanceQuery query1 = new InstanceQuery();
	        query1.setUsername(USER);
	        query1.setPassword(PASS);
	        query1.setQuery("SELECT  * FROM  ABDUL.LOCATION_TRAIN");
	        Instances trainData = query1.retrieveInstances();
	        trainData.setClassIndex(0);
	        System.out.println("trainData loaded!");
	        Classifier classifier = new LMT();
	        classifier.buildClassifier(trainData);
	        
	        InstanceQuery query2 = new InstanceQuery();
	        query2.setUsername(USER);
	        query2.setPassword(PASS);
	        query2.setQuery("SELECT  * FROM  ABDUL.LOCATION_EVALUAT");
	        Instances evaluatData = query1.retrieveInstances();
	        evaluatData.setClassIndex(0);
	        System.out.println("evaluatData loaded!");
	        Evaluation eval = new Evaluation(trainData);
	        eval.evaluateModel(classifier, evaluatData);
	        System.out.println(eval.toSummaryString());
	       
		    
			Properties props = new Properties();
			props.put("bootstrap.servers", "localhost:9092");
			props.put("group.id", "test");
			props.put("enable.auto.commit", "true");
			props.put("auto.commit.interval.ms", "1000");
			props.put("session.timeout.ms", "30000");
			props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
			props.put("value.deserializer","com.sap.detectLocation.JacksonSerializer");
			KafkaConsumer<String, PojoLocationScanResult> consumer = new KafkaConsumer<>(props);
			consumer.subscribe(Arrays.asList(topic));
			while (!flag) {
				ConsumerRecords<String, PojoLocationScanResult> records = consumer.poll(100);
				for (ConsumerRecord<String, PojoLocationScanResult> record : records) {
					System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
					location = record.key();
					column = "INSERT INTO ABDUL.LOCATION_NOW (LOCATION";
					value = ") VALUES ('?'" ;
					jArray = record.value().getLocationScanResult();
					for (LocationScanResult ja:jArray) {
                        
						//***SAP_HANA_DB Procedure
						//Should check the AP name
						
						
						column = column + ", \"" +ja.getBSSID() + "\"";
						value =  value + ", " + ja.getLevel();
						
                    }
					sql =column + value + ")";
					System.out.println(sql);
			    	try {
						stmt.executeUpdate(sql);
					} catch (SQLException e) {
						
						System.out.println("There is extra AP!");
					}
			    	
			    	InstanceQuery query3 = new InstanceQuery();
			        query3.setUsername(USER);
			        query3.setPassword(PASS);
			        query3.setQuery("SELECT * FROM ABDUL.LOCATION_NOW");
			        Instances testData = query3.retrieveInstances();
			        System.out.println("testData is loaded");
			        for (Enumeration<Instance> en = testData.enumerateInstances(); en.hasMoreElements();) {
			        	decision = 0;
			    	    temp = 0;
			    	    count = 0;
			        	double[] results = classifier.distributionForInstance(en.nextElement());
			            for (double result : results) {
			            	
			            	if (result > temp) {
			            		temp = result;
			            		decision = count;
			            	}
			            	count ++;
			                System.out.print(result + " ");
			             }
			             System.out.println();
			             switch (decision) {
			             case 0:
			            	 System.out.println ("You are in the 'WDF03, I3.10'");
			            	 toInfluxDB.dataWrite("detected_locations", location, 10);
			            	 break;
			             case 1:
			            	 System.out.println ("You are in the 'WDF03, I3.09'");
			            	 toInfluxDB.dataWrite("detected_locations", location, 9);
			            	 break;
			             case 2:
			            	 System.out.println ("You are in the 'WDF03, I3.07'");
			            	 toInfluxDB.dataWrite("detected_locations", location, 7);
			            	 break;
			             }
			             
			        };
			        try {
						stmt.executeUpdate("DELETE FROM ABDUL.LOCATION_NOW;");
					} catch (SQLException e) {
						
						System.out.println("Dosn't DELETE!");
					}
					
				}
			}
			stmt.close();
	        conn.close();
	        
			
	        
	        
	        
	        
			
			consumer.close();
		}

	}
	
}
