package com.example.baitap3;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity {
    private EditText etTaskTitle, etTaskDescription, etTaskCreator, etTaskAssignee;
    private Button btnSelectDeadline, btnCancel, btnSave, btnSelectAssignees;
    private TextView tvCreatedTime, tvAssigneesLabel;
    private CheckBox cbTaskCompleted;
    private RecyclerView recyclerViewAssignees;
    private AssigneeAdapter assigneeAdapter;

    private DatabaseHelper databaseHelper;
    private Task currentTask;
    private Date selectedDeadline;
    private List<Contact> selectedAssignees;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    private ActivityResultLauncher<Intent> contactPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initDatabase();
        setupContactPickerLauncher();
        loadTaskData();
        setupClickListeners();
    }

    private void initViews() {
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etTaskCreator = findViewById(R.id.etTaskCreator);
        etTaskAssignee = findViewById(R.id.etTaskAssignee);
        btnSelectDeadline = findViewById(R.id.btnSelectDeadline);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        btnSelectAssignees = findViewById(R.id.btnSelectAssignees);
        tvCreatedTime = findViewById(R.id.tvCreatedTime);
        tvAssigneesLabel = findViewById(R.id.tvAssigneesLabel);
        cbTaskCompleted = findViewById(R.id.cbTaskCompleted);
        recyclerViewAssignees = findViewById(R.id.recyclerViewAssignees);

        selectedAssignees = new ArrayList<>();
        assigneeAdapter = new AssigneeAdapter(selectedAssignees, this::removeAssignee);
        recyclerViewAssignees.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAssignees.setAdapter(assigneeAdapter);
    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupContactPickerLauncher() {
        contactPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<Contact> contacts = (ArrayList<Contact>) result.getData()
                                .getSerializableExtra("selected_contacts");
                        if (contacts != null) {
                            selectedAssignees.clear();
                            selectedAssignees.addAll(contacts);
                            assigneeAdapter.notifyDataSetChanged();
                            updateAssigneesDisplay();
                        }
                    }
                }
        );
    }

    @SuppressLint("SetTextI18n")
    private void loadTaskData() {
        currentTask = (Task) getIntent().getSerializableExtra("task");

        if (currentTask != null) {
            etTaskTitle.setText(currentTask.getTitle());
            etTaskDescription.setText(currentTask.getDescription() != null ? currentTask.getDescription() : "");
            etTaskCreator.setText(currentTask.getCreator() != null ? currentTask.getCreator() : "");
            etTaskAssignee.setText(currentTask.getAssignee() != null ? currentTask.getAssignee() : "");

            if (currentTask.getDeadline() != null) {
                selectedDeadline = currentTask.getDeadline();
                btnSelectDeadline.setText("Deadline: " + dateFormat.format(selectedDeadline));
            } else {
                btnSelectDeadline.setText("Chọn deadline");
            }

            tvCreatedTime.setText(dateTimeFormat.format(currentTask.getCreatedTime()));
            cbTaskCompleted.setChecked(currentTask.isCompleted());

            // Load assignees
            selectedAssignees.clear();
            if (currentTask.getAssignees() != null && !currentTask.getAssignees().isEmpty()) {
                selectedAssignees.addAll(currentTask.getAssignees());
            }
            assigneeAdapter.notifyDataSetChanged();
            updateAssigneesDisplay();
        }
    }

    private void setupClickListeners() {
        btnSelectDeadline.setOnClickListener(v -> showDatePicker());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveTask());
        btnSelectAssignees.setOnClickListener(v -> openContactPicker());
    }

    private void openContactPicker() {
        Intent intent = new Intent(this, ContactPickerActivity.class);
        contactPickerLauncher.launch(intent);
    }

    private void removeAssignee(Contact contact) {
        selectedAssignees.remove(contact);
        assigneeAdapter.notifyDataSetChanged();
        updateAssigneesDisplay();
    }

    private void updateAssigneesDisplay() {
        if (selectedAssignees.isEmpty()) {
            tvAssigneesLabel.setText("Chưa có người thực hiện");
        } else {
            tvAssigneesLabel.setText("Người thực hiện (" + selectedAssignees.size() + "):");
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDeadline != null) {
            calendar.setTime(selectedDeadline);
        }

        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    selectedDeadline = calendar.getTime();
                    btnSelectDeadline.setText("Deadline: " + dateFormat.format(selectedDeadline));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveTask() {
        String title = etTaskTitle.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();
        String creator = etTaskCreator.getText().toString().trim();
        String assignee = etTaskAssignee.getText().toString().trim();
        boolean isCompleted = cbTaskCompleted.isChecked();

        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề task", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin task
        currentTask.setTitle(title);
        currentTask.setDescription(description);
        currentTask.setDeadline(selectedDeadline);
        currentTask.setCreator(creator);
        currentTask.setAssignee(assignee);
        currentTask.setAssignees(new ArrayList<>(selectedAssignees));
        currentTask.setCompleted(isCompleted);

        // Lưu vào database
        int result = databaseHelper.updateTask(currentTask);

        if (result > 0) {
            Toast.makeText(this, "Cập nhật task thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật task", Toast.LENGTH_SHORT).show();
        }
    }
}