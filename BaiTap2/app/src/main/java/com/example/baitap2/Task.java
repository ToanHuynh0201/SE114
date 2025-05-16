package com.example.baitap2;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    private String id;
    private String title;
    private String description;
    private Date deadline;
    private Date createdTime;
    private String creator;
    private String assignee;
    private boolean isCompleted;

    public Task() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.createdTime = new Date();
        this.isCompleted = false;
    }

    public Task(String title, String description, Date deadline, String creator, String assignee) {
        this();
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.creator = creator;
        this.assignee = assignee;
    }

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
