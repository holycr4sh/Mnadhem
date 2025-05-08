package com.example.mnadhem;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID; // Import UUID
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.widget.DatePicker;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.util.Log; // Import Log

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskItemClickListener {
    private TaskAdapter taskAdapter;
    private RecyclerView recyclerViewTasks;
    private TextView textViewEmpty;
    private TaskViewModel taskViewModel;
    private List<Task> taskList = new ArrayList<>(); // Keep a Task list

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
        textViewEmpty = findViewById(R.id.textViewEmpty);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list and the listener
        taskAdapter = new TaskAdapter(this, taskList, this);
        recyclerViewTasks.setAdapter(taskAdapter);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, taskEntities -> {
            // Convert TaskEntity list to Task list
            List<Task> tasks = new ArrayList<>();
            for (TaskEntity taskEntity : taskEntities) {
                tasks.add(new Task(
                        taskEntity.getId(),
                        taskEntity.getName(),
                        taskEntity.getDescription(),
                        taskEntity.getDueDate(),
                        taskEntity.getDueTime(),
                        taskEntity.getPriority(),
                        taskEntity.isCompleted()
                ));
            }
            taskList.clear();  // Clear the old list
            taskList.addAll(tasks); // Add the new tasks
            taskAdapter.setTaskList(taskList); // Update the adapter
            if (tasks.isEmpty()) {
                textViewEmpty.setVisibility(View.VISIBLE);
                recyclerViewTasks.setVisibility(View.GONE);
            } else {
                textViewEmpty.setVisibility(View.GONE);
                recyclerViewTasks.setVisibility(View.VISIBLE);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTaskDialog();
            }
        });
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        final EditText editTextTaskName = dialogView.findViewById(R.id.editTextTaskName);
        final EditText editTextTaskDescription = dialogView.findViewById(R.id.editTextTaskDescription);
        final Spinner spinnerPriority = dialogView.findViewById(R.id.spinnerPriority);
        final TextView textViewDueDate = dialogView.findViewById(R.id.textViewDueDate);
        final Button buttonPickDate = dialogView.findViewById(R.id.buttonPickDate);
        final TextView textViewDueTime = dialogView.findViewById(R.id.textViewDueTime);
        final Button buttonPickTime = dialogView.findViewById(R.id.buttonPickTime);

        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
                this, R.array.priority_options, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        final Calendar selectedCalendar = Calendar.getInstance();
        buttonPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(textViewDueDate, selectedCalendar);
            }
        });

        buttonPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(textViewDueTime, selectedCalendar);
            }
        });

        builder.setTitle("Add New Task")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskName = editTextTaskName.getText().toString().trim();
                        String taskDescription = editTextTaskDescription.getText().toString().trim();
                        String priority = spinnerPriority.getSelectedItem().toString();
                        Date dueDate = selectedCalendar.getTime(); // Use the Calendar's Date
                        Time dueTime = null; // You might need to get this from the time picker
                        if (textViewDueTime.getText() != "Due Time")
                        {
                            try {
                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                dueTime = new Time(timeFormat.parse(textViewDueTime.getText().toString()).getTime());
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }

                        }

                        if (taskName.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Task name cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Create a new Task object
                        Task newTask = new Task(taskName, taskDescription, dueDate, dueTime, priority, false);
                        // Insert the new task
                        taskViewModel.insert(newTask);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDatePickerDialog(final TextView dueDateTextView, final Calendar selectedCalendar) {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedCalendar.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyy", Locale.getDefault());
                        dueDateTextView.setText(dateFormat.format(selectedCalendar.getTime()));
                    }
                },
                year,
                month,
                dayOfMonth
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final TextView dueTimeTextView, final Calendar selectedCalendar) {
        int hourOfDay = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = selectedCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedCalendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        dueTimeTextView.setText(timeFormat.format(selectedCalendar.getTime()));
                    }
                },
                hourOfDay,
                minute,
                true // Set to true for 24-hour format, false for 12-hour
        );
        timePickerDialog.show();
    }

    @Override
    public void onTaskCheckChanged(Task task, boolean isChecked) {
        task.setCompleted(isChecked);
        taskViewModel.update(task);
    }

    @Override
    public void onTaskItemClick(Task task) {
        // Handle task item click (e.g., show details or edit)
        showEditTaskDialog(task);
    }

    @Override
    public void onTaskItemLongClick(Task task) {
        // Show a dialog to confirm deletion
        showDeleteConfirmationDialog(task);
    }

    private void showDeleteConfirmationDialog(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Convert Task to TaskEntity for deletion
                        TaskEntity taskEntity = new TaskEntity(
                                task.getId(),
                                task.getName(),
                                task.getDescription(),
                                task.getDueDate(),
                                task.getDueTime(),
                                task.getPriority(),
                                task.isCompleted()
                        );
                        taskViewModel.delete(taskEntity);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void showEditTaskDialog(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null); // Reuse the add task dialog
        builder.setView(dialogView);

        final EditText editTextTaskName = dialogView.findViewById(R.id.editTextTaskName);
        final EditText editTextTaskDescription = dialogView.findViewById(R.id.editTextTaskDescription);
        final Spinner spinnerPriority = dialogView.findViewById(R.id.spinnerPriority);
        final TextView textViewDueDate = dialogView.findViewById(R.id.textViewDueDate);
        final Button buttonPickDate = dialogView.findViewById(R.id.buttonPickDate);
        final TextView textViewDueTime = dialogView.findViewById(R.id.textViewDueTime);
        final Button buttonPickTime = dialogView.findViewById(R.id.buttonPickTime);

        // Populate the dialog with the task's current data
        editTextTaskName.setText(task.getName());
        editTextTaskDescription.setText(task.getDescription());

        // Set the spinner adapter
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
                this, R.array.priority_options, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        // Set the spinner selection
        String[] priorityOptions = getResources().getStringArray(R.array.priority_options);
        int selectionIndex = -1;
        for (int i = 0; i < priorityOptions.length; i++) {
            if (priorityOptions[i].equals(task.getPriority())) {
                selectionIndex = i;
                break;
            }
        }

        // Set the selection only if a matching priority is found
        if (selectionIndex != -1) {
            spinnerPriority.setSelection(selectionIndex);
        } else {
            // Handle the case where the task's priority doesn't match any option
            // You might want to set a default selection or log an error
            spinnerPriority.setSelection(0); // Set to the first item as a default
            Log.e("EditDialog", "Warning: Task priority '" + task.getPriority() + "' not found in options.");
        }

        //show the date.
        if(task.getDueDate() != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyy", Locale.getDefault());
            textViewDueDate.setText(dateFormat.format(task.getDueDate()));
        }
        else{
            textViewDueDate.setText("Due Date");
        }

        //show the time
        if(task.getDueTime() != null){
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            textViewDueTime.setText(timeFormat.format(task.getDueTime()));
        }
        else{
            textViewDueTime.setText("Due Time");
        }
        final Calendar selectedCalendar = Calendar.getInstance();
        if (task.getDueDate() != null) {
            selectedCalendar.setTime(task.getDueDate());
        }
        buttonPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(textViewDueDate, selectedCalendar);
            }
        });

        buttonPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(textViewDueTime, selectedCalendar);
            }
        });

        builder.setTitle("Edit Task")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskName = editTextTaskName.getText().toString().trim();
                        String taskDescription = editTextTaskDescription.getText().toString().trim();
                        String priority = spinnerPriority.getSelectedItem().toString();
                        Date dueDate = selectedCalendar.getTime();
                        Time dueTime = null;
                        if (textViewDueTime.getText() != "Due Time")
                        {
                            try {
                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                dueTime = new Time(timeFormat.parse(textViewDueTime.getText().toString()).getTime());
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

                        if (taskName.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Task name cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update the task object
                        task.setName(taskName);
                        task.setDescription(taskDescription);
                        task.setDueDate(dueDate);
                        task.setDueTime(dueTime);
                        task.setPriority(priority);

                        // Update thetask in the database
                        taskViewModel.update(task);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}