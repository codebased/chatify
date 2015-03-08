package com.example.layoutdesign.interfaceport;

public enum SmackActionOptions {
	CONNECT(0),
	LOGIN(1);
	
	private final int value;
    private SmackActionOptions(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
