package com.example.baitap1;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String password;
    private String date;
    private String description;
    private String id;
    private String department;
    private String image;

    public User(String name, String password, String id, String date, String description, String department, String image) {
        this.date = date;
        this.department = department;
        this.description = description;
        this.id = id;
        this.image = image;
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
