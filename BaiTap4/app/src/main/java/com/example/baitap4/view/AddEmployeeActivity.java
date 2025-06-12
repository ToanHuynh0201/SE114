package com.example.baitap4.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.baitap4.R;
import com.example.baitap4.viewmodel.AddEmployeeViewModel;

public class AddEmployeeActivity extends AppCompatActivity {
    private EditText etName, etAge, etSalary, etImage;
    private Button btnSave;
    private AddEmployeeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_employee);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thêm nhân viên mới");
        }

        initViews();
        initViewModel();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etSalary = findViewById(R.id.et_salary);
        etImage = findViewById(R.id.et_image);
        btnSave = findViewById(R.id.btn_save);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(AddEmployeeViewModel.class);
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveEmployee());
    }

    private void observeViewModel() {
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                viewModel.clearErrorMessage();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            // Show/hide progress bar
            findViewById(R.id.progress_bar).setVisibility(isLoading ? View.VISIBLE : View.GONE);
            // Show/hide overlay (optional)
            findViewById(R.id.loading_overlay).setVisibility(isLoading ? View.VISIBLE : View.GONE);
            // Disable save button when loading
            btnSave.setEnabled(!isLoading);
        });
    }

    private void saveEmployee() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String salaryStr = etSalary.getText().toString().trim();
        String image = etImage.getText().toString().trim();

        viewModel.createEmployee(name, ageStr, salaryStr, image)
                .observe(this, success -> {
                    if (success) {
                        Toast.makeText(this, "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    // Error handling is done in ViewModel observer
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}