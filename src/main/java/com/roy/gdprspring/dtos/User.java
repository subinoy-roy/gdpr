package com.roy.gdprspring.dtos;

import com.roy.gdprspring.annotations.MaskedField;
import com.roy.gdprspring.annotations.PiiField;

import java.io.Serializable;

public class User implements Serializable {
    String name;
    @PiiField
    String email;
    @MaskedField
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
