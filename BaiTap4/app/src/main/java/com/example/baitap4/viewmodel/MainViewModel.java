package com.example.baitap4.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.baitap4.model.Employee;
import com.example.baitap4.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private final EmployeeRepository repository;
    private final MutableLiveData<List<Employee>> employeesLiveData;
    private final MutableLiveData<Boolean> isLoadingLiveData;
    private final MutableLiveData<String> errorMessageLiveData;
    private final List<Employee> employeeList;

    public MainViewModel() {
        repository = EmployeeRepository.getInstance();
        employeesLiveData = new MutableLiveData<>();
        isLoadingLiveData = new MutableLiveData<>();
        errorMessageLiveData = new MutableLiveData<>();
        employeeList = new ArrayList<>();
    }

    public LiveData<List<Employee>> getEmployees() {
        return employeesLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessageLiveData;
    }

    public void loadEmployees() {
        isLoadingLiveData.setValue(true);

        repository.getEmployees().observeForever(employees -> {
            isLoadingLiveData.setValue(false);

            if (employees != null) {
                employeeList.clear();
                employeeList.addAll(employees);
                employeesLiveData.setValue(new ArrayList<>(employeeList));
            } else {
                errorMessageLiveData.setValue("Lỗi tải dữ liệu");
            }
        });
    }

    public void deleteEmployee(Employee employee) {
        repository.deleteEmployee(employee.getId()).observeForever(success -> {
            if (success) {
                employeeList.remove(employee);
                employeesLiveData.setValue(new ArrayList<>(employeeList));
            } else {
                errorMessageLiveData.setValue("Lỗi xóa nhân viên");
            }
        });
    }

    public void refreshEmployees() {
        loadEmployees();
    }
}
