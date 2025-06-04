package com.example.baitap3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ListView lvTasks;
    private TaskAdapter taskAdapter;
    private DatabaseHelper databaseHelper;
    private List<Task> taskList;
    private Date selectedDeadline;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initDatabase();
        loadTasks();
        setupListView();
    }

    private void initViews() {
        lvTasks = findViewById(R.id.lvTasks);
    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void loadTasks() {
        taskList = databaseHelper.getAllTasks();
        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(this, taskList);
            lvTasks.setAdapter(taskAdapter);
        } else {
            taskAdapter.updateTasks(taskList);
        }
    }

    private void setupListView() {
        lvTasks.setOnItemClickListener((parent, view, position, id) -> {
            // Only navigate to detail if not in selection mode
            if (!taskAdapter.isSelectionMode()) {
                Task selectedTask = taskList.get(position);
                Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
                intent.putExtra("task", selectedTask);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_add) {
            // Exit selection mode when adding new task
            if (taskAdapter.isSelectionMode()) {
                taskAdapter.setSelectionMode(false);
            }
            showAddTaskDialog();
            return true;
        } else if (id == R.id.menu_edit) {
            if (taskAdapter.isSelectionMode()) {
                // Exit selection mode
                taskAdapter.setSelectionMode(false);
                Toast.makeText(this, "Đã thoát chế độ chọn", Toast.LENGTH_SHORT).show();
            } else {
                // Enter selection mode
                taskAdapter.setSelectionMode(true);
                Toast.makeText(this, "Chọn các task để xóa", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.menu_delete) {
            if (taskAdapter.isSelectionMode()) {
                showDeleteConfirmDialog();
            } else {
                Toast.makeText(this, "Vui lòng vào chế độ chọn trước khi xóa", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etDialogTaskTitle);
        EditText etDescription = dialogView.findViewById(R.id.etDialogTaskDescription);
        Button btnSelectDeadline = dialogView.findViewById(R.id.btnDialogSelectDeadline);
        EditText etCreator = dialogView.findViewById(R.id.etDialogTaskCreator);
        EditText etAssignee = dialogView.findViewById(R.id.etDialogTaskAssignee);
        Button btnCancel = dialogView.findViewById(R.id.btnDialogCancel);
        Button btnAdd = dialogView.findViewById(R.id.btnDialogAdd);

        AlertDialog dialog = builder.create();

        btnSelectDeadline.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
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
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            selectedDeadline = null;
        });

        btnAdd.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String creator = etCreator.getText().toString().trim();
            String assignee = etAssignee.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập tiêu đề task", Toast.LENGTH_SHORT).show();
                return;
            }

            Task newTask = new Task(title, description, selectedDeadline, creator, assignee);
            long result = databaseHelper.addTask(newTask);

            if (result != -1) {
                Toast.makeText(MainActivity.this, "Thêm task thành công", Toast.LENGTH_SHORT).show();
                loadTasks();
                dialog.dismiss();
                selectedDeadline = null;
            } else {
                Toast.makeText(MainActivity.this, "Lỗi khi thêm task", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showDeleteConfirmDialog() {
        List<String> selectedIds = taskAdapter.getSelectedTaskIds();

        if (selectedIds.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn task để xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa " + selectedIds.size() + " task đã chọn?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            boolean success = databaseHelper.deleteTasks(selectedIds);

            if (success) {
                taskAdapter.setSelectionMode(false);
                loadTasks();
                Toast.makeText(MainActivity.this, "Đã xóa " + selectedIds.size() + " task", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Lỗi khi xóa task", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> {
            // Keep selection mode active when canceling
            dialog.dismiss();
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        // Handle back button press
        if (taskAdapter != null && taskAdapter.isSelectionMode()) {
            // Exit selection mode when back is pressed
            taskAdapter.setSelectionMode(false);
            Toast.makeText(this, "Đã thoát chế độ chọn", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            loadTasks(); // Reload tasks when returning from TaskDetailActivity
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }
}