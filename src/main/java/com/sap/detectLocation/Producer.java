package com.sap.detectLocation;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Producer {
	
	public Producer (String topic, String id_in, PojoLocationScanResult input){
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "com.sap.detectLocation.JacksonSerializer");		
		KafkaProducer<String, PojoLocationScanResult> producer = new KafkaProducer<>(props);
		ProducerRecord<String, PojoLocationScanResult> record = new ProducerRecord<>(topic, id_in, input);
		producer.send(record);
		producer.close();
	}
	
	public Producer (String topic, String id_in, String input){
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");		
		KafkaProducer<String, String> producer = new KafkaProducer<>(props);
		ProducerRecord<String, String> record = new ProducerRecord<>(topic, id_in, input);
		producer.send(record);
		producer.close();
	}

}
