package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.databinding.ActivityPaymentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    private TableModel tableModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean discountUnit;
    private DecimalFormat decimalFormat;
    private boolean ignoreFormatting;
    private FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setFormatMoney();

        Intent intent = getIntent();
        tableModel = (TableModel) intent.getSerializableExtra("tableModel");

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        binding.discountEt.addTextChangedListener(new TextWatcher() {
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

                if(!discountUnit){
                    // Loại bỏ các ký tự phân tách hàng nghìn và thập phân
                    String text = s.toString().replaceAll("[.,]", "");

                    try {
                        // Chuyển đổi thành số double và định dạng lại theo kiểu tiền tệ Việt Nam
                        double amount = Double.parseDouble(text);
                        String formattedAmount = decimalFormat.format(amount);

                        // Cập nhật giá trị mới vào EditText
                        binding.discountEt.setText(formattedAmount);
                        binding.discountEt.setSelection(formattedAmount.length());
                    } catch (NumberFormatException e) {
                        // Xảy ra lỗi khi chuyển đổi giá trị, không làm gì
                    }
                }
                ignoreFormatting = false;

            }
        });

        binding.customerPayEt.addTextChangedListener(new TextWatcher() {
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
                    binding.customerPayEt.setText(formattedAmount);
                    binding.customerPayEt.setSelection(formattedAmount.length());
                } catch (NumberFormatException e) {
                    // Xảy ra lỗi khi chuyển đổi giá trị, không làm gì
                }
                ignoreFormatting = false;

            }
        });

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
                    binding.doneCv.setVisibility(View.GONE);
                } else {
                    // Bàn phím ẩn (đóng)
                    // Thực hiện các hành động khi bàn phím ẩn

                    Handler handler = new Handler();
                    int delayInMillis = 100; // Đặt độ trễ là 2000ms (2 giây)

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Thực hiện thay đổi sự hiển thị của view sau độ trễ
                            binding.doneCv.setVisibility(View.VISIBLE); // Hoặc View.GONE, View.INVISIBLE tùy theo yêu cầu
                        }
                    }, delayInMillis);
                }
            }
        });

        db.collection("reservations").document(tableModel.getDocumentId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        ReservationModel model = doc.toObject(ReservationModel.class);
                        if(model == null) return;
                        viewPayment(model);

                    }
                });

        binding.toggleGroupDiscount.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                double dTotalPr = Double.parseDouble(binding.totalPrice.getText().toString().replace(",",""));
                double dNeedTP = Double.parseDouble(binding.needToPay.getText().toString().replace(",",""));
                if(binding.btnPercent.isChecked()){
                    int percent = (int) (((dTotalPr - dNeedTP)/dTotalPr)*100);
                    if(percent != 0)
                        binding.discountEt.setText(String.valueOf(percent));
                    discountUnit = true;
                }else{
                    if((dTotalPr - dNeedTP) != 0)
                        binding.discountEt.setText(decimalFormat.format(dTotalPr - dNeedTP));
                    discountUnit = false;
                }
            }
        });
        binding.customerPayEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(binding.customerPayEt.getText().toString().trim().isEmpty())
                        binding.customerPayEt.setText("0");

                    binding.moneyChangeTv.setText(calculateMoneyChange(
                            binding.needToPay.getText().toString().trim(),
                            binding.customerPayEt.getText().toString().trim()));
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.customerPayEt.getWindowToken(), 0);
                    binding.customerPayEt.clearFocus();
                    return true;
                }

                return false;
            }
        });
        binding.doneCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = new GregorianCalendar();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String currentDate = dateFormat.format(c.getTime());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                String currentTime = timeFormat.format(c.getTime());
                Map<String,Object> map = new HashMap<>();
                map.put("discountUnit",discountUnit);
                map.put("discountValue",binding.discountEt.getText().toString().trim());
                map.put("customerPay",binding.customerPayEt.getText().toString().trim());
                map.put("tableId",tableModel.getDocumentId());
                map.put("timeCheckOut",c.getTime().toString());
                map.put("totalPrice",binding.totalPrice.getText().toString());
                map.put("price",binding.needToPay.getText().toString());
                map.put("theChange",binding.moneyChangeTv.getText().toString());
                map.put("paymentUnit","Tiền mặt");
                db.collection("reservations").document(tableModel.getDocumentId())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                ReservationModel model = documentSnapshot.toObject(ReservationModel.class);
                                if(model == null) return;
                                map.put("timeCheckIn",model.getCurrentTime()+" " + model.getCurrentDate());
                                db.collection("paymentHis").add(map).addOnSuccessListener(
                                        new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(PaymentActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                                                dbRealtime.getReference("donePayment").setValue(true);
                                                finish();
                                            }
                                        }
                                );
                            }
                        });
            }
        });

        binding.discountEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){


                    binding.needToPay.setText(
                            calculateNeedToPay(
                                    binding.discountEt.getText().toString().trim(),
                                    binding.totalPrice.getText().toString().trim()
                            )
                    );
                    if(binding.customerPayEt.getText().toString().trim().isEmpty())
                        binding.customerPayEt.setText("0");

                    binding.moneyChangeTv.setText(calculateMoneyChange(
                            binding.needToPay.getText().toString().trim(),
                            binding.customerPayEt.getText().toString().trim()
                    ));

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.discountEt.getWindowToken(), 0);
                    binding.discountEt.clearFocus();
                    return true;
                }

                return false;
            }
        });


    }

    private void viewPayment(ReservationModel model) {
        binding.quantity.setText(model.getTotalQuantity());
        binding.totalPrice.setText(model.getTotalPrice());
        if(binding.discountEt.getText().toString().trim().isEmpty())
            binding.needToPay.setText(model.getTotalPrice());
        binding.customerPayEt.setText(model.getTotalPrice());

        binding.moneyChangeTv.setText(calculateMoneyChange(
                binding.needToPay.getText().toString().trim(),
                binding.customerPayEt.getText().toString().trim()));

    }
    private String calculateMoneyChange(String needToPay, String customerPay){
        double dNeedTP = Double.parseDouble(needToPay.replace(",",""));
        double dCusP = Double.parseDouble(customerPay.replace(",",""));
        return decimalFormat.format(dCusP - dNeedTP);
    }
    private void setFormatMoney() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("#,###",symbols);
    }



    private String calculateNeedToPay(String discountV, String totalPrice){
        double dTotalPr = Double.parseDouble(totalPrice.replace(",",""));
        if(discountUnit){
            try {
                int iDiscV = Integer.parseInt(discountV);
                return decimalFormat.format(dTotalPr - (dTotalPr * iDiscV / 100));
            }catch (Exception e){
                return totalPrice;
            }

        }
        try {
            double dDiscV = Double.parseDouble(discountV.replace(",",""));
            if( dDiscV >= dTotalPr ) {
                binding.discountEt.setText(totalPrice);
                return "0";
            }
            return decimalFormat.format(dTotalPr - dDiscV);
        }catch (Exception e){
            return totalPrice;
        }
    }
}