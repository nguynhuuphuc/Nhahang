package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Adapters.ReservationDetailAdapter;
import com.example.nhahang.BottomSheetDialogFragments.NoteProductInReservationFragment;
import com.example.nhahang.BottomSheetDialogFragments.WarningDeleteItemFragment;
import com.example.nhahang.Interfaces.IClickItemReservationDetail;
import com.example.nhahang.Interfaces.IClickViewInNoteProductListeners;
import com.example.nhahang.Interfaces.IClickViewInWaringDelete;
import com.example.nhahang.Models.ProductInReservationModel;
import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.databinding.ActivityReservationDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReservationDetailActivity extends AppCompatActivity {
    private ActivityReservationDetailBinding binding;
    private TableModel tableModel;
    private List<ProductInReservationModel> productList;
    private ReservationDetailAdapter reservationDetailAdapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DecimalFormat decimalFormat;
    private final FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();
    private boolean ignoreGetChange = false;
    private AlertDialog dialog;
    private String oldPrice = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setFormatMoney();
        buildDialog();

        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String newPrice = binding.totalPrice.getText().toString().trim();
                    if(!oldPrice.equals("") && !oldPrice.equals(newPrice)){
                        dialog.show();
                    }else{
                        onBackPressed();
                        supportFinishAfterTransition();
                    }
                }catch (Exception e){
                    onBackPressed();
                    supportFinishAfterTransition();
                }

            }

        });

        Intent intent = getIntent();
        tableModel = (TableModel) intent.getSerializableExtra("table");
        binding.tableId.setText(tableModel.getId());
        try {
            String sActivity = intent.getStringExtra("activity");
            if (sActivity.equals("Home")){
                binding.thongBao.setEnabled(true);
                binding.thongBao.setBackgroundResource(R.drawable.radius_sky_blue_background);
            }
        }catch (Exception ignored){}

        try {
            oldPrice = intent.getStringExtra("oldPrice");
        }catch (Exception ignored){}

        dbRealtime.getReference("changeReservationDetail").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isChanged = snapshot.getValue(Boolean.class);
                if(Boolean.TRUE.equals(isChanged)){
                    binding.thongBao.setBackgroundResource(R.drawable.radius_sky_blue_background);
                    binding.thongBao.setEnabled(true);
                    getProduct();
                    updateReservation();
                    dbRealtime.getReference("notifyKitchen").setValue(true);
                    dbRealtime.getReference("changeReservationDetail").setValue(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.thanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationDetailActivity.this,PaymentActivity.class);
                intent.putExtra("tableModel",tableModel);
                startActivity(intent);
            }
        });

        binding.xemTamTinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationDetailActivity.this,TemporaryPaymentActivity.class);
                intent.putExtra("tableModel",tableModel);
                startActivity(intent);
            }
        });
        binding.thongBao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.thongBao.setBackgroundResource(R.drawable.radius_light_blue_background);
                onBackPressed();
                supportFinishAfterTransition();
            }
        });

        binding.floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationDetailActivity.this,SelectProductActitvity.class);
                intent.putExtra("table",tableModel);
                intent.putExtra("activity","ReservationDetail");
                startActivity(intent);
            }
        });

        binding.productsRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        productList = new ArrayList<>();
        reservationDetailAdapter = new ReservationDetailAdapter(productList, this, new IClickItemReservationDetail() {
            @Override
            public void onClickItem(ProductInReservationModel model, String command) {
                if(command == null){
                    NoteProductInReservationFragment fragment = new NoteProductInReservationFragment(ReservationDetailActivity.this, model, new IClickViewInNoteProductListeners() {
                        @Override
                        public void onClickListener(ProductInReservationModel pModel,String command) {
                            switch (command){
                                case SAVED_NOTE:
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("productDiscountUnint",pModel.isProductDiscountUnint());
                                    map.put("productDiscountValue",pModel.getProductDiscountValue());
                                    map.put("productNote", pModel.getProductNote());
                                    map.put("productQuantity",pModel.getProductQuantity());
                                    map.put("productTotalPrice",pModel.getProductTotalPrice());
                                    db.collection("reservationDetail").document(tableModel.getDocumentId())
                                            .collection("products").document(pModel.getDocumentId())
                                            .update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    dbRealtime.getReference("updateRes").setValue(true);
                                                    updateReservation();
                                                    binding.thongBao.setBackgroundResource(R.drawable.radius_sky_blue_background);
                                                    binding.thongBao.setEnabled(true);
                                                    reservationDetailAdapter.notifyDataSetChanged();
                                                    InProgress(false);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ReservationDetailActivity.this, "Cập nhật không thành công, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                                                    InProgress(false);
                                                }
                                            });
                                    

                            }
                        }
                    });
                    fragment.show(getSupportFragmentManager(),fragment.getTag());
                }else{
                    switch (command){
                        case REMOVE_ITEM:
                            WarningDeleteItemFragment fragment = new WarningDeleteItemFragment(new IClickViewInWaringDelete() {
                                @Override
                                public void onClick(String command) {
                                    switch (command){
                                        case ACCEPT:
                                            InProgress(true);
                                            db.collection("reservationDetail").document(tableModel.getDocumentId())
                                                    .collection("products").document(model.getDocumentId())
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            productList.removeIf(product -> product.getDocumentId().equals(model.getDocumentId()));
                                                            updateReservation();
                                                            reservationDetailAdapter.notifyDataSetChanged();
                                                            binding.thongBao.setEnabled(true);
                                                            binding.thongBao.setBackgroundResource(R.drawable.radius_sky_blue_background);
                                                            dbRealtime.getReference("notifyKitchen").setValue(true);
                                                            InProgress(false);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            InProgress(false);
                                                            Toast.makeText(ReservationDetailActivity.this, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                    }
                                }
                            });
                            fragment.show(getSupportFragmentManager(),fragment.getTag());
                    }
                }
            }
        });
        binding.productsRv.setAdapter(reservationDetailAdapter);
        getProduct();
        getDateTotalPriceAndQuantity();

        dbRealtime.getReference("donePayment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean ischange = snapshot.getValue(Boolean.class);
                if(Boolean.TRUE.equals(ischange)){
                    dbRealtime.getReference("donePayment").setValue(false);
                    db.collection("reservations").document(tableModel.getDocumentId()).delete();
                    db.collection("reservationDetail").document(tableModel.getDocumentId())
                            .collection("products")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for (QueryDocumentSnapshot doc : task.getResult()){
                                            doc.getReference().delete();
                                        }
                                    }
                                }
                            });
                    db.collection("tables").document(tableModel.getDocumentId())
                            .update("status","");
                    finish();
                    dbRealtime.getReference("changedResTable").setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReservationDetailActivity.this);
        builder.setMessage("Bạn có muốn thông báo cho bếp không?")
                .setTitle("Thoát");
        builder.setPositiveButton("Thông báo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
                supportFinishAfterTransition();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
                supportFinishAfterTransition();
            }
        });
        dialog = builder.create();
    }


    private void getProduct(){
        InProgress(true);
        db.collection("reservationDetail").document(tableModel.getDocumentId())
                .collection("products").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            productList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                ProductInReservationModel model = document.toObject(ProductInReservationModel.class);
                                model.setDocumentId(document.getId());
                                productList.add(model);
                            }
                            reservationDetailAdapter.notifyDataSetChanged();
                            InProgress(false);
                        }
                    }
                });
    }

    private void updateReservation(){
        InProgress(true);
        db.collection("reservationDetail").document(tableModel.getDocumentId())
                .collection("products").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        double totalPrice = 0;
                        int quantity = 0;
                        for (ProductInReservationModel p : productList){
                            totalPrice += Double.parseDouble(p.getProductTotalPrice().replace(",","").trim());
                            quantity += Double.parseDouble(p.getProductQuantity());
                        }
                        Map<String, Object> map = new HashMap<>();
                        map.put("totalQuantity",String.valueOf(quantity));
                        map.put("totalPrice",decimalFormat.format(totalPrice));
                        db.collection("reservations").document(tableModel.getDocumentId())
                                .update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        getDateTotalPriceAndQuantity();
                                        InProgress(false);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        InProgress(false);
                                    }
                                });
                    }
                });
    }

    private void getDateTotalPriceAndQuantity(){
        InProgress(true);
        db.collection("reservations").document(tableModel.getDocumentId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            ReservationModel reModel = doc.toObject(ReservationModel.class);
                            if(reModel != null){
                                binding.totalPrice.setText(reModel.getTotalPrice());
                                binding.quantity.setText(reModel.getTotalQuantity());
                                InProgress(false);
                            }

                        }
                    }
                });
    }
    private void setFormatMoney() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("#,###",symbols);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbRealtime.getReference("changeReservationDetail").setValue(false);
        dbRealtime.getReference("changedResTable").setValue(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more_reservation_detail,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.infoReseration:
                Intent intent = new Intent(this,InforReservationActivity.class);
                intent.putExtra("documentId",tableModel.getDocumentId());
                startActivity(intent);
                return true;
            case R.id.hisNotifyRes:
                startActivity(new Intent(this,HisNotifyResActivity.class));
                return true;
            case R.id.cancelReservation:
                WarningDeleteItemFragment fragment = new WarningDeleteItemFragment(new IClickViewInWaringDelete() {
                    @Override
                    public void onClick(String command) {
                        switch (command){
                            case ACCEPT:
                                db.collection("reservations").document(tableModel.getDocumentId()).delete();
                                db.collection("reservationDetail").document(tableModel.getDocumentId())
                                                .collection("products")
                                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    for (QueryDocumentSnapshot doc : task.getResult()){
                                                        doc.getReference().delete();
                                                    }
                                                }
                                            }
                                        });
                                db.collection("tables").document(tableModel.getDocumentId())
                                        .update("status","");
                                finish();
                                dbRealtime.getReference("changedResTable").setValue(true);
                        }
                    }
                });
                fragment.show(getSupportFragmentManager(),fragment.getTag());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void InProgress(boolean isIn){
        int viewProgress = View.INVISIBLE;
        int viewOrther = View.VISIBLE;
        if(isIn){
            viewOrther = View.INVISIBLE;
            viewProgress = View.VISIBLE;
        }
        
        binding.ProgressBar.setVisibility(viewProgress);
        binding.contentRl.setVisibility(viewOrther);
        binding.quantity.setVisibility(viewOrther);
        binding.totalPrice.setVisibility(viewOrther);
        
    }
}