package com.roy.gdprspring.models;

import com.roy.gdprspring.annotations.Masked;
import com.roy.gdprspring.annotations.Pii;

import java.io.Serializable;

public class User implements Serializable {
    String name;
    @Pii
    String email;
    @Masked
    String instagramHandle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstagramHandle() {
        return instagramHandle;
    }

    public void setInstagramHandle(String instagramHandle) {
        this.instagramHandle = instagramHandle;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", instagramHandle='" + instagramHandle + '\'' +
                '}';
    }
}
