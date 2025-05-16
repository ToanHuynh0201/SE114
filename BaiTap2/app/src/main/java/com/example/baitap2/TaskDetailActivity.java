package com.example.baitap2;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity {
    private EditText etTaskTitle, etTaskDescription, etTaskCreator, etTaskAssignee;
    private TextView tvCreatedTime;
    private Button btnSelectDeadline, btnCancel, btnSave;
    private CheckBox cbTaskCompleted;

    private Task task;
    private int position;
    private Date selectedDeadline;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dateTimeFormat;

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
        Log.d("TaskDetailActivity", "onCreate called");

        // Khởi tạo calendar và định dạng ngày tháng
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Ánh xạ các thành phần UI
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        btnSelectDeadline = findViewById(R.id.btnSelectDeadline);
        etTaskCreator = findViewById(R.id.etTaskCreator);
        etTaskAssignee = findViewById(R.id.etTaskAssignee);
        tvCreatedTime = findViewById(R.id.tvCreatedTime);
        cbTaskCompleted = findViewById(R.id.cbTaskCompleted);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("task")) {
            task = (Task) intent.getSerializableExtra("task");
            position = intent.getIntExtra("position", -1);

            // Hiển thị dữ liệu của task
            if (task != null) {
                displayTaskData();
                Toast.makeText(this, "Đang xem chi tiết task: " + task.getTitle(), Toast.LENGTH_SHORT).show();
            } else {
                Log.e("TaskDetailActivity", "task is null");
                Toast.makeText(this, "Lỗi: Không thể tải dữ liệu task", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Log.e("TaskDetailActivity", "Intent does not have task extra");
            Toast.makeText(this, "Lỗi: Không nhận được dữ liệu", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Thiết lập sự kiện click cho các nút
        btnSelectDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTaskData();
            }
        });
    }

    // Hiển thị dữ liệu của task
    private void displayTaskData() {
        try {
            etTaskTitle.setText(task.getTitle());
            etTaskDescription.setText(task.getDescription());

            if (task.getDeadline() != null) {
                selectedDeadline = task.getDeadline();
                btnSelectDeadline.setText("Deadline: " + dateFormat.format(selectedDeadline));
            } else {
                btnSelectDeadline.setText("Chọn deadline");
            }

            etTaskCreator.setText(task.getCreator());
            etTaskAssignee.setText(task.getAssignee());

            if (task.getCreatedTime() != null) {
                tvCreatedTime.setText(dateTimeFormat.format(task.getCreatedTime()));
            } else {
                tvCreatedTime.setText("N/A");
            }

            cbTaskCompleted.setChecked(task.isCompleted());
        } catch (Exception e) {
            Log.e("TaskDetailActivity", "Error displaying task data", e);
            Toast.makeText(this, "Lỗi hiển thị dữ liệu task", Toast.LENGTH_SHORT).show();
        }
    }

    // Lưu dữ liệu của task
    private void saveTaskData() {
        try {
            if (task == null) {
                task = new Task();
            }

            task.setTitle(etTaskTitle.getText().toString().trim());
            task.setDescription(etTaskDescription.getText().toString().trim());
            task.setDeadline(selectedDeadline);
            task.setCreator(etTaskCreator.getText().toString().trim());
            task.setAssignee(etTaskAssignee.getText().toString().trim());
            task.setCompleted(cbTaskCompleted.isChecked());

            // Trả về kết quả cho MainActivity
            Intent resultIntent = new Intent(this, MainActivity.class);
            resultIntent.putExtra("task", task);
            resultIntent.putExtra("position", position);
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Đã lưu thông tin task", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Log.e("TaskDetailActivity", "Error saving task data", e);
            Toast.makeText(this, "Lỗi khi lưu dữ liệu task", Toast.LENGTH_SHORT).show();
        }
    }

    // Hiển thị DatePickerDialog để chọn deadline
    private void showDatePickerDialog() {
        if (selectedDeadline != null) {
            calendar.setTime(selectedDeadline);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selectedDeadline = calendar.getTime();
                        btnSelectDeadline.setText("Deadline: " + dateFormat.format(selectedDeadline));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}