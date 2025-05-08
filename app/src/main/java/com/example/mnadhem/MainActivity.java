package com.example.mnadhem;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TaskAdapter taskAdapter;
    private RecyclerView recyclerViewTasks;
    private TextView textViewEmpty;
    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewEmpty = findViewById(R.id.textViewEmpty);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, new ArrayList<>(), new TaskAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskCheckChanged(Task task, boolean isChecked) {
                task.setCompleted(isChecked);
                taskViewModel.update(task);
            }

            @Override
            public void onTaskItemClick(Task task) {
                // Implement your click action here (e.g., show details)
                Toast.makeText(MainActivity.this, "Task clicked: " + task.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTaskItemLongClick(Task task) {
                // Implement your long click action here (e.g., delete task)
                showDeleteConfirmationDialog(task);
            }
        });
        recyclerViewTasks.setAdapter(taskAdapter);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, taskEntities -> {
            List<Task> tasks = new ArrayList<>();
            for (TaskEntity entity : taskEntities) {
                tasks.add(new Task(entity.getId(), entity.getName(), entity.getDescription(), entity.getDueDate(), entity.getDueTime(), entity.getPriority(), entity.isCompleted()));
            }
            taskAdapter.setTasks(tasks);
            updateEmptyView();
        });

        showAddTaskDialog(); // Call this here to set up the FAB click listener
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (taskAdapter.getItemCount() == 0) {
            textViewEmpty.setVisibility(View.VISIBLE);
            recyclerViewTasks.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
            recyclerViewTasks.setVisibility(View.VISIBLE);
        }
    }

    private void showAddTaskDialog() {
        FloatingActionButton fab_add = findViewById(R.id.fab);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });
    }

    private void addTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        final EditText task_name = new EditText(this);
        task_name.setInputType(InputType.TYPE_CLASS_TEXT);
        task_name.setHint("Task Title");
        builder.setView(task_name);

        final EditText task_description = new EditText(this);
        task_description.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        task_description.setHint("Description (Optional)");
        builder.setView(task_description);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String taskTitle = task_name.getText().toString().trim();
                String taskDescription = task_description.getText().toString().trim();
                if (!taskTitle.isEmpty()) {
                    TaskEntity newTask = new TaskEntity(taskTitle, taskDescription);
                    taskViewModel.insert(newTask);
                    recyclerViewTasks.scrollToPosition(taskAdapter.getItemCount() - 1);
                } else {
                    Toast.makeText(MainActivity.this, "Task title cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setView(task_description); // Add description EditText to the dialog
        builder.show(); // Display the dialog
    }

    private void showDeleteConfirmationDialog(final Task taskToDelete) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete '" + taskToDelete.getName() + "'?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Convert Task back to TaskEntity for deletion
                        TaskEntity taskEntityToDelete = new TaskEntity(taskToDelete.getId(), taskToDelete.getName(), taskToDelete.getDescription(), taskToDelete.getDueDate(), taskToDelete.getDueTime(), taskToDelete.getPriority(), taskToDelete.isCompleted());
                        taskViewModel.delete(taskEntityToDelete);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}