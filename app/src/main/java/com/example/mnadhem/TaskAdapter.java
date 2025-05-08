package com.example.mnadhem;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Context context;
    private OnTaskItemClickListener listener;

    public interface OnTaskItemClickListener {
        void onTaskCheckChanged(Task task, boolean isChecked);

        void onTaskItemClick(Task task);

        void onTaskItemLongClick(Task task);
    }

    public TaskAdapter(Context context, List<Task> taskList, OnTaskItemClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = taskList.get(position);
        holder.bind(currentTask); // Use the bind method.
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxCompleted;
        TextView itemNameTextView;
        TextView textViewDueDate;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxCompleted = itemView.findViewById(R.id.checkBox);
            itemNameTextView = itemView.findViewById(R.id.itemName);
            textViewDueDate = itemView.findViewById(R.id.itemDueDate);
        }

        public void bind(final Task task) {
            itemNameTextView.setText(task.getName());
            checkBoxCompleted.setChecked(task.isCompleted());

            if (task.getDueDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyy", Locale.getDefault());
                textViewDueDate.setText("Due: " + dateFormat.format(task.getDueDate()));
                textViewDueDate.setVisibility(View.VISIBLE);
            } else {
                textViewDueDate.setVisibility(View.GONE);
            }

            checkBoxCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (listener != null) {
                        if (task.isCompleted() != isChecked) {
                            listener.onTaskCheckChanged(task, isChecked);
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onTaskItemClick(task);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        listener.onTaskItemLongClick(task);
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
