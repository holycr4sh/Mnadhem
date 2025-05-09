package com.example.mnadhem;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Locale;
import android.view.View;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.sql.Time;
import java.text.ParseException;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.lifecycle.ViewModelProvider;
import java.util.Date;
import android.widget.AdapterView;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskItemClickListener {

    private RecyclerView taskRecyclerView;
    private TextView emptyTaskTextView;
    private TaskAdapter taskAdapter;
    private TaskViewModel taskViewModel;
    private List<Task> taskList = new ArrayList<>();
    private Spinner sortOptionsSpinner;
    private Spinner filterOptionsSpinner;
    private FloatingActionButton searchTaskFab;

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

        taskRecyclerView = findViewById(R.id.recyclerViewTasks);
        emptyTaskTextView = findViewById(R.id.textViewEmpty);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, taskList, this);
        taskRecyclerView.setAdapter(taskAdapter);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        sortOptionsSpinner = findViewById(R.id.sortSpinner);
        filterOptionsSpinner = findViewById(R.id.filterSpinner);
        searchTaskFab = findViewById(R.id.fabSearch);

        setupSpinners();
        setupSearchFab();
        observeTasks();

        FloatingActionButton addTaskFab = findViewById(R.id.fab);
        addTaskFab.setOnClickListener(view -> showAddTaskDialog());
    }

    private void setupSpinners() {
        sortOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFiltersAndSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        filterOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFiltersAndSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void observeTasks() {
        taskViewModel.getAllTasks().observe(this, taskEntities -> {
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
            taskList.clear();
            taskList.addAll(tasks);
            applyFiltersAndSort();
            updateUIVisibility();
        });
    }

    private void updateUIVisibility() {
        if (taskList.isEmpty()) {
            emptyTaskTextView.setVisibility(View.VISIBLE);
            taskRecyclerView.setVisibility(View.GONE);
        } else {
            emptyTaskTextView.setVisibility(View.GONE);
            taskRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupSearchFab() {
        searchTaskFab.setOnClickListener(v -> showSearchTaskDialog());
    }

    private void applyFiltersAndSort() {
        List<Task> filteredAndSortedTasks = new ArrayList<>(taskList);
        applyFilters(filteredAndSortedTasks);
        applySort(filteredAndSortedTasks);
        taskAdapter.setTaskList(filteredAndSortedTasks);
    }

    private void applyFilters(List<Task> tasks) {
        String filterOption = filterOptionsSpinner.getSelectedItem().toString();
        if ("Completed".equals(filterOption)) {
            tasks.removeIf(task -> !task.isCompleted());
        } else if ("Incomplete".equals(filterOption)) {
            tasks.removeIf(Task::isCompleted);
        }
    }

    private void applySort(List<Task> tasks) {
        String sortOption = sortOptionsSpinner.getSelectedItem().toString();
        if ("Due Date".equals(sortOption)) {
            tasks.sort((task1, task2) -> {
                if (task1.getDueDate() == null && task2.getDueDate() == null) return 0;
                if (task1.getDueDate() == null) return 1;
                if (task2.getDueDate() == null) return -1;
                return task1.getDueDate().compareTo(task2.getDueDate());
            });
        } else if ("Priority".equals(sortOption)) {
            tasks.sort(Comparator.comparing(Task::getPriority));
        }
    }

    private void showAddTaskDialog() {
        showTaskDialog(null, "Add New Task");
    }

    private void showEditTaskDialog(final Task task) {
        showTaskDialog(task, "Edit Task");
    }

    private void showTaskDialog(final Task task, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        final EditText taskNameEditText = dialogView.findViewById(R.id.editTextTaskName);
        final EditText taskDescriptionEditText = dialogView.findViewById(R.id.editTextTaskDescription);
        final Spinner prioritySpinner = dialogView.findViewById(R.id.spinnerPriority);
        final TextView dueDateTextView = dialogView.findViewById(R.id.textViewDueDate);
        final Button pickDateButton = dialogView.findViewById(R.id.buttonPickDate);
        final TextView dueTimeTextView = dialogView.findViewById(R.id.textViewDueTime);
        final Button pickTimeButton = dialogView.findViewById(R.id.buttonPickTime);

        setupPrioritySpinner(prioritySpinner);
        final Calendar selectedCalendar = Calendar.getInstance();

        if (task != null) {
            populateTaskDetails(task, taskNameEditText, taskDescriptionEditText, prioritySpinner,
                    dueDateTextView, dueTimeTextView, selectedCalendar);
        }

        pickDateButton.setOnClickListener(v -> showDatePickerDialog(dueDateTextView, selectedCalendar));
        pickTimeButton.setOnClickListener(v -> showTimePickerDialog(dueTimeTextView, selectedCalendar));

        builder.setTitle(title)
                .setPositiveButton(task == null ? "Add" : "Update", (dialog, which) ->
                        processTaskInput(task, taskNameEditText, taskDescriptionEditText, prioritySpinner,
                                selectedCalendar, dueDateTextView, dueTimeTextView))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }

    private void setupPrioritySpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
                this, R.array.priority_options, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(priorityAdapter);
    }

    private void populateTaskDetails(Task task, EditText nameEditText, EditText descriptionEditText,
                                     Spinner prioritySpinner, TextView dueDateTextView,
                                     TextView dueTimeTextView, Calendar selectedCalendar) {
        nameEditText.setText(task.getName());
        descriptionEditText.setText(task.getDescription());
        prioritySpinner.setSelection(
                ((ArrayAdapter) prioritySpinner.getAdapter()).getPosition(task.getPriority()));

        if (task.getDueDate() != null) {
            selectedCalendar.setTime(task.getDueDate());
            dueDateTextView.setText(formatDate(task.getDueDate()));
        }

        if (task.getDueTime() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(task.getDueTime().getTime()));
            selectedCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            selectedCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            dueTimeTextView.setText(formatTime(selectedCalendar.getTime()));
        }
    }

    private void processTaskInput(Task task, EditText nameEditText, EditText descriptionEditText,
                                  Spinner prioritySpinner, Calendar selectedCalendar,
                                  TextView dueDateTextView, TextView dueTimeTextView) {
        String name = nameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            showToast("Task name cannot be empty");
            return;
        }

        String description = descriptionEditText.getText().toString().trim();
        String priority = prioritySpinner.getSelectedItem().toString();
        Date dueDate = selectedCalendar.getTime();
        Time dueTime = parseTime(dueTimeTextView.getText().toString());

        if (task == null) {
            taskViewModel.insert(new Task(name, description, dueDate, dueTime, priority, false));
        } else {
            task.setName(name);
            task.setDescription(description);
            task.setPriority(priority);
            task.setDueDate(dueDate);
            task.setDueTime(dueTime);
            taskViewModel.update(task);
        }
    }

    private Time parseTime(String timeText) {
        if (timeText.equals("Due Time") || timeText.isEmpty()) {
            return null;
        }
        try {
            return new Time(new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(timeText).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showDatePickerDialog(final TextView dateTextView, final Calendar calendar) {
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    dateTextView.setText(formatDate(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showTimePickerDialog(final TextView timeTextView, final Calendar calendar) {
        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    timeTextView.setText(formatTime(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("MMM dd, yyy", Locale.getDefault()).format(date);
    }

    private String formatTime(Date date) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    }

    private void showSearchTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Task");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Search", (dialog, which) -> {
            String searchText = input.getText().toString().trim();
            searchTask(searchText);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }

    private void searchTask(String searchText) {
        List<Task> searchResults = new ArrayList<>();
        for (Task task : taskList) {
            if (task.getName().toLowerCase().contains(searchText.toLowerCase())) {
                searchResults.add(task);
            }
        }
        taskAdapter.setTaskList(searchResults);
    }

    @Override
    public void onTaskCheckChanged(Task task, boolean isChecked) {
        task.setCompleted(isChecked);
        taskViewModel.update(task);
        applyFiltersAndSort();
    }

    @Override
    public void onTaskItemClick(Task task) {
        showEditTaskDialog(task);
    }

    @Override
    public void onTaskItemLongClick(Task task) {
        showDeleteConfirmationDialog(task);
    }

    private void showDeleteConfirmationDialog(final Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    taskViewModel.delete(convertToTaskEntity(task));
                    applyFiltersAndSort();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private TaskEntity convertToTaskEntity(Task task) {
        return new TaskEntity(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getDueDate(),
                task.getDueTime(),
                task.getPriority(),
                task.isCompleted()
        );
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}