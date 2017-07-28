package com.sap.detectLocation;

import com.db.influxdb.Configuration;
import com.db.influxdb.DataWriter;

public class ToInfluxDB {
	
private DataWriter writer;
	
	public ToInfluxDB (String host, String port, String user, String pass, String db_name) throws Exception {
		Configuration configuration = new Configuration(host, port, user, pass, db_name);
		this.writer = new DataWriter(configuration);
	}
	
	public void dataWrite (String my_table, String my_tag, int my_value) throws Exception {
		writer.setTableName(my_table);
		writer.addTag("device", my_tag);
		writer.addField("location", my_value);
		writer.writeData();
	}

}
