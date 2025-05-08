package com.example.mnadhem;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskViewModel extends AndroidViewModel {
    private TaskDao taskDao;
    private LiveData<List<TaskEntity>> allTasks;
    private ExecutorService executorService;

    public TaskViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<TaskEntity>> getAllTasks() {
        return allTasks;
    }

    // Modified insert to take Task, convert to TaskEntity
    public void insert(Task task) {
        TaskEntity taskEntity = new TaskEntity(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getDueDate(),
                task.getDueTime(),
                task.getPriority(),
                task.isCompleted()
        );
        executorService.execute(() -> taskDao.insert(taskEntity));
    }

    // Modified update to take Task, convert to TaskEntity
    public void update(Task task) {
        TaskEntity taskEntity = new TaskEntity(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getDueDate(),
                task.getDueTime(),
                task.getPriority(),
                task.isCompleted()
        );
        executorService.execute(() -> taskDao.update(taskEntity));
    }

    public void delete(TaskEntity task) {
        executorService.execute(() -> taskDao.delete(task));
    }
}
