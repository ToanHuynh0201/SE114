package com.example.baitap3;

import android.annotation.SuppressLint;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private List<Task> tasks;
    private LayoutInflater inflater;
    private boolean isSelectionMode = false;
    private List<String> selectedTaskIds;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private DatabaseHelper databaseHelper;

    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
        this.inflater = LayoutInflater.from(context);
        this.selectedTaskIds = new ArrayList<>();
        this.databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.task_item, parent, false);
            holder = new ViewHolder();
            holder.tvTaskTitle = convertView.findViewById(R.id.tvTaskTitle);
            holder.tvTaskDeadline = convertView.findViewById(R.id.tvTaskDeadline);
            holder.cbTaskSelection = convertView.findViewById(R.id.cbTaskSelection);
            holder.cbTaskStatus = convertView.findViewById(R.id.cbTaskStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = tasks.get(position);
        holder.tvTaskTitle.setText(task.getTitle());

        if (task.getDeadline() != null) {
            holder.tvTaskDeadline.setText("Deadline: " + dateFormat.format(task.getDeadline()));
        } else {
            holder.tvTaskDeadline.setText("Deadline: Chưa có");
        }

        // Handle selection checkbox (left side)
        if (isSelectionMode) {
            holder.cbTaskSelection.setVisibility(View.VISIBLE);
            holder.cbTaskSelection.setChecked(selectedTaskIds.contains(task.getId()));
            holder.cbTaskSelection.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!selectedTaskIds.contains(task.getId())) {
                        selectedTaskIds.add(task.getId());
                    }
                } else {
                    selectedTaskIds.remove(task.getId());
                }
            });
        } else {
            holder.cbTaskSelection.setVisibility(View.GONE);
        }

        // Handle completion status checkbox (right side)
        holder.cbTaskStatus.setChecked(task.isCompleted());
        holder.cbTaskStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update task completion status
            task.setCompleted(isChecked);
            // Update in database
            databaseHelper.updateTaskStatus(task.getId(), isChecked);
        });

        // Prevent checkboxes from consuming click events
        holder.cbTaskStatus.setFocusable(false);
        holder.cbTaskSelection.setFocusable(false);

        return convertView;
    }

    public boolean isSelectionMode() {
        return isSelectionMode;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.isSelectionMode = selectionMode;
        if (!selectionMode) {
            selectedTaskIds.clear();
        }
        notifyDataSetChanged();
    }

    public List<String> getSelectedTaskIds() {
        return new ArrayList<>(selectedTaskIds);
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView tvTaskTitle;
        TextView tvTaskDeadline;
        CheckBox cbTaskSelection;  // Checkbox for selection (left)
        CheckBox cbTaskStatus;     // Checkbox for completion status (right)
    }
}