package com.example.nhahang;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.OrderModel;
import com.example.nhahang.Models.PaymentMethodModel;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityPaymentBinding;
import com.example.nhahang.databinding.DialogImageBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.vnpay.authentication.VNP_AuthenticationActivity;
import com.vnpay.authentication.VNP_SdkCompletedCallback;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    private TableModel tableModel;
    private boolean discountUnit;
    private DecimalFormat decimalFormat;
    private boolean ignoreFormatting;
    private double orderTotalAmount;
    private int orderTotalQuantity;
    private OrderModel orderModel;
    private ActivityResultLauncher<Intent> launcher;
    private String vnpayUrl;
    private String paymentMethod = "CASH";
    private int paymentMethodId = 1; // 1: Tiền mặt (Mặc định) , 2: VNPAY, 3: Chuyển khoản
    private MyApplication mApp;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getVnpayUrl() {
        return vnpayUrl;
    }

    public void setVnpayUrl(String vnpayUrl) {
        this.vnpayUrl = vnpayUrl;
    }

    public int getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(int paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mApp = (MyApplication) getApplication();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == RESULT_OK){
                    Intent data = o.getData();
                    assert data != null;
                    setPaymentMethod(data.getStringExtra("paymentMethod"));
                    switch (getPaymentMethod()){
                        case "CASH":
                            setViewCashMethod();
                            setPaymentMethodId(1);
                            break;
                        case "VNPAY":
                            setViewQRMethod();
                            setPaymentMethodId(2);
                            break;

                        case "EXCHANGE":
                            setViewExchangeMethod();
                            setPaymentMethodId(3);
                            break;
                    }

                }
            }
        });

        setFormatMoney();

        Intent intent = getIntent();
        tableModel = (TableModel) intent.getSerializableExtra("table");

        orderModel = (OrderModel) intent.getSerializableExtra("order");
        orderTotalAmount = intent.getDoubleExtra("orderTotalAmount",0);
        orderTotalQuantity = intent.getIntExtra("orderTotalQuantity",0);

        viewPayment();


        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.discountEt.getText().toString().isEmpty()){
                if(binding.btnVnd.isChecked()){
                    double discount_amount = Double.parseDouble(binding.discountEt.getText().toString().trim().replaceAll("[,.₫]",""));
                    orderModel.setDiscount_amount(discount_amount);
                }
                if(binding.btnPercent.isChecked()){
                    int discount_percentage = Integer.parseInt(binding.discountEt.getText().toString().trim());
                    orderModel.setDiscount_percent(discount_percentage);
                }
                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra("response","PaymentActivity");
                resultIntent.putExtra("orderModel",orderModel);
                setResult(RESULT_OK,resultIntent);
                getOnBackPressedDispatcher().onBackPressed();
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
                if(binding.btnVnd.isChecked()){
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
                            if(amount <= orderTotalAmount){
                                String formattedAmount = decimalFormat.format(amount);

                                // Cập nhật giá trị mới vào EditText
                                binding.discountEt.setText(formattedAmount);
                                binding.discountEt.setSelection(formattedAmount.length());
                                orderModel.setDiscount_amount(amount);
                            }else {
                                String formattedAmount = decimalFormat.format(orderTotalAmount);
                                orderModel.setDiscount_amount(orderTotalAmount);
                                binding.discountEt.setText(formattedAmount);
                                binding.discountEt.setSelection(formattedAmount.length());
                            }

                        } catch (NumberFormatException e) {
                            // Xảy ra lỗi khi chuyển đổi giá trị, không làm gì
                        }
                    }
                    ignoreFormatting = false;
                }else if(!s.toString().isEmpty()){
                    int percent = Integer.parseInt(s.toString());
                    orderModel.setDiscount_percent(percent);
                    if(percent > 100){
                        binding.discountEt.setText("100");
                        binding.discountEt.setSelection(3);
                    }
                }

            }
        });
        binding.needToPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String discount = binding.discountEt.getText().toString();
                if(discount.isEmpty() || discount.equals("0")){
                    return;
                }
                if(binding.btnVnd.isChecked()){
                    String discount_amount = discount.trim().replaceAll("[.,]", "");
                    double amount = Double.parseDouble(discount_amount);
                    orderModel.setDiscount_amount(amount);
                }
                if(binding.btnPercent.isChecked()){
                    String discount_percentage = discount.trim();
                    int percentage = Integer.parseInt(discount_percentage);
                    orderModel.setDiscount_percent(percentage);
                }
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
                    if(binding.discountEt.hasFocus())
                        binding.discountEt.onEditorAction(EditorInfo.IME_ACTION_DONE);



                    if(binding.customerPayEt.hasFocus())
                        binding.customerPayEt.onEditorAction(EditorInfo.IME_ACTION_DONE);


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

        binding.paymentMethodCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, PaymentMethodsActivity.class);
                launcher.launch(intent);
            }
        });

        binding.toggleGroupDiscount.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                double dTotalPr = Double.parseDouble(binding.totalPrice.getText().toString().replaceAll("[,.₫]",""));
                double dNeedTP = Double.parseDouble(binding.needToPay.getText().toString().replaceAll("[,.₫]",""));
                if(binding.btnPercent.isChecked()){
                    int percent = (int) (((dTotalPr - dNeedTP)/dTotalPr)*100);
                    if(percent != 0){
                        binding.discountEt.setText(String.valueOf(percent));
                        binding.discountEt.setSelection(String.valueOf(percent).length());
                    }

                    discountUnit = true;
                }else{
                    if((dTotalPr - dNeedTP) != 0){
                        binding.discountEt.setText(decimalFormat.format(dTotalPr - dNeedTP));
                        binding.discountEt.setSelection(decimalFormat.format(dTotalPr - dNeedTP).length());
                    }

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
                switch (getPaymentMethod()){
                    case "CASH":
                        updatePaidOrder();
                        setPaymentMethodId(1);
                        break;
                    case "VNPAY":
                        openGateWayVNPay();
                        setPaymentMethodId(2);

                        break;
                    case "EXCHANGE":
                        getQRExchange();
                        setPaymentMethodId(3);
                        break;
                }
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

    private void openGateWayVNPay() {
        ApiService.apiService.paymentVNPAY(new OrderRequest(orderModel)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    String url = response.body();
                    openSdk(url);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }
    public void openSdk(String url) {
        Intent intent = new Intent(this, VNP_AuthenticationActivity.class);
        intent.putExtra("url", url); //bắt buộc, VNPAY cung cấp
        intent.putExtra("tmn_code", "TZJ4OYC1"); //bắt buộc, VNPAY cung cấp
        intent.putExtra("scheme", "paymentactivity"); //bắt buộc, scheme để mở lại app khi có kết quả thanh toán từ mobile banking
        intent.putExtra("is_sandbox", true); //bắt buộc, true <=> môi trường test, true <=> môi trường live
        VNP_AuthenticationActivity.setSdkCompletedCallback(new VNP_SdkCompletedCallback() {
            @Override
            public void sdkAction(String action) {
                Log.wtf("SplashActivity", "action: " + action);
                //action == AppBackAction
                //Người dùng nhấn back từ sdk để quay lại

                //action == CallMobileBankingApp
                //Người dùng nhấn chọn thanh toán qua app thanh toán (Mobile Banking, Ví...)
                //lúc này app tích hợp sẽ cần lưu lại cái PNR, khi nào người dùng mở lại app tích hợp thì sẽ gọi kiểm tra trạng thái thanh toán của PNR Đó xem đã thanh toán hay chưa.

                //action == WebBackAction
                //Người dùng nhấn back từ trang thanh toán thành công khi thanh toán qua thẻ khi url có chứa: cancel.sdk.merchantbackapp
                if(action.equals("WebBackAction")){
                    Toast.makeText(PaymentActivity.this, "Giao dịch thất bại", Toast.LENGTH_SHORT).show();
                }

                //action == FaildBackAction
                //giao dịch thanh toán bị failed
                if(action.equals("FaildBackAction")){
                    Toast.makeText(PaymentActivity.this, "Giao dịch thất bại", Toast.LENGTH_SHORT).show();
                }

                //action == SuccessBackAction
                //thanh toán thành công trên webview
                if(action.equals("SuccessBackAction")){
                    Toast.makeText(PaymentActivity.this, "Giao dịch thành công", Toast.LENGTH_SHORT).show();
                    updatePaidOrder();
                }
            }
        });
        startActivity(intent);
    }

    private void getQRExchange() {
        ApiService.apiService.paymentExchange(new OrderRequest(orderModel))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()){
                            String qr = response.body();
                            showImageDialog(qr);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }

    private void setViewExchangeMethod() {

        binding.cashAndExchangeLl.setVisibility(View.VISIBLE);
        binding.moneyChangeRl.setVisibility(View.GONE);
        binding.customerPayLl.setVisibility(View.GONE);
        binding.iconPayment.setImageResource(R.drawable.currency_exchange_icon);
        binding.namePayment.setText("Chuyển khoản ngân hàng");

    }

    private void setViewQRMethod() {

        binding.cashAndExchangeLl.setVisibility(View.GONE);
        binding.iconPayment.setImageResource(R.drawable.vnpaylogo);
        binding.moneyChangeRl.setVisibility(View.GONE);
        binding.customerPayLl.setVisibility(View.GONE);
        binding.namePayment.setText("VNPAY");


    }

    private void setViewCashMethod() {

        binding.cashAndExchangeLl.setVisibility(View.VISIBLE);
        binding.moneyChangeRl.setVisibility(View.VISIBLE);
        binding.customerPayLl.setVisibility(View.VISIBLE);
        binding.iconPayment.setImageResource(R.drawable.paid_ic);
        binding.namePayment.setText("Tiền mặt");
    }

    private void viewPayment() {
        binding.quantity.setText(String.valueOf(orderTotalQuantity));
        Util.updateMoneyLabel(binding.totalPrice,orderTotalAmount);

        if(orderModel.getDiscount_amount() != 0){
            binding.btnVnd.setChecked(true);
            Util.updateMoneyEditText(binding.discountEt,orderModel.getDiscount_amount());
        }
        if(orderModel.getDiscount_percent() != 0){
            binding.btnPercent.setChecked(true);
            Util.updateMoneyEditText(binding.discountEt,orderModel.getDiscount_percent());
        }
        if(!binding.discountEt.getText().toString().isEmpty()){
            binding.needToPay.setText(
                    calculateNeedToPay(
                            binding.discountEt.getText().toString().trim(),
                            binding.totalPrice.getText().toString().trim()
                    )
            );
        }else
            Util.updateMoneyLabel(binding.needToPay,orderTotalAmount);


        Util.updateMoneyEditText(binding.customerPayEt,orderTotalAmount);


        binding.moneyChangeTv.setText(calculateMoneyChange(
                binding.needToPay.getText().toString().trim(),
                binding.customerPayEt.getText().toString().trim()));

    }
    private String calculateMoneyChange(String needToPay, String customerPay){
        double dNeedTP = Double.parseDouble(needToPay.replaceAll("[,.₫]",""));
        double dCusP = Double.parseDouble(customerPay.replaceAll("[,.₫]",""));
        return decimalFormat.format(dCusP - dNeedTP)+"₫";
    }
    private void setFormatMoney() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("#,###",symbols);
    }



    private String calculateNeedToPay(String discountV, String totalPrice){
        double dTotalPr = Double.parseDouble(totalPrice.replaceAll("[,.₫]",""));
        if(discountUnit){
            try {
                int iDiscV = Integer.parseInt(discountV);
                return decimalFormat.format(dTotalPr - (dTotalPr * iDiscV / 100));
            }catch (Exception e){
                return totalPrice;
            }

        }
        try {
            double dDiscV = Double.parseDouble(discountV.replaceAll("[,.₫]",""));
            if( dDiscV >= dTotalPr ) {
                Util.updateMoneyEditText(binding.discountEt,dTotalPr);
                return "0";
            }
            return decimalFormat.format(dTotalPr - dDiscV);
        }catch (Exception e){
            return totalPrice;
        }
    }
    private void showImageDialog(String qrExchange) {
        DialogImageBinding imageBinding;

        imageBinding = DialogImageBinding.inflate(getLayoutInflater());
        // Create a dialog
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove the title bar

        // Set the custom layout for the dialog
        dialog.setContentView(imageBinding.getRoot());

        imageBinding.closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        imageBinding.doneCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePaidOrder();
            }
        });

        // Set the image to display in the ImageView
        Glide.with(this).load(qrExchange).into(imageBinding.imageView);// Replace with your image resource
        // Show the dialog
        dialog.show();
    }

    private void updatePaidOrder() {
        OrderRequest request = new OrderRequest(orderModel.getOrder_id(),true){};
        request.setPayment_method_id(getPaymentMethodId());
        ApiService.apiService.updatePaidOrder(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("response","PaidOrder");
                    setResult(RESULT_OK,resultIntent);
                    notifyPaidOrderSuccess();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi sever", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notifyPaidOrderSuccess() {
        JSONObject object = new JSONObject();
        try {
            object.put("from", Auth.User_Uid);
            JSONObject message = new JSONObject();
            message.put("order_id",orderModel.getOrder_id());
            message.put("status","đã hoàn tất thanh toán đơn của " + tableModel.getTable_name() );
            message.put("new_table_id",tableModel.getTable_id());
            object.put("message",message);
            object.put("action","PAID");
            mApp.getWebSocketClient().send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}