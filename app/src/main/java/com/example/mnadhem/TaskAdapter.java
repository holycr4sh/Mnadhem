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

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

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
        return new TaskViewHolder(itemView, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = taskList.get(position);
        holder.taskNameTextView.setText(currentTask.getName());
        holder.checkBoxCompleted.setChecked(currentTask.isCompleted());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        Date dueDate = currentTask.getDueDate();
        if (dueDate != null) {
            holder.dueDateTextView.setText("Due: " + dateFormat.format(dueDate));
            holder.dueDateTextView.setVisibility(View.VISIBLE);

            Time dueTime = currentTask.getDueTime();
            if (dueTime != null) {
                holder.dueTimeTextView.setText(timeFormat.format(dueTime));
                holder.dueTimeTextView.setVisibility(View.VISIBLE);
            } else {
                holder.dueTimeTextView.setVisibility(View.GONE);
            }
        } else {
            holder.dueDateTextView.setVisibility(View.GONE);
        }

        updateTaskCompletionUI(holder, currentTask.isCompleted());

        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) ->
                listener.onTaskCheckChanged(currentTask, isChecked)
        );

        holder.itemView.setOnClickListener(v -> listener.onTaskItemClick(currentTask));

        holder.itemView.setOnLongClickListener(v -> {
            listener.onTaskItemLongClick(currentTask);
            return true;
        });
    }

    private void updateTaskCompletionUI(TaskViewHolder holder, boolean isCompleted) {
        int flags = holder.taskNameTextView.getPaintFlags();
        if (isCompleted) {
            flags |= Paint.STRIKE_THRU_TEXT_FLAG;
        } else {
            flags &= (~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        holder.taskNameTextView.setPaintFlags(flags);
        holder.dueDateTextView.setPaintFlags(flags);
        holder.dueTimeTextView.setPaintFlags(flags);
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
        Context context;

        public TaskViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            taskNameTextView = itemView.findViewById(R.id.textViewTaskName);
            dueDateTextView = itemView.findViewById(R.id.textViewDueDate);
            dueTimeTextView = itemView.findViewById(R.id.textViewDueTime);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);

            int whiteColor = context.getResources().getColor(R.color.white);
            taskNameTextView.setTextColor(whiteColor);
            dueDateTextView.setTextColor(whiteColor);
            dueTimeTextView.setTextColor(whiteColor);
            checkBoxCompleted.setTextColor(whiteColor);
        }
    }

    public interface OnTaskItemClickListener {
        void onTaskCheckChanged(Task task, boolean isChecked);
        void onTaskItemClick(Task task);
        void onTaskItemLongClick(Task task);
    }
}