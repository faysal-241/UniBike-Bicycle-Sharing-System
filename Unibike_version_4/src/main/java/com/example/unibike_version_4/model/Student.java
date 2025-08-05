package com.example.unibike_version_4.model;

public class Student extends User {
    public Student(String id, String name, String email, String password, double balance) {
        super(id, name, email, password, balance, true); // <-- Active by default
    }
}

