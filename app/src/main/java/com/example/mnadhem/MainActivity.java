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
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TaskAdapter taskAdapter;
    private List<Task> myTaskList = new ArrayList<>();

    private RecyclerView recyclerViewTasks;

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
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        taskAdapter= new TaskAdapter(this, myTaskList, new TaskAdapter.OnTaskItemClickListener() {
            @Override
            public void onTaskCheckChanged(Task task, boolean isChecked) {

            }

            @Override
            public void onTaskItemClick(Task task) {

            }

            @Override
            public void onTaskItemLongClick(Task task) {

            }
        });
        recyclerViewTasks.setAdapter(taskAdapter);

    }
        private void showAddTaskDialog(){
        FloatingActionButton fab_add=findViewById(R.id.fab);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });}

    private void addTask() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        final EditText task_name=new EditText(this);
        task_name.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(task_name);

        final EditText task_description=new EditText(this);
        task_description.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(task_description);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String taskTitle = task_name.getText().toString().trim();
                if (!taskTitle.isEmpty()) {
                    Task newTask = new Task(taskTitle);

                    //lezemni nsajal el data fi ba3d lblasa

                    myTaskList.add(newTask);
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

        builder.show(); // Display the dialog
    }



    }