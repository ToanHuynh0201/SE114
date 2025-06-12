package com.example.baitap4.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.baitap4.R;
import com.example.baitap4.adapter.EmployeeAdapter;
import com.example.baitap4.model.Employee;
import com.example.baitap4.viewmodel.MainViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements EmployeeAdapter.OnEmployeeClickListener {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvLoading;
    private EmployeeAdapter adapter;
    private MainViewModel viewModel;
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
        initViewModel();
        initViews();
        setupRecyclerView();
        observeViewModel();
        viewModel.loadEmployees();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        progressBar = findViewById(R.id.progress_bar);
        tvLoading = findViewById(R.id.tv_loading);

        swipeRefresh.setOnRefreshListener(() -> viewModel.refreshEmployees());
    }

    private void setupRecyclerView() {
        adapter = new EmployeeAdapter(this, new ArrayList<>());
        adapter.setOnEmployeeClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        // Observe employees data
        viewModel.getEmployees().observe(this, employees -> {
            if (employees != null) {
                adapter.updateEmployees(employees);
                // Hide progress bar when data is loaded
                hideProgressBar();

                // Show RecyclerView if it was hidden
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    // Show appropriate loading indicator
                    if (adapter.getItemCount() == 0) {
                        // First time loading - show progress bar
                        showProgressBar();
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        // Refreshing - use SwipeRefreshLayout
                        swipeRefresh.setRefreshing(true);
                    }
                } else {
                    // Hide all loading indicators
                    hideProgressBar();
                    swipeRefresh.setRefreshing(false);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                // Hide loading indicators on error
                hideProgressBar();
                swipeRefresh.setRefreshing(false);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        tvLoading.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_new) {
            startActivity(new Intent(this, AddEmployeeActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEmployeeClick(Employee employee) {
        Intent intent = new Intent(this, EmployeeDetailActivity.class);
        intent.putExtra("employee_id", employee.getId());
        startActivity(intent);
    }

    @Override
    public void onEmployeeMenuClick(Employee employee, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.employee_context_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_delete) {
                viewModel.deleteEmployee(employee);
                Toast.makeText(this, "Đã xóa nhân viên", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        popup.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.refreshEmployees();
    }
}