package com.example.baitap4.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.baitap4.R;
import com.example.baitap4.model.Employee;
import com.example.baitap4.viewmodel.EmployeeDetailViewModel;

public class EmployeeDetailActivity extends AppCompatActivity {
    private EditText etName, etAge, etSalary, etImage;
    private ImageView ivProfile;
    private EmployeeDetailViewModel viewModel;
    private int employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết nhân viên");
        }

        employeeId = getIntent().getIntExtra("employee_id", -1);
        if (employeeId == -1) {
            finish();
            return;
        }

        initViews();
        initViewModel();
        observeViewModel();
        loadEmployeeDetails();
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etSalary = findViewById(R.id.et_salary);
        etImage = findViewById(R.id.et_image);
        ivProfile = findViewById(R.id.iv_profile);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(EmployeeDetailViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getCurrentEmployee().observe(this, this::displayEmployeeInfo);

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
        });

        viewModel.getIsEditMode().observe(this, this::setEditMode);
    }

    private void loadEmployeeDetails() {
        viewModel.loadEmployeeDetails(employeeId);
    }

    private void displayEmployeeInfo(Employee employee) {
        if (employee != null) {
            etName.setText(employee.getEmployee_name());
            etAge.setText(String.valueOf(employee.getEmployee_age()));
            etSalary.setText(String.valueOf(employee.getEmployee_salary()));
            etImage.setText(employee.getProfile_image());

            if (employee.getProfile_image() != null && !employee.getProfile_image().isEmpty()) {
                Glide.with(this)
                        .load(employee.getProfile_image())
                        .placeholder(R.drawable.ic_person)
                        .into(ivProfile);
            }
        }
    }

    private void setEditMode(boolean editMode) {
        etName.setEnabled(editMode);
        etAge.setEnabled(editMode);
        etSalary.setEnabled(editMode);
        etImage.setEnabled(editMode);

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);

        Boolean isEditMode = viewModel.getIsEditMode().getValue();
        boolean editMode = isEditMode != null && isEditMode;

        MenuItem editItem = menu.findItem(R.id.menu_edit);
        MenuItem saveItem = menu.findItem(R.id.menu_save);
        MenuItem cancelItem = menu.findItem(R.id.menu_cancel);

        editItem.setVisible(!editMode);
        saveItem.setVisible(editMode);
        cancelItem.setVisible(editMode);

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            Boolean isEditMode = viewModel.getIsEditMode().getValue();
            if (isEditMode != null && isEditMode) {
                cancelEdit();
            } else {
                finish();
            }
            return true;
        } else if (itemId == R.id.menu_edit) {
            viewModel.setEditMode(true);
            return true;
        } else if (itemId == R.id.menu_save) {
            saveEmployee();
            return true;
        } else if (itemId == R.id.menu_cancel) {
            cancelEdit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cancelEdit() {
        viewModel.setEditMode(false);
        // Employee info will be restored automatically through observer
    }

    private void saveEmployee() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String salaryStr = etSalary.getText().toString().trim();
        String image = etImage.getText().toString().trim();

        viewModel.updateEmployee(employeeId, name, ageStr, salaryStr, image)
                .observe(this, success -> {
                    if (success) {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    }
                    // Error handling is done in ViewModel observer
                });
    }
}