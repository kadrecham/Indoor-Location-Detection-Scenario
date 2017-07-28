package com.sap.detectLocation;


import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	
	public static String myTopic = "";

    public static void main(String[] args) throws Exception {
    	
    	switch (args.length) {
    		case 0: 
    			System.out.println("Please Enter a Topic");
    			System.exit(0);
    			break;
    		
    		case 1: 
    			myTopic = args[0];
    			SpringApplication.run(Application.class, args);
    			break;
    		
    		default: 
    			myTopic = args[0];
    			switch (args[1]) {
    				case "mConsumer":
    					Consumer jConsumer = new Consumer (myTopic,true);
    					break;
    				case "dConsumer":
    					Consumer sConsumer = new Consumer (myTopic,false);
    					break;
    				default:
    					SpringApplication.run(Application.class, args);
    					break;
    	    		}
    			break;
    	}
    }
}
