package com.example.lukas.inclass10;

//Lukas Newman

public class User {
    String FName, LName, Email;

    @Override
    public String toString() {
        return "User{" +
                "FName='" + FName + '\'' +
                ", LName='" + LName + '\'' +
                ", Email='" + Email + '\'' +
                '}';
    }

    public User(String FName, String LName, String email) {
        this.FName = FName;
        this.LName = LName;
        Email = email;
    }

    public User() {

    }
}
