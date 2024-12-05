package com.roy.gdprspring.aspects;

/**
 * Class representing a payload for encryption or decryption.
 * This class is used to encapsulate the data that needs to be processed.
 */
public class Payload {
    private String payload;

    /**
     * Constructor to initialize the payload with a given value.
     *
     * @param payload the value to be set as the payload
     */
    public Payload(String payload) {
        this.payload = payload;
    }

    /**
     * Default constructor.
     */
    public Payload() {}

    /**
     * Sets the payload value.
     *
     * @param payload the value to be set as the payload
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }

    /**
     * Gets the payload value.
     *
     * @return the current value of the payload
     */
    public String getPayload() {
        return payload;
    }
}