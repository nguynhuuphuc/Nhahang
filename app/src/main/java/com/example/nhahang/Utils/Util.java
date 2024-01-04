package com.example.nhahang.Utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.Requests.PhoneNumberRequest;
import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.R;
import com.example.nhahang.ViewModels.AttributeWatcherViewModel;
import com.google.type.DateTime;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Util {
    public static final String localhost = "172.20.10.5";
    public static final String WEBSOCKET_URL = "ws://" + localhost+":3000";

        public static final String CRUD = "CRUD";

    public static boolean formIsValid;
    public static boolean isVerify = false;

    public static void updateDateLabel(EditText editText, Date date){
        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        editText.setText(dateFormat.format(date));
    }

    public static void updateDateLabel(TextView textView, Date date){
        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        textView.setText(dateFormat.format(date));
    }
    public static void updateTimeLabel(TextView textView, Date date){
        String format = "HH:mm:ss";
        SimpleDateFormat timeFormat = new SimpleDateFormat(format);
        textView.setText(timeFormat.format(date));
    }

    public static void updateMoneyLabel(TextView textView,double value){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        DecimalFormat customFormat = new DecimalFormat("###,###.##", symbols);
        String formattedAmount = customFormat.format(value)+ "₫";
        textView.setText(formattedAmount);
    }
    public static void updateMoneyEditText(EditText editText,double value){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        DecimalFormat customFormat = new DecimalFormat("###,###.##", symbols);
        String formattedAmount = customFormat.format(value);
        editText.setText(formattedAmount);
    }

    public static Date DateParse(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            // Parse the date string into a LocalDate
            LocalDate parsedDate = LocalDate.parse(dateString, formatter);
            Instant instant = parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            return Date.from(instant);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error parsing date string.");
            return null;
        }
    }
    public static void checkAccount(Context context,String fullNumberWithPlus){
        AttributeWatcherViewModel attributeWatcherViewModel = new ViewModelProvider((ViewModelStoreOwner) context)
                .get(AttributeWatcherViewModel.class);
        PhoneNumberRequest request = new PhoneNumberRequest(fullNumberWithPlus);
        ApiService.apiService.checkPhoneNumberRequest(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    attributeWatcherViewModel.setIsPhoneExistsLiveData(true);
                    checkPasswordExists(context,request);
                    return;
                }
                attributeWatcherViewModel.setIsPhoneExistsLiveData(false);
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(context, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private static void checkPasswordExists(Context context, PhoneNumberRequest request) {
        AttributeWatcherViewModel attributeWatcherViewModel = new ViewModelProvider((ViewModelStoreOwner) context)
                .get(AttributeWatcherViewModel.class);
        ApiService.apiService.checkPasswordExists(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    attributeWatcherViewModel.setIsPasswordExistsLiveData(true);
                    return;
                }
                attributeWatcherViewModel.setIsPasswordExistsLiveData(false);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(context, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static String getDayOfWeek(LocalDate date){
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        switch (dayOfWeek.getValue()){
            case 1:
                return "T2";
            case 2:
                return "T3";
            case 3:
                return "T4";
            case 4:
                return "T5";
            case 5:
                return "T6";
            case 6:
                return "T7";
            case 7:
                return "CN";
        }
        return "";
    }
    public static double tinhTienGiamGia(double giaTien, double phanTramGiamGia) {
        double tileGiamGia = phanTramGiamGia / 100;
        return giaTien * tileGiamGia;
    }
    public static double tinhTienSauGiamGia(double giaTien, double phanTramGiamGia) {
        double tileGiamGia = phanTramGiamGia / 100;
        double tienGiam = giaTien * tileGiamGia;
        return giaTien - tienGiam;
    }
    public static View.OnTouchListener ShowOrHidePass(final EditText editText){
        return new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Người dùng nhấn vào biểu tượng
                        if (editText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                            // Nếu đang hiển thị mật khẩu, chuyển sang ẩn mật khẩu
                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                        } else {
                            // Ngược lại, chuyển sang hiển thị mật khẩu
                            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
                        }
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static String dateTimeInMessageFormatting(Date date){
        // Lấy ngày và giờ hiện tại
        Date currentDate = new Date();
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);

        // Ngày và giờ cần định dạng
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);

        // Tạo đối tượng SimpleDateFormat để định dạng kiểu ngày và giờ
        SimpleDateFormat dateFormat;
        if (currentCalendar.get(Calendar.YEAR) == targetCalendar.get(Calendar.YEAR)
                && currentCalendar.get(Calendar.MONTH) == targetCalendar.get(Calendar.MONTH)
                && currentCalendar.get(Calendar.DAY_OF_MONTH) == targetCalendar.get(Calendar.DAY_OF_MONTH)) {
            // Nếu là ngày hiện tại, chỉ hiển thị giờ và phút
            dateFormat = new SimpleDateFormat("h:mm a");
        } else {
            // Ngày không trùng khớp, hiển thị ngày và giờ
            dateFormat = new SimpleDateFormat("yy/MM/dd h:mm a");
        }
        return dateFormat.format(date);
    }


    public static String convertToTodayYesterday(Date date) {
        Date currentDate = new Date();

        // Create Calendar instances to work with dates
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);

        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);

        // Compare the years, months, and days
        if (currentCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)
                && currentCalendar.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH)
                && currentCalendar.get(Calendar.DAY_OF_MONTH) == dateCalendar.get(Calendar.DAY_OF_MONTH)) {
            return "Hôm nay";
        }

        currentCalendar.add(Calendar.DATE, -1); // Subtract one day to check for yesterday

        if (currentCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)
                && currentCalendar.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH)
                && currentCalendar.get(Calendar.DAY_OF_MONTH) == dateCalendar.get(Calendar.DAY_OF_MONTH)) {
            return "Hôm qua";
        }

        // If it's not today or yesterday, format the date using SimpleDateFormat
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }
    public static String DayFormatting(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    public static String DayTimeFormatting(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    public static String TimeFormatting(Date date){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        return  timeFormat.format(date);
    }
    public static void showDatePickerDialog(Context context, EditText editTextDate) {
        // Khởi tạo calendar với ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        // Lấy ngày, tháng, năm từ Calendar
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Cập nhật ngày trong calendar
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                        // Format ngày để hiển thị trong EditText
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String formattedDate = sdf.format(calendar.getTime());

                        // Đặt ngày đã chọn vào EditText
                        editTextDate.setText(formattedDate);
                    }
                },
                year, month, dayOfMonth);

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }
    public static void changeTablesAdding(List<TableModel> changeTables, List<TableModel> values){
        if(changeTables == null ) changeTables = new ArrayList<>();
        if(changeTables.isEmpty()){
            changeTables.addAll(values);
            return;
        }
        Map<Integer, TableModel> map = new HashMap<>();

        // Tạo một map từ array1 để dễ dàng kiểm tra sự tồn tại
        for (TableModel obj : changeTables) {
            map.put(obj.getTable_id(), obj);
        }

        // Duyệt qua array2 và ghi đè hoặc thêm mới vào array1
        for (TableModel obj : values) {
            if (map.containsKey(obj.getTable_id())) {
                // Ghi đè nếu tồn tại
                TableModel existingObject = map.get(obj.getTable_id());
                for(TableModel model : changeTables){
                    if(model.getTable_id() == existingObject.getTable_id()){
                        changeTables.set(changeTables.indexOf(model),obj);
                        break;
                    }
                }
            } else {
                // Thêm mới nếu không tồn tại
                changeTables.add(obj);
            }
        }
    }
    public static void changeReservationAdding(List<ReservationModel> changeReservations, List<ReservationModel> values){
        if(changeReservations == null ) changeReservations = new ArrayList<>();
        if(changeReservations.isEmpty()){
            changeReservations.addAll(values);
            return;
        }
        Map<Integer, ReservationModel> map = new HashMap<>();

        // Tạo một map từ array1 để dễ dàng kiểm tra sự tồn tại
        for (ReservationModel obj : changeReservations) {
            map.put(obj.getId(), obj);
        }

        // Duyệt qua array2 và ghi đè hoặc thêm mới vào array1
        for (ReservationModel obj : values) {
            if (map.containsKey(obj.getId())) {
                // Ghi đè nếu tồn tại
                ReservationModel existingObject = map.get(obj.getId());
                for(ReservationModel model : changeReservations){
                    if(model.getId() == existingObject.getId()){
                        changeReservations.set(changeReservations.indexOf(model),existingObject);
                        break;
                    }
                }
            } else {
                // Thêm mới nếu không tồn tại
                changeReservations.add(obj);
            }
        }
    }

}
