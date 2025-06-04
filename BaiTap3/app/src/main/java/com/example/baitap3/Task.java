package com.example.baitap3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Task implements Serializable {
    private String id;
    private String title;
    private String description;
    private Date deadline;
    private Date createdTime;
    private String creator;
    private String assignee; // Giữ lại để backward compatibility
    private List<Contact> assignees; // Danh sách nhiều người thực hiện
    private boolean isCompleted;

    public Task() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.createdTime = new Date();
        this.isCompleted = false;
        this.assignees = new ArrayList<>();
    }

    public Task(String title, String description, Date deadline, String creator, String assignee) {
        this();
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.creator = creator;
        this.assignee = assignee;
    }

    public Task(String title, String description, Date deadline, String creator, List<Contact> assignees) {
        this();
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.creator = creator;
        this.assignees = assignees != null ? assignees : new ArrayList<>();
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

    public List<Contact> getAssignees() {
        if (assignees == null) {
            assignees = new ArrayList<>();
        }
        return assignees;
    }

    public void setAssignees(List<Contact> assignees) {
        this.assignees = assignees != null ? assignees : new ArrayList<>();
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // Utility methods
    public String getAssigneesAsString() {
        if (assignees == null || assignees.isEmpty()) {
            return assignee != null ? assignee : "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < assignees.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(assignees.get(i).getName());
        }
        return sb.toString();
    }

    public void addAssignee(Contact contact) {
        if (assignees == null) {
            assignees = new ArrayList<>();
        }
        if (!assignees.contains(contact)) {
            assignees.add(contact);
        }
    }

    public void removeAssignee(Contact contact) {
        if (assignees != null) {
            assignees.remove(contact);
        }
    }
}