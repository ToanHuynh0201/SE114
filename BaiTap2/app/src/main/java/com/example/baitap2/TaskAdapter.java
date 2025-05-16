package com.example.baitap2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends ArrayAdapter<Task> {
    private Context context;
    private List<Task> taskList;
    private SimpleDateFormat dateFormat;

    private static class ViewHolder{
        TextView tvTitle;
        TextView tvDeadline;
        CheckBox cbStatus;
    }

    public TaskAdapter(@NonNull Context context, List<Task> taskList) {
        super(context, R.layout.task_item, taskList);
        this.context = context;
        this.taskList = taskList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Task currentTask = getItem(position);
        ViewHolder holder;
        final View result;

        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.task_item, parent, false);
            holder.tvTitle = convertView.findViewById(R.id.tvTaskTitle);
            holder.tvDeadline = convertView.findViewById(R.id.tvTaskDeadline);
            holder.cbStatus = convertView.findViewById(R.id.cbTaskStatus);

            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        holder.tvTitle.setText(currentTask.getTitle());

        if (currentTask.getDeadline() != null) {
            holder.tvDeadline.setText("Deadline: " + dateFormat.format(currentTask.getDeadline()));
        } else {
            holder.tvDeadline.setText("Không có deadline");
        }

        holder.cbStatus.setChecked(currentTask.isCompleted());

        holder.cbStatus.setOnClickListener(v -> currentTask.setCompleted(holder.cbStatus.isChecked()));
        convertView.setOnClickListener(v -> {
            if(context instanceof MainActivity){
                ((MainActivity) context).openTaskDetail(currentTask, position);
                Toast.makeText(context, "Test from adapter", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
}
