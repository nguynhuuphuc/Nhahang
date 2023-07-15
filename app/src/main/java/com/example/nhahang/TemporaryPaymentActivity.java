package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Adapters.ItemInTemporaryPaymentAdapter;
import com.example.nhahang.Models.ProductInReservationModel;
import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.databinding.ActivityTemporaryPaymentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TemporaryPaymentActivity extends AppCompatActivity {

    private ActivityTemporaryPaymentBinding binding;
    private TableModel tableModel;
    private List<ProductInReservationModel> productList;
    private ItemInTemporaryPaymentAdapter itemAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTemporaryPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        tableModel = (TableModel) intent.getSerializableExtra("tableModel");

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


        binding.contentRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        productList = new ArrayList<>();
        itemAdapter = new ItemInTemporaryPaymentAdapter(productList);

        db.collection("reservationDetail").document(tableModel.getDocumentId())
                        .collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                ProductInReservationModel model = doc.toObject(ProductInReservationModel.class);
                                productList.add(model);
                            }
                            itemAdapter.notifyDataSetChanged();
                        }
                    }
                });
        db.collection("reservations").document(tableModel.getDocumentId())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        ReservationModel model  = doc.toObject(ReservationModel.class);
                        if(model == null) return;
                        binding.quantity.setText(model.getTotalQuantity());
                        binding.totalPrice.setText(model.getTotalPrice());
                        binding.totalPricePayment.setText(model.getTotalPrice());
                    }
                });

        binding.contentRv.setAdapter(itemAdapter);
    }
}