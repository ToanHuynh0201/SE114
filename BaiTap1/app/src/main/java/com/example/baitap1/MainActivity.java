package com.example.baitap1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText editTextId;
    private TextInputEditText editTextPassword;
    private Button buttonLogin;
    private List<User> userList;
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
        setupOnClickEvent();
        createSampleUsers();
    }

    private void createSampleUsers() {
        userList = new ArrayList<>();

        // Thêm một số người dùng mẫu
        userList.add(new User(
                "Nguyễn Văn A",
                "123",
                "001",
                "01/01/2023",
                "Nhân viên kế toán",
                "Kế toán",
                "https://i.pravatar.cc/150?img=1"
        ));

        userList.add(new User(
                "Trần Thị B",
                "123",
                "002",
                "15/02/2023",
                "Trưởng phòng nhân sự",
                "Nhân sự",
                "https://i.pravatar.cc/150?img=2"
        ));

        userList.add(new User(
                "Lê Văn C",
                "123",
                "003",
                "10/03/2023",
                "Kỹ sư phần mềm",
                "IT",
                "https://i.pravatar.cc/150?img=3"
        ));
    }

    private void setupOnClickEvent() {
        buttonLogin.setOnClickListener(v -> {
            String id = Objects.requireNonNull(editTextId.getText()).toString().trim();
            String password = Objects.requireNonNull(editTextPassword.getText()).toString().trim();

            // Kiểm tra id và password
            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tìm kiếm người dùng trong danh sách
            User user = findUser(id, password);
            if (user != null) {
                // Đăng nhập thành công, chuyển đến MainActivity
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("USER", user);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                // Đăng nhập thất bại
                Toast.makeText(this, "ID hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private User findUser(String id, String password) {
        for (User user : userList) {
            if (user.getId().equals(id) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    private void initViews() {
        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
    }
}