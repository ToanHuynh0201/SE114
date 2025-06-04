package com.example.baitap3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssigneeAdapter extends RecyclerView.Adapter<AssigneeAdapter.AssigneeViewHolder> {
    private List<Contact> assigneeList;
    private OnRemoveAssigneeListener listener;

    public interface OnRemoveAssigneeListener {
        void onRemoveAssignee(Contact contact);
    }

    public AssigneeAdapter(List<Contact> assigneeList, OnRemoveAssigneeListener listener) {
        this.assigneeList = assigneeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssigneeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assignee, parent, false);
        return new AssigneeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssigneeViewHolder holder, int position) {
        Contact assignee = assigneeList.get(position);
        holder.bind(assignee);
    }

    @Override
    public int getItemCount() {
        return assigneeList.size();
    }

    class AssigneeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAssigneeName;
        private TextView tvAssigneePhone;
        private ImageButton btnRemove;

        public AssigneeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAssigneeName = itemView.findViewById(R.id.tvAssigneeName);
            tvAssigneePhone = itemView.findViewById(R.id.tvAssigneePhone);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        public void bind(Contact assignee) {
            tvAssigneeName.setText(assignee.getName());
            tvAssigneePhone.setText(assignee.getPhoneNumber());

            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveAssignee(assignee);
                }
            });
        }
    }
}