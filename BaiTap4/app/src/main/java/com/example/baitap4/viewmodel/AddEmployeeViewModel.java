package com.example.baitap4.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.baitap4.model.Employee;
import com.example.baitap4.repository.EmployeeRepository;

public class AddEmployeeViewModel extends ViewModel {
    private final EmployeeRepository employeeRepository;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public AddEmployeeViewModel() {
        employeeRepository = EmployeeRepository.getInstance();
        isLoading.setValue(false);
    }

    public LiveData<Boolean> createEmployee(String name, String ageStr, String salaryStr, String image) {
        // Validate input
        if (name.isEmpty() || ageStr.isEmpty() || salaryStr.isEmpty()) {
            errorMessage.setValue("Vui lòng điền đầy đủ thông tin");
            return new MutableLiveData<>(false);
        }

        try {
            int age = Integer.parseInt(ageStr);
            int salary = Integer.parseInt(salaryStr);

            Employee employee = new Employee();
            employee.setEmployee_name(name);
            employee.setEmployee_age(age);
            employee.setEmployee_salary(salary);
            employee.setProfile_image(image.isEmpty() ? "" : image);

            isLoading.setValue(true);

            LiveData<Boolean> result = employeeRepository.createEmployee(employee);

            // Reset loading state when done
            result.observeForever(success -> {
                isLoading.setValue(false);
                if (!success) {
                    errorMessage.setValue("Lỗi thêm nhân viên");
                }
            });

            return result;

        } catch (NumberFormatException e) {
            errorMessage.setValue("Tuổi và lương phải là số");
            return new MutableLiveData<>(false);
        }
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }
}
