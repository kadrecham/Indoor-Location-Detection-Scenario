package com.sap.detectLocation;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @RequestMapping(path ="/js", method = RequestMethod.POST, consumes = "application/json")
    public Responce js(@RequestBody PojoLocationScanResult jobject, @RequestParam(value="myKey", defaultValue="null") String myKey)  {
    	Producer producer = new Producer (Application.myTopic, myKey, jobject);
    	System.out.println(jobject);
        return new Responce (100,myKey);
    }
    
    @RequestMapping(path ="/st", method = RequestMethod.POST)
    public Responce st(@RequestBody String jobject, @RequestParam(value="myKey", defaultValue="null") String myKey)  {
    	Producer producer = new Producer (Application.myTopic, myKey, jobject);
    	System.out.println(jobject);
        return new Responce (100,myKey);
    }
}
