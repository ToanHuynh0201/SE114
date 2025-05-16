package com.example.baitap1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class HomeActivity extends AppCompatActivity {
    private ImageView imageViewUser;
    private TextView textViewName;
    private TextView textViewId;
    private TextView textViewDate;
    private TextView textViewDepartment;
    private TextView textViewDescription;
    private Button buttonLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupOnClickEvent();
    }
    private void setupOnClickEvent() {
        buttonLogout.setOnClickListener(v -> {
            // Đăng xuất và quay lại màn hình đăng nhập
            finish();
        });
    }

    private void initViews() {
        imageViewUser = findViewById(R.id.imageViewUser);
        textViewName = findViewById(R.id.textViewName);
        textViewId = findViewById(R.id.textViewId);
        textViewDate = findViewById(R.id.textViewDate);
        textViewDepartment = findViewById(R.id.textViewDepartment);
        textViewDescription = findViewById(R.id.textViewDescription);
        buttonLogout = findViewById(R.id.buttonLogout);

        // Lấy dữ liệu user từ Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER")) {
            User user = (User) intent.getSerializableExtra("USER");
            displayUserInfo(user);
        } else {
            // Nếu không có dữ liệu, quay lại màn hình đăng nhập
            finish();
        }
    }

    private void displayUserInfo(User user) {
        textViewName.setText(user.getName());
        textViewId.setText(user.getId());
        textViewDate.setText(user.getDate());
        textViewDepartment.setText(user.getDepartment());
        textViewDescription.setText(user.getDescription());

        // Sử dụng Glide để hiển thị ảnh từ URL
        Glide.with(this)
                .load(user.getImage())
                .apply(RequestOptions.centerCropTransform())  // Sử dụng centerCrop thay vì circleCrop để phù hợp với khung hình
                .placeholder(R.drawable.ic_launcher_foreground)  // Ảnh mặc định khi đang tải
                .error(R.drawable.ic_launcher_foreground)  // Ảnh hiển thị khi lỗi
                .into(imageViewUser);
    }
}