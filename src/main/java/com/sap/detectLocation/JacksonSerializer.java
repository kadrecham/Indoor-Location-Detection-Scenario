package com.sap.detectLocation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

public class JacksonSerializer implements Closeable, AutoCloseable, Serializer<PojoLocationScanResult>, Deserializer<PojoLocationScanResult> {
	
	private ObjectMapper mapper;
	
	public JacksonSerializer() {
        this(null);
    }
	
	public JacksonSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }
	
	public static JacksonSerializer defaultConfig() {
        return new JacksonSerializer(new ObjectMapper());
    }
	
	public static JacksonSerializer smileConfig() {
        return new JacksonSerializer(new ObjectMapper(new SmileFactory()));
    }
	
	@Override
    public void configure(Map<String, ?> map, boolean b) {
        if(mapper == null) {
            if("true".equals(map.get("value.serializer.jackson.smile"))) {
                mapper = new ObjectMapper(new SmileFactory());
            }
            else {
                mapper = new ObjectMapper();
            }
        }
    }
	
	@Override
    public byte[] serialize(String s, PojoLocationScanResult myPojo) {
        try {
            return mapper.writeValueAsBytes(myPojo);
        }
        catch(JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
	
	@Override
    public PojoLocationScanResult deserialize(String s, byte[] bytes) {
        try {
            return mapper.readValue(bytes, PojoLocationScanResult.class);
        }
        catch(IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
	
	@Override
    public void close() {
        mapper = null;
    }
	
}
