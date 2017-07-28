package com.sap.detectLocation;

public class Responce {
	
	private final long id;
    private final String content;

    public Responce(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

}
