package com.sap.detectLocation;

public class LocationScanResult {
	
	private String bssid;
	private String level;
	
	public String getBSSID() {
	return bssid;
	}

	public void setBSSID(String bssid) {
	this.bssid = bssid;
	}

	public String getLevel() {
	return level;
	}

	public void setLevel(String level) {
	this.level = level;
	}
	
	@Override
	public String toString() {
		return "{\"bssid\":\""+ this.bssid + "\",\"level\":\"" + this.level + "\"}";
	}

}
