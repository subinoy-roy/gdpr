package com.roy.gdprspring.models;

import com.roy.gdprspring.annotations.Pii;

import java.io.Serializable;

public class User implements Serializable {
    String name;
    @Pii
    String email;

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

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
