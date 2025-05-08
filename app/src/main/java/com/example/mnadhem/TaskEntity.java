package com.example.mnadhem;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import java.util.Date;
import java.sql.Time;

@Entity(tableName = "tasks")
public class TaskEntity {

    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String description;
    private Date dueDate;
    private Time dueTime;
    private String priority;
    private boolean isCompleted;

    public TaskEntity(String id, String name, String description, Date dueDate, Time dueTime, String priority, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.priority = priority;
        this.isCompleted = isCompleted;
    }

    // Example of a secondary constructor that you might use in your code
    @Ignore
    public TaskEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}