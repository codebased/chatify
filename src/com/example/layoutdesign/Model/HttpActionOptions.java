package com.example.layoutdesign.Model;

public enum HttpActionOptions {
	AUTHENTICATEPHONE(0),
	REGISTERPHONE(1),
	IDENTIFYPHONE(2),
	ROASTER(3),
	PROFILEIMAGE(4),
	UNREGISTERPHONE(5);
	
	private final int value;
    private HttpActionOptions(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

