package com.example.baitap4.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baitap4.R;
import com.example.baitap4.model.Employee;

import java.util.List;
import android.widget.ImageView;
import android.widget.TextView;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder>{
    private List<Employee> employees;
    private final Context context;
    private OnEmployeeClickListener listener;

    public interface OnEmployeeClickListener {
        void onEmployeeClick(Employee employee);
        void onEmployeeMenuClick(Employee employee, View view);
    }

    public EmployeeAdapter(Context context, List<Employee> employees) {
        this.context = context;
        this.employees = employees;
    }

    public void setOnEmployeeClickListener(OnEmployeeClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = employees.get(position);
        holder.tvStt.setText(String.valueOf(position + 1));
        holder.tvName.setText(employee.getEmployee_name());

        // Load profile image
        if (employee.getProfile_image() != null && !employee.getProfile_image().isEmpty()) {
            Glide.with(context)
                    .load(employee.getProfile_image())
                    .placeholder(R.drawable.ic_person)
                    .into(holder.ivProfile);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEmployeeClick(employee);
            }
        });

        holder.ivMenu.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEmployeeMenuClick(employee, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateEmployees(List<Employee> newEmployees) {
        this.employees = newEmployees;
        notifyDataSetChanged();
    }

//    public void removeEmployee(Employee employee) {
//        int position = employees.indexOf(employee);
//        if (position != -1) {
//            employees.remove(position);
//            notifyItemRemoved(position);
//        }
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStt, tvName;
        ImageView ivProfile, ivMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tv_stt);
            tvName = itemView.findViewById(R.id.tv_name);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            ivMenu = itemView.findViewById(R.id.iv_menu);
        }
    }
}
