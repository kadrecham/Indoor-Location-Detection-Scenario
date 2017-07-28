package com.sap.detectLocation;

public class PojoLocationScanResult {
	
private LocationScanResult[] locationScanResult ;
	
	public LocationScanResult[] getLocationScanResult() {
		
	return locationScanResult;
	}
	
	public void setLocationScanResult(LocationScanResult[] locationScanResult) {
		
	this.locationScanResult = locationScanResult;
	}
	
	@Override
	public String toString() {
		String myString  = "{\"locationScanResult\":[";
		for (LocationScanResult lsr:this.locationScanResult)
			myString = myString + lsr.toString() + ",";
		myString = myString.substring(0, myString.length()-1) + "]}";
		return myString;
	}

}
