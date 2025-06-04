package com.example.baitap3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactPickerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;
    private List<Contact> selectedContacts;
    private Button btnConfirmSelection;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_picker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupPermissionLauncher();
        checkPermissionAndLoadContacts();
    }
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewContacts);
        btnConfirmSelection = findViewById(R.id.btnConfirmSelection);

        contactList = new ArrayList<>();
        selectedContacts = new ArrayList<>();

        contactAdapter = new ContactAdapter(contactList, this::onContactSelected);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);

        btnConfirmSelection.setOnClickListener(v -> confirmSelection());
    }

    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        loadContacts();
                    } else {
                        Toast.makeText(this, "Cần quyền truy cập danh bạ để sử dụng tính năng này", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
        );
    }

    private void checkPermissionAndLoadContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        } else {
            loadContacts();
        }
    }

    private void loadContacts() {
        contactList.clear();

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));

                Contact contact = new Contact(contactId, name, phoneNumber);

                // Tránh trùng lặp contact
                boolean exists = false;
                for (Contact existingContact : contactList) {
                    if (existingContact.getId().equals(contactId)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    contactList.add(contact);
                }
            }
            cursor.close();
        }

        contactAdapter.notifyDataSetChanged();
    }

    private void onContactSelected(Contact contact, boolean isSelected) {
        if (isSelected) {
            selectedContacts.add(contact);
        } else {
            selectedContacts.remove(contact);
        }

        btnConfirmSelection.setText("Xác nhận (" + selectedContacts.size() + ")");
        btnConfirmSelection.setEnabled(!selectedContacts.isEmpty());
    }

    private void confirmSelection() {
        if (selectedContacts.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một người thực hiện", Toast.LENGTH_SHORT).show();
            return;
        }

        // Trả về danh sách contact đã chọn
        ArrayList<Contact> result = new ArrayList<>(selectedContacts);
        getIntent().putExtra("selected_contacts", result);
        setResult(RESULT_OK, getIntent());
        finish();
    }
}