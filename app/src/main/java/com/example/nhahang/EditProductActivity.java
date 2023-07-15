package com.example.nhahang;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.UserModel;
import com.example.nhahang.databinding.ActivityEditProductBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {

    private ActivityEditProductBinding binding;
    private MenuModel product;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean ignoreFormatting;
    private DecimalFormat decimalFormat;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String imgUrl;
    private FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        product = (MenuModel) getIntent().getSerializableExtra("product");
        viewData();
        setFormatMoney();



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
                inProgress(true);
                Map<String,Object> map = new HashMap<>();
                map.put("name",binding.nameProductEt.getText().toString().trim());
                map.put("price",binding.priceProductEt.getText().toString().trim());
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
                                                    Toast.makeText(EditProductActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                                    dbRealtime.getReference("productManagementChange").setValue(true);
                                                    inProgress(false);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(EditProductActivity.this, "Cập nhật không thành công", Toast.LENGTH_SHORT).show();
                                                    inProgress(false);
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
                                    Toast.makeText(EditProductActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    inProgress(false);
                                    dbRealtime.getReference("productManagementChange").setValue(true);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditProductActivity.this, "Cập nhật không thành công", Toast.LENGTH_SHORT).show();
                                    inProgress(false);
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