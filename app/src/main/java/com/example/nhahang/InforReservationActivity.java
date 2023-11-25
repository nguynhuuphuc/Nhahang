package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.OrderModel;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.databinding.ActivityInforReservationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InforReservationActivity extends AppCompatActivity {

    private ActivityInforReservationBinding binding;
    private String documentId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TableModel tableModel;
    private OrderModel orderModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInforReservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        inProgress(false);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        Intent intent = getIntent();
        tableModel = (TableModel) intent.getSerializableExtra("table");
        orderModel = (OrderModel) intent.getSerializableExtra("order");

        ApiService.apiService.getEmployee(new UserUidRequest(orderModel.getCreated_by()))
                .enqueue(new Callback<Employee>() {
                    @Override
                    public void onResponse(Call<Employee> call, Response<Employee> response) {
                        if(response.isSuccessful()){
                            Employee employee = response.body();
                            binding.staffName.setText(employee.getFull_name());
                        }
                    }

                    @Override
                    public void onFailure(Call<Employee> call, Throwable t) {

                    }
                });




        try {
            documentId = intent.getStringExtra("documentId");
            db.collection("reservations").document(documentId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                ReservationModel model = doc.toObject(ReservationModel.class);
                                assert model != null;
                                viewData(model);
                            }
                        }
                    });
        }catch (Exception ignored){
        }

    }

    private void inProgress(boolean isIn) {
        if(isIn){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.contentSv.setVisibility(View.GONE);
        }
        binding.progressBar.setVisibility(View.GONE);
        binding.contentSv.setVisibility(View.VISIBLE);
    }

    private void viewData(ReservationModel model) {
        inProgress(true);
        String time = model.getCurrentTime() + " " + model.getCurrentDate();
        binding.time.setText(time);
        UserUidRequest request = new UserUidRequest(model.getStaffId());
        ApiService.apiService.getEmployee(request).enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if(response.isSuccessful()){
                    Employee employee = response.body();
                    assert employee != null;
                    binding.staffName.setText(employee.getFull_name());
                    inProgress(false);
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                Toast.makeText(InforReservationActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });

    }

}