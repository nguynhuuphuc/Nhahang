package com.example.nhahang;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Models.UserModel;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityUserInformationManagementBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInformationManagementActivity extends AppCompatActivity {

    private ActivityUserInformationManagementBinding binding;
    private Employee employee;
    Calendar mCalendar = Calendar.getInstance();

    private FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    ActivityResultLauncher<Intent> activityResultLauncher;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserInformationManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        enableEdit(false);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUri = data.getData();
                            binding.avatar.setImageURI(imageUri);
                        }
                        else {
                            Toast.makeText(UserInformationManagementActivity.this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        employee = (Employee) getIntent().getSerializableExtra("user");
        setInformationView(employee);
        inProgress(false);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR,year);
                mCalendar.set(Calendar.MONTH,month);
                mCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel();
            }
        };
        binding.dateBornEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(UserInformationManagementActivity.this,date
                        ,mCalendar.get(Calendar.YEAR)
                        ,mCalendar.get(Calendar.MONTH)
                        ,mCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });

        binding.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
        binding.changeAvatarTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEdit(true);
            }
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEdit(false);
            }
        });
        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_uid = employee.getUser_uid();
                if(!employee.getFull_name().equals(binding.nameEt.getText().toString().trim()))
                    employee.setFull_name(binding.nameEt.getText().toString().trim());
                if(!employee.getEmail().equals(binding.emailEt.getText().toString().trim()))
                    employee.setEmail(binding.emailEt.getText().toString().trim());
                String dateString = binding.dateBornEt.getText().toString().trim();
                // Define a DateTimeFormatter for the specified format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                try {
                    // Parse the date string into a LocalDate
                    LocalDate parsedDate = LocalDate.parse(dateString, formatter);
                    Instant instant = parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
                    if(!employee.getDate_of_birth().equals(Date.from(instant)))
                        employee.setDate_of_birth(Date.from(instant));
                    // Now you can work with the parsed LocalDate object
                    System.out.println("Parsed Date: " + parsedDate);

                    // You can also format the LocalDate object back to a string
                    String formattedDate = parsedDate.format(formatter);
                    System.out.println("Formatted Date: " + formattedDate);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error parsing date string.");
                }

                RadioButton sexChecked = null;
                if (binding.maleRb.isChecked()) {
                    sexChecked = binding.maleRb;
                }
                if (binding.femaleRb.isChecked()) {
                    sexChecked = binding.femaleRb;
                }
                if (binding.ortherRb.isChecked()) {
                    sexChecked = binding.ortherRb;
                }
                assert sexChecked != null;
                if(!employee.getGender().equals(sexChecked.getText().toString().trim()))
                    employee.setGender(sexChecked.getText().toString().trim());

                inProgress(true);
                enableEdit(false);
                if (imageUri != null) {
                    StorageReference imgReference = storageRef.child("user/" + employee.getUser_uid() + "." + getFileExtension(imageUri));

                    imgReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    employee.setAvatar(uri.toString());
                                    ApiService.apiService.updateEmployee(employee).enqueue(new Callback<ServerResponse>() {
                                        @Override
                                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                            if(response.isSuccessful()) {
                                                ServerResponse serverResponse = response.body();
                                                Toast.makeText(UserInformationManagementActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                                reloadUserInfo(user_uid);
                                                inProgress(false);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                                            Toast.makeText(UserInformationManagementActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                            reloadUserInfo(user_uid);
                                            inProgress(false);

                                        }
                                    });
                                }

                            });
                        }
                    });
                }
                else
                {
                    ApiService.apiService.updateEmployee(employee).enqueue(new Callback<ServerResponse>() {
                        @Override
                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                            if(response.isSuccessful()) {
                                ServerResponse serverResponse = response.body();
                                Toast.makeText(UserInformationManagementActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                reloadUserInfo(user_uid);
                                inProgress(false);
                            }

                        }
                        @Override
                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                            Toast.makeText(UserInformationManagementActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                            reloadUserInfo(user_uid);
                            inProgress(false);
                        }
                    });
                }
            }

        });
    }
    private void reloadUserInfo(String user_uid){
        UserUidRequest userUidRequest = new UserUidRequest(user_uid);
        ApiService.apiService.getEmployee(userUidRequest).enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if(response.isSuccessful()){
                    employee = response.body();
                    setInformationView(employee);
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {

            }
        });
    }


    private void setInformationView(Employee employee) {
        binding.nameTv.setText(employee.getFull_name());
        binding.nameEt.setText(employee.getFull_name());
        binding.phoneEt.setText(employee.getPhone_number());
        binding.emailEt.setText(employee.getEmail());
        if(employee.getDate_of_birth() != null){
            Util.updateDateLabel(binding.dateBornEt,employee.getDate_of_birth());
        }

        switch (employee.getGender()){
            case "Nam":
                binding.maleRb.setChecked(true);
                break;
            case "Nữ":
                binding.femaleRb.setChecked(true);
                break;
            default:
                binding.ortherRb.setChecked(true);
                break;
        }
        if(employee.getAvatar() !=null && !employee.getAvatar().isEmpty()){
            Glide.with(this).load(employee.getAvatar()).into(binding.avatar);
        }

    }

    void enableEdit(boolean b) {
        int viewEdit = View.VISIBLE;
        int viewSave,viewCancel;
        viewSave = viewCancel = View.GONE;

        if(b) {
            viewEdit = View.GONE;
            viewSave = viewCancel = View.VISIBLE;
        }
        binding.edit.setVisibility(viewEdit);
        binding.save.setVisibility(viewSave);
        binding.cancel.setVisibility(viewCancel);

        enableInput(b);

    }

    void enableInput(boolean b) {
        int borderEditText = R.drawable.edit_text_rounded_corner_border_black;
        int nonBorderEditText = R.drawable.edit_text_rounded_corner;
        if(b){
            binding.nameEt.setElevation(5);
            //binding.phoneEt.setElevation(5);
            binding.emailEt.setElevation(5);
            binding.dateBornEt.setElevation(5);



            binding.nameEt.setBackgroundResource(borderEditText);
            //binding.phoneEt.setBackgroundResource(borderEditText);
            binding.emailEt.setBackgroundResource(borderEditText);
            binding.dateBornEt.setBackgroundResource(borderEditText);


            binding.changeAvatarTv.setVisibility(View.VISIBLE);
        }else{
            binding.nameEt.setElevation(0);
            binding.phoneEt.setElevation(0);
            binding.emailEt.setElevation(0);
            binding.dateBornEt.setElevation(0);

            binding.nameEt.setBackgroundResource(nonBorderEditText);
            binding.phoneEt.setBackgroundResource(nonBorderEditText);
            binding.emailEt.setBackgroundResource(nonBorderEditText);
            binding.dateBornEt.setBackgroundResource(nonBorderEditText);

            binding.changeAvatarTv.setVisibility(View.GONE);
        }
        binding.nameEt.setEnabled(b);
        binding.phoneEt.setEnabled(false);
        binding.emailEt.setEnabled(b);
        binding.dateBornEt.setEnabled(b);
        binding.maleRb.setEnabled(b);
        binding.femaleRb.setEnabled(b);
        binding.ortherRb.setEnabled(b);
        binding.avatar.setEnabled(b);
    }

    private void updateLabel() {
        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        binding.dateBornEt.setText(dateFormat.format(mCalendar.getTime()));
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void selectPhoto() {
        Intent photoPicker = new Intent();
        photoPicker.setAction(Intent.ACTION_GET_CONTENT);
        photoPicker.setType("image/*");
        activityResultLauncher.launch(photoPicker);
    }

    private void inProgress(boolean isInProgress) {
        if(isInProgress){
            binding.progressbar.setVisibility(View.VISIBLE);
            binding.informationLL.setVisibility(View.GONE);
            binding.avatar.setVisibility(View.GONE);
            return;
        }
        binding.informationLL.setVisibility(View.VISIBLE);
        binding.avatar.setVisibility(View.VISIBLE);
        binding.progressbar.setVisibility(View.GONE);
    }

}