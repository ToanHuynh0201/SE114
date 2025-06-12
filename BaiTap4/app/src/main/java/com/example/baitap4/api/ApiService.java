package com.example.baitap4.api;

import com.example.baitap4.model.Employee;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/v1/employees")
    Call<List<Employee>> getEmployees();

    @GET("api/v1/employee/{id}")
    Call<Employee> getEmployee(@Path("id") int id);

    @POST("api/v1/create")
    Call<Employee> createEmployee(@Body Employee employee);

    @PUT("api/v1/update/{id}")
    Call<Employee> updateEmployee(@Path("id") int id, @Body Employee employee);

    @DELETE("api/v1/delete/{id}")
    Call<Void> deleteEmployee(@Path("id") int id);
}
