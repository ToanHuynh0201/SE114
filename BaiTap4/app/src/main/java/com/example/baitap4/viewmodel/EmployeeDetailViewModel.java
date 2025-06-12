package com.example.baitap4.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.baitap4.model.Employee;
import com.example.baitap4.repository.EmployeeRepository;

public class EmployeeDetailViewModel extends ViewModel {
    private final EmployeeRepository employeeRepository;
    private final MutableLiveData<Employee> currentEmployee = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEditMode = new MutableLiveData<>();

    public EmployeeDetailViewModel() {
        employeeRepository = EmployeeRepository.getInstance();
        isLoading.setValue(false);
        isEditMode.setValue(false);
    }

    public void loadEmployeeDetails(int employeeId) {
        isLoading.setValue(true);

        LiveData<Employee> employeeLiveData = employeeRepository.getEmployee(employeeId);
        employeeLiveData.observeForever(employee -> {
            isLoading.setValue(false);
            if (employee != null) {
                currentEmployee.setValue(employee);
            } else {
                errorMessage.setValue("Lỗi tải thông tin nhân viên");
            }
        });
    }

    public LiveData<Boolean> updateEmployee(int employeeId, String name, String ageStr, String salaryStr, String image) {
        // Validate input
        if (name.isEmpty() || ageStr.isEmpty() || salaryStr.isEmpty()) {
            errorMessage.setValue("Vui lòng điền đầy đủ thông tin");
            return new MutableLiveData<>(false);
        }

        try {
            int age = Integer.parseInt(ageStr);
            double salary = Double.parseDouble(salaryStr);

            Employee updatedEmployee = new Employee();
            updatedEmployee.setEmployee_name(name);
            updatedEmployee.setEmployee_age(age);
            updatedEmployee.setEmployee_salary(salary);
            updatedEmployee.setProfile_image(image);

            isLoading.setValue(true);

            LiveData<Boolean> result = employeeRepository.updateEmployee(employeeId, updatedEmployee);

            result.observeForever(success -> {
                isLoading.setValue(false);
                if (success) {
                    // Update current employee data
                    updatedEmployee.setId(employeeId);
                    currentEmployee.setValue(updatedEmployee);
                    setEditMode(false);
                } else {
                    errorMessage.setValue("Lỗi cập nhật nhân viên");
                }
            });

            return result;

        } catch (NumberFormatException e) {
            errorMessage.setValue("Tuổi và lương phải là số");
            return new MutableLiveData<>(false);
        }
    }

    public void setEditMode(boolean editMode) {
        isEditMode.setValue(editMode);
    }

    public LiveData<Employee> getCurrentEmployee() {
        return currentEmployee;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsEditMode() {
        return isEditMode;
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }
}
