package com.example.baitap2;
import android.app.Activity;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ListView listViewTasks;
    private FloatingActionButton fabAddTask;
    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;
    private Calendar calendar;

    // Sử dụng cả 2 cách để đảm bảo bạn có thể vào được TaskDetailActivity
    private static final int REQUEST_CODE_TASK_DETAIL = 1001;

    ActivityResultLauncher<Intent> taskDetailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Task updatedTask = (Task) data.getSerializableExtra("task");
                        int position = data.getIntExtra("position", -1);

                        if (position != -1 && updatedTask != null) {
                            taskList.set(position, updatedTask);
                            taskAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });

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

        calendar = Calendar.getInstance();

        // Khởi tạo danh sách và adapter
        taskList = new ArrayList<>();

        // Tạo dữ liệu mẫu
        createSampleData();

        // Khởi tạo các thành phần UI
        listViewTasks = findViewById(R.id.listViewTasks);
        fabAddTask = findViewById(R.id.fabAddTask);

        // Khởi tạo adapter và set cho ListView
        taskAdapter = new TaskAdapter(MainActivity.this, taskList);
        listViewTasks.setAdapter(taskAdapter);

        // Thiết lập sự kiện click cho ListView
        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("MainActivity", "Item clicked at position: " + position);
            Toast.makeText(MainActivity.this, "Test", Toast.LENGTH_SHORT).show();
            Task selectedTask = taskList.get(position);
            openTaskDetail(selectedTask, position);
        });

        // Thiết lập sự kiện click cho FloatingActionButton
        fabAddTask.setOnClickListener(v -> showAddTaskDialog());
    }


    // Mở màn hình chi tiết task bằng ActivityResultLauncher (cách mới)
    public void openTaskDetail(Task task, int position) {
        Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
        intent.putExtra("task", task);
        intent.putExtra("position", position);
        taskDetailLauncher.launch(intent);
    }

    // Xử lý kết quả bằng phương thức truyền thống
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TASK_DETAIL && resultCode == RESULT_OK && data != null) {
            Task updatedTask = (Task) data.getSerializableExtra("task");
            int position = data.getIntExtra("position", -1);

            if (position != -1 && updatedTask != null) {
                taskList.set(position, updatedTask);
                taskAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Task đã được cập nhật", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Hiển thị dialog thêm task mới
    private void showAddTaskDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_task);

        final EditText etDialogTaskTitle = dialog.findViewById(R.id.etDialogTaskTitle);
        final EditText etDialogTaskDescription = dialog.findViewById(R.id.etDialogTaskDescription);
        final Button btnDialogSelectDeadline = dialog.findViewById(R.id.btnDialogSelectDeadline);
        final EditText etDialogTaskCreator = dialog.findViewById(R.id.etDialogTaskCreator);
        final EditText etDialogTaskAssignee = dialog.findViewById(R.id.etDialogTaskAssignee);
        Button btnDialogCancel = dialog.findViewById(R.id.btnDialogCancel);
        Button btnDialogAdd = dialog.findViewById(R.id.btnDialogAdd);

        final Date[] selectedDeadline = new Date[1];

        // Thiết lập sự kiện cho nút chọn deadline
        btnDialogSelectDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                selectedDeadline[0] = calendar.getTime();
                                btnDialogSelectDeadline.setText("Deadline: " + dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });

        // Thiết lập sự kiện cho nút Hủy
        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Thiết lập sự kiện cho nút Thêm
        btnDialogAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etDialogTaskTitle.getText().toString().trim();
                String description = etDialogTaskDescription.getText().toString().trim();
                String creator = etDialogTaskCreator.getText().toString().trim();
                String assignee = etDialogTaskAssignee.getText().toString().trim();

                if (!title.isEmpty()) {
                    Task newTask = new Task(title, description, selectedDeadline[0], creator, assignee);
                    taskList.add(newTask);
                    taskAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    // Tạo dữ liệu mẫu
    private void createSampleData() {
        Calendar cal = Calendar.getInstance();

        // Task 1
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Task task1 = new Task("Hoàn thành báo cáo", "Báo cáo phải có đầy đủ số liệu và phân tích",
                cal.getTime(), "Nguyễn Văn A", "Trần Thị B");
        taskList.add(task1);

        // Task 2
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Task task2 = new Task("Họp nhóm phát triển", "Thảo luận về tính năng mới",
                cal.getTime(), "Lê Văn C", "Phạm Thị D");
        taskList.add(task2);

        // Task 3
        cal.add(Calendar.DAY_OF_MONTH, -3);
        Task task3 = new Task("Nộp bài tập lớn", "Hoàn thành dự án TodoList",
                cal.getTime(), "Hoàng Văn E", "Ngô Thị F");
        task3.setCompleted(true);
        taskList.add(task3);
    }
}