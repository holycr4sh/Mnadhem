package com.example.mnadhem;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(TaskEntity task);

    @Update
    void update(TaskEntity task);

    @Delete
    void delete(TaskEntity task);

    @Query("SELECT * FROM tasks ORDER BY name ASC")
    LiveData<List<TaskEntity>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE id = :id")
    LiveData<TaskEntity> getTaskById(String id);
}
