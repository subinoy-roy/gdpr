package com.roy.gdprspring.aspects;

public class Payload {
    String payload;

    public Payload(String payload) {
        this.payload = payload;
    }

    public Payload() {}

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
}
