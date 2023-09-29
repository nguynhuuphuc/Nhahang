package com.example.nhahang;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nhahang.Adapters.MenuCategoryArrayAdapter;
import com.example.nhahang.Interfaces.IClickItemRecommendMenuCateListener;
import com.example.nhahang.Models.MenuCategoryModel;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.UserModel;
import com.example.nhahang.databinding.ActivityAddNewProductBinding;
import com.example.nhahang.databinding.ActivityEditProductBinding;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {

    private ActivityAddNewProductBinding binding;
    private MenuModel product;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean ignoreFormatting;
    private DecimalFormat decimalFormat;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String imgUrl;
    private FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();
    private Uri imageUri;
    private List<MenuCategoryModel> menuCategoryModelList;
    private MenuCategoryArrayAdapter menuCategoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        product = (MenuModel) getIntent().getSerializableExtra("product");
        viewData();
        setFormatMoney();



        binding.toolbar.setTitle("Sửa sản phẩm");

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUri = data.getData();
                            binding.imageProductIv.setImageURI(imageUri);
                        }
                        else {
                            Toast.makeText(EditProductActivity.this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                binding.getRoot().getWindowVisibleDisplayFrame(r);
                int screenHeight = binding.getRoot().getRootView().getHeight();
                int keyboardHeight = screenHeight - r.bottom;

                if (keyboardHeight > screenHeight * 0.15) {
                    // Bàn phím hiển thị (pop up)
                    // Thực hiện các hành động khi bàn phím hiển thị
                    binding.bottomButton.setVisibility(View.GONE);
                    if(binding.typeProductET.hasFocus()){
                        binding.recommendTypeRV.setVisibility(View.VISIBLE);
                        db.collection("menucategory").orderBy("id", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    menuCategoryModelList.clear();
                                    for (QueryDocumentSnapshot doc : task.getResult()){
                                        MenuCategoryModel model = doc.toObject(MenuCategoryModel.class);
                                        model.setDocumentId(doc.getId());
                                        if (model == null) continue;
                                        if (!model.getId().equals("00"))
                                            menuCategoryModelList.add(model);
                                    }
                                    menuCategoryAdapter.getFilter().filter(binding.typeProductET.getText().toString().trim());
                                }
                            }
                        });
                    }
                } else {
                    // Bàn phím ẩn (đóng)
                    // Thực hiện các hành động khi bàn phím ẩn
                    if(binding.nameProductEt.hasFocus()){
                        binding.nameProductEt.clearFocus();
                    }else if(binding.priceProductEt.hasFocus()){
                        binding.priceProductEt.clearFocus();
                    }else{
                        binding.typeProductET.clearFocus();
                    }
                    binding.recommendTypeRV.setVisibility(View.GONE);
                    Handler handler = new Handler();
                    int delayInMillis = 100; // Đặt độ trễ là 2000ms (2 giây)

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Thực hiện thay đổi sự hiển thị của view sau độ trễ
                            binding.bottomButton.setVisibility(View.VISIBLE); // Hoặc View.GONE, View.INVISIBLE tùy theo yêu cầu
                        }
                    }, delayInMillis);
                }
            }
        });

        menuCategoryModelList = new ArrayList<>();
        menuCategoryAdapter = new MenuCategoryArrayAdapter(menuCategoryModelList, new IClickItemRecommendMenuCateListener() {
            @Override
            public void onClickItem(MenuCategoryModel model) {
                binding.typeProductET.setText(model.getName());
                binding.typeProductET.clearFocus();
                binding.recommendTypeRV.setVisibility(View.GONE);
            }
        });
        binding.recommendTypeRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        binding.recommendTypeRV.setAdapter(menuCategoryAdapter);

        binding.typeProductET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.recommendTypeRV.setVisibility(View.VISIBLE);
                db.collection("menucategory").orderBy("id", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            menuCategoryModelList.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                MenuCategoryModel model = doc.toObject(MenuCategoryModel.class);
                                model.setDocumentId(doc.getId());
                                if (model == null) continue;
                                if (!model.getId().equals("00"))
                                    menuCategoryModelList.add(model);
                            }
                            menuCategoryAdapter.getFilter().filter(s.toString());
                        }
                    }
                });

            }
        });

        db.collection("menucategory").orderBy("id", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    menuCategoryModelList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        MenuCategoryModel model = doc.toObject(MenuCategoryModel.class);
                        model.setDocumentId(doc.getId());
                        if (model == null) continue;
                        if (!model.getId().equals("00"))
                            menuCategoryModelList.add(model);
                    }
                    menuCategoryAdapter.notifyDataSetChanged();
                }
            }
        });
        binding.imageProductIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
        binding.editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sName = binding.nameProductEt.getText().toString().trim();
                String sPrice = binding.priceProductEt.getText().toString().trim();
                String cateName = binding.typeProductET.getText().toString().trim();
                if(sName.isEmpty()){
                    binding.nameProductEt.setError("Tên sản phẩm không được bỏ trống!");
                    return;
                }
                if(sPrice.isEmpty()){
                    binding.priceProductEt.setError("Giá sản phẩm không được bỏ trống!");
                    return;
                }
                if(cateName.isEmpty()){
                    binding.typeProductET.setError("Loại sản phẩm không được bỏ trống!");
                    return;
                }
                inProgress(true);
                Map<String,Object> map = new HashMap<>();
                map.put("name",binding.nameProductEt.getText().toString().trim());
                map.put("price",binding.priceProductEt.getText().toString().trim());
                db.collection("menucategory").whereEqualTo("name",cateName)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    QuerySnapshot querySnapshots = task.getResult();
                                    if(querySnapshots.isEmpty()){
                                        db.collection("menucategory")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            List<MenuCategoryModel> models = new ArrayList<>();
                                                            for (QueryDocumentSnapshot doc : task.getResult()){
                                                                MenuCategoryModel model = doc.toObject(MenuCategoryModel.class);
                                                                models.add(model);
                                                            }
                                                            String id;
                                                            if(models.size() >= 10) id = String.valueOf(models.size());
                                                            else id = "0"+models.size();
                                                            Map<String,Object> map = new HashMap<>();
                                                            map.put("id","0"+ id);
                                                            map.put("name",cateName);
                                                            db.collection("menucategory").add(map);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
                if(imageUri != null){
                    StorageReference imgReference = FirebaseStorage.getInstance().getReference().child("product-picture/"+product.getName().replace(" ","-")+ "." + getFileExtension(imageUri));
                    imgReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imgUrl = uri.toString();
                                    map.put("img", imgUrl);
                                    db.collection("menu").document(product.getDocumentId())
                                            .update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    db.collection("menucategory").whereEqualTo("name",cateName)
                                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if(task.isSuccessful()){
                                                                        for (QueryDocumentSnapshot doc : task.getResult()){
                                                                            MenuCategoryModel model = doc.toObject(MenuCategoryModel.class);
                                                                            db.collection("menu").document(product.getDocumentId())
                                                                                    .update("type",model.getId())
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            dbRealtime.getReference("productManagementChange").setValue(true);
                                                                                            Toast.makeText(EditProductActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                                                                            inProgress(false);
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(EditProductActivity.this, "Cập nhật không thành công", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }

                            });
                        }
                    });
                }else{
                    db.collection("menu").document(product.getDocumentId())
                            .update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    db.collection("menucategory").whereEqualTo("name",cateName)
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        for (QueryDocumentSnapshot doc : task.getResult()){
                                                            MenuCategoryModel model = doc.toObject(MenuCategoryModel.class);
                                                            db.collection("menu").document(product.getDocumentId())
                                                                    .update("type",model.getId())
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            dbRealtime.getReference("productManagementChange").setValue(true);
                                                                            Toast.makeText(EditProductActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                                                            inProgress(false);
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditProductActivity.this, "Cập nhật không thành công", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        binding.priceProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ignoreFormatting) {
                    return;
                }
                ignoreFormatting = true;
                // Loại bỏ các ký tự phân tách hàng nghìn và thập phân
                String text = s.toString().replaceAll("[.,]", "");

                try {
                    // Chuyển đổi thành số double và định dạng lại theo kiểu tiền tệ Việt Nam
                    double amount = Double.parseDouble(text);
                    String formattedAmount = decimalFormat.format(amount);

                    // Cập nhật giá trị mới vào EditText
                    binding.priceProductEt.setText(formattedAmount);
                    binding.priceProductEt.setSelection(formattedAmount.length());
                } catch (NumberFormatException e) {
                    // Xảy ra lỗi khi chuyển đổi giá trị, không làm gì
                }
                ignoreFormatting = false;

            }
        });
    }



    private void viewData() {
        inProgress(true);
        Glide.with(this).load(product.getImg()).into(binding.imageProductIv);
        binding.nameProductEt.setText(product.getName());
        binding.priceProductEt.setText(product.getPrice());
        db.collection("menucategory").whereEqualTo("id",product.getType())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            MenuCategoryModel model = new MenuCategoryModel();
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                model = doc.toObject(MenuCategoryModel.class);
                                model.setDocumentId(doc.getId());
                            }
                            binding.typeProductET.setText(model.getName());
                        }
                    }
                });

        inProgress(false);
    }
    private void setFormatMoney() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("#,###",symbols);
    }

    private void inProgress(boolean isIn){
        if(isIn){
            binding.contentLl.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            return;
        }
        binding.contentLl.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.INVISIBLE);
    }
    private void selectPhoto() {
        Intent photoPicker = new Intent();
        photoPicker.setAction(Intent.ACTION_GET_CONTENT);
        photoPicker.setType("image/*");
        activityResultLauncher.launch(photoPicker);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}