package com.example.nhahang.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.LoginActivity;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Models.UserModel;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.R;

import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.MySharedPreferences;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.FragmentUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class UserFragment extends Fragment {
    private FragmentUserBinding binding;
    Calendar mCalendar = Calendar.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    ActivityResultLauncher<Intent> activityResultLauncher;

    Uri imageUri;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
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
                            Toast.makeText(getContext(), "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


        inProgress(true);
        AuthSigined();

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



        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> user = new HashMap<>();
                Employee employee = new Employee();
                employee.setUser_uid(Auth.User_Uid);
                employee.setFull_name(binding.nameEt.getText().toString().trim());
                employee.setEmail(binding.emailEt.getText().toString().trim());
                String dateString = binding.dateBornEt.getText().toString().trim();
                // Define a DateTimeFormatter for the specified format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                try {
                    // Parse the date string into a LocalDate
                    LocalDate parsedDate = LocalDate.parse(dateString, formatter);
                    Instant instant = parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
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
                employee.setGender(sexChecked.getText().toString().trim());

                inProgress(true);
                enableEdit(false);
                if (imageUri != null) {
                    Toast.makeText(getContext(), imageUri.toString(), Toast.LENGTH_SHORT).show();
                    StorageReference imgReference = storageRef.child("user/employees/" + Auth.User_Uid + "." + getFileExtension(imageUri));
                    imgReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    employee.setAvatar(uri.toString());
                                    ApiService.apiService.updateEmployee(employee).enqueue(new Callback<Employee>() {
                                        @Override
                                        public void onResponse(Call<Employee> call, Response<Employee> response) {
                                            if(response.isSuccessful()) {
                                                Employee serverResponse = response.body();
                                                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                                AuthSigined();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Employee> call, Throwable t) {
                                            Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            });
                        }
                    });
                }
                else
                {

                    ApiService.apiService.updateEmployee(employee).enqueue(new Callback<Employee>() {
                        @Override
                        public void onResponse(Call<Employee> call, Response<Employee> response) {
                            if(response.isSuccessful()) {
                                Employee serverResponse = response.body();
                                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                AuthSigined();
                            }

                        }
                        @Override
                        public void onFailure(Call<Employee> call, Throwable t) {
                            Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

        });

        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEdit(false);
                inProgress(true);
                AuthSigined();
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR,year);
                mCalendar.set(Calendar.MONTH,month);
                mCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel();
            }
        };

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(auth != null){
                    auth.signOut();
                }
                Auth.SetNull();
                MySharedPreferences.saveUserUid(getContext(),null);
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });

        binding.dateBornEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(),date
                        ,mCalendar.get(Calendar.YEAR)
                        ,mCalendar.get(Calendar.MONTH)
                        ,mCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });


        return view;
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
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

    private void AuthSigined() {
            ApiService.apiService.getEmployee(new UserUidRequest(Auth.User_Uid)).enqueue(new Callback<Employee>() {
                @Override
                public void onResponse(Call<Employee> call, Response<Employee> response) {
                    if(response.isSuccessful()){
                        Employee employee = response.body();
                        setInformationView(employee);
                        inProgress(false);
                    } else {
                        // Request failed, handle the error
                        int statusCode = response.code();
                        // Check the status code for specific error handling
                        switch (statusCode){
                            case 500:
                                break;
                            case 401:
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<Employee> call, Throwable t) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
            });
    }

    private void setInformationView(Employee employee) {
        binding.nameTv.setText(employee.getFull_name());
        binding.nameEt.setText(employee.getFull_name());
        binding.phoneEt.setText(Auth.PhoneNumber);
        binding.emailEt.setText(employee.getEmail());
        if(employee.getDate_of_birth() != null)
            Util.updateDateLabel(binding.dateBornEt,employee.getDate_of_birth());
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
        if(employee.getAvatar() != null && !employee.getAvatar().isEmpty()){
            Glide.with(getContext()).load(employee.getAvatar()).into(binding.avatar);
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

            binding.emailEt.setElevation(5);
            binding.dateBornEt.setElevation(5);



            binding.nameEt.setBackgroundResource(borderEditText);

            binding.emailEt.setBackgroundResource(borderEditText);
            binding.dateBornEt.setBackgroundResource(borderEditText);

            binding.logout.setVisibility(View.GONE);
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

            binding.logout.setVisibility(View.VISIBLE);
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
}
