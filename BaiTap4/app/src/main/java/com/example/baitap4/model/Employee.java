package com.example.baitap4.model;

public class Employee {
    private int id;
    private String employee_name;
    private int employee_age;
    private double employee_salary;
    private String profile_image;

    // Constructors
    public Employee() {}

    public Employee(int id, String employee_name, int employee_age, double employee_salary, String profile_image) {
        this.id = id;
        this.employee_name = employee_name;
        this.employee_age = employee_age;
        this.employee_salary = employee_salary;
        this.profile_image = profile_image;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmployee_name() { return employee_name; }
    public void setEmployee_name(String employee_name) { this.employee_name = employee_name; }

    public int getEmployee_age() { return employee_age; }
    public void setEmployee_age(int employee_age) { this.employee_age = employee_age; }

    public double getEmployee_salary() { return employee_salary; }
    public void setEmployee_salary(double employee_salary) { this.employee_salary = employee_salary; }

    public String getProfile_image() { return profile_image; }
    public void setProfile_image(String profile_image) { this.profile_image = profile_image; }
}
