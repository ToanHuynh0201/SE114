package com.example.baitap3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> contactList;
    private OnContactSelectedListener listener;

    public interface OnContactSelectedListener {
        void onContactSelected(Contact contact, boolean isSelected);
    }

    public ContactAdapter(List<Contact> contactList, OnContactSelectedListener listener) {
        this.contactList = contactList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.bind(contact);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView tvContactName;
        private TextView tvContactPhone;
        private CheckBox cbSelected;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactPhone = itemView.findViewById(R.id.tvContactPhone);
            cbSelected = itemView.findViewById(R.id.cbSelected);
        }

        public void bind(Contact contact) {
            tvContactName.setText(contact.getName());
            tvContactPhone.setText(contact.getPhoneNumber());
            cbSelected.setChecked(contact.isSelected());

            cbSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
                contact.setSelected(isChecked);
                if (listener != null) {
                    listener.onContactSelected(contact, isChecked);
                }
            });

            itemView.setOnClickListener(v -> {
                cbSelected.setChecked(!cbSelected.isChecked());
            });
        }
    }
}