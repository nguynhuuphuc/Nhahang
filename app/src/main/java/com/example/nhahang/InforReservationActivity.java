package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.UserModel;
import com.example.nhahang.databinding.ActivityInforReservationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InforReservationActivity extends AppCompatActivity {

    private ActivityInforReservationBinding binding;
    private String documentId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInforReservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        Intent intent = getIntent();
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
    }

    private void viewData(ReservationModel model) {
        String time = model.getCurrentTime() + " " + model.getCurrentDate();
        binding.time.setText(time);

        db.collection("users").document(model.getStaffId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            UserModel user = doc.toObject(UserModel.class);
                            assert user != null;
                            binding.staffName.setText(user.getName());
                        }
                    }
                });

    }
}