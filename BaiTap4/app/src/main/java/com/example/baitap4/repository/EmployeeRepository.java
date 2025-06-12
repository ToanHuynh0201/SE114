package com.example.baitap4.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.baitap4.api.NetworkManager;
import com.example.baitap4.model.Employee;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeRepository {
    private static EmployeeRepository instance;

    public static EmployeeRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeRepository();
        }
        return instance;
    }

    public LiveData<List<Employee>> getEmployees() {
        MutableLiveData<List<Employee>> employeesLiveData = new MutableLiveData<>();

        NetworkManager.getInstance().getApiService().getEmployees()
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Employee>> call, @NonNull Response<List<Employee>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            employeesLiveData.setValue(response.body());
                        } else {
                            employeesLiveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Employee>> call, @NonNull Throwable t) {
                        employeesLiveData.setValue(null);
                    }
                });

        return employeesLiveData;
    }

    public LiveData<Employee> getEmployee(int employeeId) {
        MutableLiveData<Employee> employeeLiveData = new MutableLiveData<>();

        NetworkManager.getInstance().getApiService().getEmployee(employeeId)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Employee> call, @NonNull Response<Employee> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            employeeLiveData.setValue(response.body());
                        } else {
                            employeeLiveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Employee> call, @NonNull Throwable t) {
                        employeeLiveData.setValue(null);
                    }
                });

        return employeeLiveData;
    }

    public LiveData<Boolean> createEmployee(Employee employee) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        NetworkManager.getInstance().getApiService().createEmployee(employee)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Employee> call, @NonNull Response<Employee> response) {
                        resultLiveData.setValue(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(@NonNull Call<Employee> call, @NonNull Throwable t) {
                        resultLiveData.setValue(false);
                    }
                });

        return resultLiveData;
    }

    public LiveData<Boolean> updateEmployee(int employeeId, Employee employee) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        NetworkManager.getInstance().getApiService().updateEmployee(employeeId, employee)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Employee> call, @NonNull Response<Employee> response) {
                        resultLiveData.setValue(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(@NonNull Call<Employee> call, @NonNull Throwable t) {
                        resultLiveData.setValue(false);
                    }
                });

        return resultLiveData;
    }

    public LiveData<Boolean> deleteEmployee(int employeeId) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        NetworkManager.getInstance().getApiService().deleteEmployee(employeeId)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        resultLiveData.setValue(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        resultLiveData.setValue(false);
                    }
                });

        return resultLiveData;
    }
}