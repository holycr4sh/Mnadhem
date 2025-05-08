package com.example.mnadhem;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

public class Task {
    private String id;
    private String name;
    private String description;

    private Date dueDate;

    private Time dueTime;
    private String priority;
    private boolean isCompleted;
    public Task(String name, String description, Date dueDate, Time dueTime, String priority, boolean isCompleted) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.dueDate=dueDate;
        this.dueTime=dueTime;
        this.priority=priority;
        this.isCompleted=isCompleted;
    }
    public Task(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.isCompleted=false;
        this.priority="Low";
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Time getDueTime() {
        return dueTime;
    }

    public void setDueTime(Time dueTime) {
        this.dueTime = dueTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isCompleted=" + isCompleted +
                ", dueDate=" + dueDate +
                ", due time=" + dueTime +
                ", priority=" + priority +
                '}';
    }
}
