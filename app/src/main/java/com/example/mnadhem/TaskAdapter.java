package com.example.mnadhem;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;
    private OnTaskItemClickListener listener;

    public TaskAdapter(Context context, List<Task> taskList, OnTaskItemClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = taskList.get(position);
        holder.taskNameTextView.setText(currentTask.getName());
        holder.checkBoxCompleted.setChecked(currentTask.isCompleted());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        if (currentTask.getDueDate() != null) {
            holder.dueDateTextView.setText("Due: " + dateFormat.format(currentTask.getDueDate()));
            holder.dueDateTextView.setVisibility(View.VISIBLE);
            if (currentTask.getDueTime() != null) {
                holder.dueTimeTextView.setText(timeFormat.format(currentTask.getDueTime()));
                holder.dueTimeTextView.setVisibility(View.VISIBLE);
            } else {
                holder.dueTimeTextView.setVisibility(View.GONE);
            }
        } else {
            holder.dueDateTextView.setVisibility(View.GONE);
            holder.dueTimeTextView.setVisibility(View.GONE);
        }

        // Apply strikethrough if the task is completed
        if (currentTask.isCompleted()) {
            holder.taskNameTextView.setPaintFlags(holder.taskNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.dueDateTextView.setPaintFlags(holder.dueDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.dueTimeTextView.setPaintFlags(holder.dueTimeTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.taskNameTextView.setPaintFlags(holder.taskNameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.dueDateTextView.setPaintFlags(holder.dueDateTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.dueTimeTextView.setPaintFlags(holder.dueTimeTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onTaskCheckChanged(currentTask, isChecked);
            }
            // Update the strikethrough immediately when the checkbox changes
            if (isChecked) {
                holder.taskNameTextView.setPaintFlags(holder.taskNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.dueDateTextView.setPaintFlags(holder.dueDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.dueTimeTextView.setPaintFlags(holder.dueTimeTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.taskNameTextView.setPaintFlags(holder.taskNameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.dueDateTextView.setPaintFlags(holder.dueDateTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.dueTimeTextView.setPaintFlags(holder.dueTimeTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskItemClick(currentTask);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onTaskItemLongClick(currentTask);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTextView;
        TextView dueDateTextView;
        TextView dueTimeTextView;
        CheckBox checkBoxCompleted;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.textViewTaskName);
            dueDateTextView = itemView.findViewById(R.id.textViewDueDate);
            dueTimeTextView = itemView.findViewById(R.id.textViewDueTime);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
        }
    }

    public interface OnTaskItemClickListener {
        void onTaskCheckChanged(Task task, boolean isChecked);
        void onTaskItemClick(Task task);
        void onTaskItemLongClick(Task task);
    }
}
