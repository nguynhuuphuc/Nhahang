package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.Requests.RevenueRequest;
import com.example.nhahang.Models.Respones.RevenueResponse;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityRevenueStatisticsBinding;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.datepicker.RangeDateSelector;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.protobuf.Api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RevenueStatisticsActivity extends AppCompatActivity implements OnChartValueSelectedListener, AdapterView.OnItemSelectedListener {
    private ActivityRevenueStatisticsBinding binding;
    private List<String> xLabel = new ArrayList<>();
    private ArrayList<BarEntry> entries,intervalEntries;
    private String label = "Doanh thu theo ngày";
    private final String[] intervalOptions = {"Ngày", "Tuần", "Tháng", "Quý", "Năm"};
    private final String[] dateRangeOptions = {"Thống kê của tuần", "Thống kê theo khoảng thời gian"};
    private boolean isDateRange = false;
    private String interval = "day";
    private YAxis yAxis;
    private  ValueFormatter customValueFormatter;
    private int selectedColor = Color.BLUE; // Color for selected bars
    private int unselectedColor = Color.GRAY;
    private BarDataSet dataSet;
    private LocalDate dayStart, dayEnd;


    public LocalDate getDayStart() {
        return dayStart;
    }

    public void setDayStart(LocalDate dayStart) {
        this.dayStart = dayStart;
    }

    public LocalDate getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(LocalDate dayEnd) {
        this.dayEnd = dayEnd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRevenueStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        customValueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f,000đ", value); // Hiển thị giá trị số tiền VND
            }
        };



        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });


        getTodayRev();
        getForecastRev();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, intervalOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.intervalSpinner.setAdapter(adapter);


        ArrayAdapter<String> dateRangeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,dateRangeOptions);
        dateRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dateRangeSpinner.setAdapter(dateRangeAdapter);


        binding.barChart2.getAxisRight().setDrawLabels(false);
        binding.barChart2.setOnChartValueSelectedListener(this);
        binding.barChart2.setVisibleXRangeMaximum(50f);
        binding.barChart2.moveViewToX(50f);
        binding.barChart2.setPinchZoom(true); // Cho phép phóng to/nhỏ gọn bằng cách kẹp (pinch)
        binding.barChart2.setScaleEnabled(false); // Cho phép phóng to/nhỏ gọn theo trục Y


        binding.barChart.getAxisRight().setDrawLabels(false);
        binding.barChart.setVisibleXRangeMaximum(50f);
        binding.barChart.moveViewToX(50f);
        binding.barChart.setPinchZoom(true); // Cho phép phóng to/nhỏ gọn bằng cách kẹp (pinch)
        binding.barChart.setScaleEnabled(false);



        binding.carviewCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.carviewCloseIv.setEnabled(false);
                binding.detailCardView.setVisibility(View.INVISIBLE);
            }
        });

        binding.intervalSpinner.setOnItemSelectedListener(this);

        binding.dateRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectOption = dateRangeOptions[position];
                Toast.makeText(RevenueStatisticsActivity.this, selectOption, Toast.LENGTH_SHORT).show();
                switch (selectOption){
                    case "Thống kê của tuần":
                        isDateRange = false;
                        viewRangeSelect(false);
                        getThisWeekBarchart();
                        checkForwardWeek();
                        break;
                    case "Thống kê theo khoảng thời gian":
                        isDateRange = true;
                        viewRangeSelect(true);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(new Pair<>(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                )).build();
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                String start = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.first));
                String end = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(new Date(selection.second));
                binding.editTextStartDate.setText(start);
                binding.editTextEndDate.setText(end);
            }
        });
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                getRevenue(new Date(selection.first),new Date(selection.second));
            }
        });
        binding.selectRangeDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(),"tag");
            }
        });

        binding.backWeekArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy ngày thứ 2 của tuần trước
                LocalDate previousMonday = getDayStart().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));

                // Lấy ngày chủ nhật của tuần trước
                LocalDate previousSunday = getDayStart().with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
                setDayStart(previousMonday);
                setDayEnd(previousSunday);
                getRevenue(getDayStart(),getDayEnd());
                checkForwardWeek();
            }
        });

        binding.forwardWeekArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDate nextMonday = getDayEnd().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

                // Lấy ngày chủ nhật của tuần sau
                LocalDate nextSunday = getDayEnd().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
                setDayStart(nextMonday);
                setDayEnd(nextSunday);
                getRevenue(getDayStart(),getDayEnd());
                checkForwardWeek();

            }
        });
    }

    private void getForecastRev() {
        ApiService.apiService.getRevenue(new RevenueRequest("forecast")).enqueue(new Callback<ArrayList<RevenueResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RevenueResponse>> call, Response<ArrayList<RevenueResponse>> response) {
                if(response.isSuccessful()){
                    RevenueResponse forecast = response.body().get(0);
                    binding.todayWillQuantityTv.setText(String.valueOf(forecast.getQuantity_forecast()));
                    Util.updateMoneyLabel(binding.todayWillTotalAmountTv,forecast.getTotal_forecast());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RevenueResponse>> call, Throwable t) {

            }
        });
    }

    private void getTodayRev() {
        RevenueRequest request = new RevenueRequest("today");
        ApiService.apiService.getRevenue(request).enqueue(new Callback<ArrayList<RevenueResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RevenueResponse>> call, Response<ArrayList<RevenueResponse>> response) {
                if(response.isSuccessful()){
                    RevenueResponse todayRev = response.body().get(0);
                    Util.updateMoneyLabel(binding.todayTotalAmountTv,todayRev.getTotal());
                    binding.todayQuantityTv.setText(String.valueOf(todayRev.getQuantity()));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RevenueResponse>> call, Throwable t) {

            }
        });
    }

    private void viewRangeSelect(boolean b) {
        if(b){
            binding.rangeDateSelectLl.setVisibility(View.VISIBLE);
            binding.forwardWeekArrow.setEnabled(false);
            binding.backWeekArrow.setEnabled(false);
            binding.backWeekArrow.setVisibility(View.INVISIBLE);
            binding.forwardWeekArrow.setVisibility(View.INVISIBLE);
        }else{
            binding.rangeDateSelectLl.setVisibility(View.GONE);
            binding.backWeekArrow.setVisibility(View.VISIBLE);
            binding.forwardWeekArrow.setVisibility(View.VISIBLE);
            binding.forwardWeekArrow.setEnabled(true);
            binding.backWeekArrow.setEnabled(true);
        }
    }

    private void checkForwardWeek(){
        LocalDate nextMonday = getDayEnd().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        if(nextMonday.isAfter(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY)))){
            binding.forwardWeekArrow.setEnabled(false);
            binding.forwardWeekArrow.setColorFilter(getColor(R.color.gray));
        }else{
            binding.forwardWeekArrow.setColorFilter(getColor(R.color.black));
            binding.forwardWeekArrow.setEnabled(true);
        }
    }

    private void getThisWeekBarchart() {
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // Lấy ngày chủ nhật của tuần này
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        setDayStart(monday);
        setDayEnd(sunday);
        getRevenue(monday,sunday);
    }

    private void viewIntervalRev(LocalDate start, LocalDate end){
        String intervalLabelS;
        int monthStart = start.getMonthValue();
        int monthEnd = end.getMonthValue();

        if(monthStart != monthEnd){
            // Định dạng ngày thành chuỗi
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'tháng' MM");
            // Lấy thông tin ngày và tháng từ mỗi ngày
            String formattedStart = start.format(formatter);
            String formattedEnd = end.format(formatter);
            intervalLabelS = String.format("Doanh thu %s - %s", formattedStart, formattedEnd);

        }else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
            String formattedStart = start.format(formatter);
            String formattedEnd = end.format(formatter);
            intervalLabelS = String.format("Doanh thu %s - %s tháng %d", formattedStart, formattedEnd, start.getMonthValue());
        }

        binding.intervalTv.setText(intervalLabelS);
        double total = 0;
        long quantity = 0;

        for(BarEntry entry : intervalEntries){
            RevenueResponse item = (RevenueResponse) entry.getData();
            total += item.getTotal();
            quantity += item.getQuantity();
        }

        Util.updateMoneyLabel(binding.weekTotalAmountTv,total);
        binding.weekQuantityTv.setText(String.valueOf(quantity));
    }

    private void getRevenue(){
        RevenueRequest request = new RevenueRequest(interval);
        ApiService.apiService.getRevenue(request).enqueue(new Callback<ArrayList<RevenueResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RevenueResponse>> call, Response<ArrayList<RevenueResponse>> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    addEntries(response.body());
                    setBarchart();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RevenueResponse>> call, Throwable t) {

            }
        });
    }

    private void getRevenue(Date start, Date end){
        RevenueRequest request = new RevenueRequest(Util.DayFormatting(start),Util.DayFormatting(end));
        ApiService.apiService.getRevenue(request).enqueue(new Callback<ArrayList<RevenueResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RevenueResponse>> call, Response<ArrayList<RevenueResponse>> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    addIntervalEntries(response.body());
                    setIntervalBarchart();
                    LocalDate startLocal = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate endLocal = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    viewIntervalRev(startLocal,endLocal);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RevenueResponse>> call, Throwable t) {

            }
        });
    }

    private void getRevenue(LocalDate start, LocalDate end){
        RevenueRequest request = new RevenueRequest(start.toString(),end.toString());
        ApiService.apiService.getRevenue(request).enqueue(new Callback<ArrayList<RevenueResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RevenueResponse>> call, Response<ArrayList<RevenueResponse>> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    addIntervalEntries(response.body());
                    setIntervalBarchart();
                    viewIntervalRev(start,end);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RevenueResponse>> call, Throwable t) {

            }
        });
    }


    private void addIntervalEntries(ArrayList<RevenueResponse> responses) {
        if(intervalEntries != null && !intervalEntries.isEmpty()){
            intervalEntries.clear();
        }
        float x = 0;
        for(RevenueResponse item: responses){
            float value = (float) (item.getTotal() / 1000);
            if(intervalEntries == null){
                intervalEntries = new ArrayList<>();
                intervalEntries.add(new BarEntry(x++,value,item));
                continue;
            }
            intervalEntries.add(new BarEntry(x++,value,item));
        }
    }

    private void setIntervalBarchart(){
        InProgressBarchart(true);
        if(!binding.barChart.isEmpty()){
            binding.barChart.clear();
        }
        float max = 0f;
        int count = 0;

        for(BarEntry entry : intervalEntries){
            max = Math.max(max,entry.getY());
            count++;
        }
        yAxis = binding.barChart.getAxisLeft();
        yAxis.setValueFormatter(customValueFormatter);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(max + 500f);
        yAxis.setZeroLineWidth(2f);
        yAxis.setZeroLineColor(Color.BLACK);
        yAxis.setLabelCount(count + 3);


        BarDataSet dataSet = new BarDataSet(intervalEntries,"Thứ trong tuần");
        dataSet.setColors(ColorTemplate.getHoloBlue());
        dataSet.setHighlightEnabled(false);
        dataSet.setValueFormatter(customValueFormatter);

        float barWidth = 0.45f; // x2 dataset
        setDataSet(dataSet);


        BarData barData = new BarData(dataSet);
        barData.setBarWidth(barWidth);
        binding.barChart.setData(barData);
        binding.barChart.setFitBars(true);



        binding.barChart.getDescription().setEnabled(false);
        if(!xLabel.isEmpty()) xLabel.clear();
        if(isDateRange){
            for(BarEntry entry : intervalEntries){
                RevenueResponse response = (RevenueResponse) entry.getData();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
                xLabel.add(dateFormat.format(response.getDay()));
            }
        }else{
            for(BarEntry entry : intervalEntries){
                RevenueResponse response = (RevenueResponse) entry.getData();
                if(response.getDay_of_week_number() == '1')
                    xLabel.add("Chủ nhật");
                else{
                    xLabel.add(String.valueOf(response.getDay_of_week_number()));
                }
            }
        }

        binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabel));
        binding.barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.barChart.getXAxis().setGranularity(1f);
        binding.barChart.getXAxis().setGranularityEnabled(true);

        binding.barChart.invalidate();
        InProgressBarchart(false);
    }

    private void setBarchart() {
        InProgressBarchart2(true);
        if(!binding.barChart2.isEmpty()){
            binding.barChart2.clear();
        }


        float max = 0f;
        int count = 0;

        for(BarEntry entry : entries){
            max = Math.max(max,entry.getY());
            count++;
        }
        yAxis = binding.barChart2.getAxisLeft();
        yAxis.setValueFormatter(customValueFormatter);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(max + 500f + (max/2.6f));
        yAxis.setZeroLineWidth(2f);
        yAxis.setZeroLineColor(Color.BLACK);
        yAxis.setLabelCount(count + 3);


        BarDataSet dataSet = new BarDataSet(entries,label);
        dataSet.setHighLightColor(getColor(R.color.lighter_deep_sky_blue));
        dataSet.setColor(Color.LTGRAY);
        dataSet.setHighLightAlpha(255);
        dataSet.setValueFormatter(customValueFormatter);

        float barWidth = 0.45f; // x2 dataset
        setDataSet(dataSet);


        BarData barData = new BarData(dataSet);
        barData.setBarWidth(barWidth);
        binding.barChart2.setData(barData);
        binding.barChart2.setFitBars(true);

        binding.barChart2.getDescription().setEnabled(false);


        if(!xLabel.isEmpty()) xLabel.clear();

        for(BarEntry entry : entries){
            RevenueResponse response = (RevenueResponse) entry.getData();
            switch (interval){
                case "day":
                    xLabel.add(Util.convertToTodayYesterday(response.getInterval()));
                    break;
                case "quarter":
                    xLabel.add(response.getQuarter());
                    break;
                case "year":
                    xLabel.add(response.getYear());
                    break;
                default:
                    xLabel.add(Util.DayFormatting(response.getInterval()));
                    break;
            }

        }

        binding.barChart2.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabel));
        binding.barChart2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.barChart2.getXAxis().setGranularity(1f);
        binding.barChart2.getXAxis().setGranularityEnabled(true);

        BarEntry entry = entries.get(entries.size()-1);

        binding.barChart2.highlightValue(entry.getX(),entry.getY(),0);
        binding.barChart2.invalidate();
        InProgressBarchart2(false);
    }

    private void addEntries(ArrayList<RevenueResponse> revenueResponses) {
        if(entries != null && !entries.isEmpty()){
            entries.clear();
        }
        float x = revenueResponses.size()-1;
        for(RevenueResponse item: revenueResponses){
            float value = (float) (item.getTotal() / 1000);
            if(entries == null){
                entries = new ArrayList<>();
                entries.add(0,new BarEntry(x--,value,item));
                continue;
            }
            entries.add(0,new BarEntry(x--,value,item));
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        RevenueResponse response = (RevenueResponse) e.getData();
        binding.cardviewQuantityTv.setText(String.valueOf(response.getQuantity()));
        switch (interval){
            case "day":
                binding.cardviewDayTv.setText(Util.convertToTodayYesterday(response.getInterval()));
                break;
            case "quarter":
                binding.cardviewDayTv.setText(response.getQuarter());
                break;
            case "year":
                binding.cardviewDayTv.setText(response.getYear());
                break;
            default:
                binding.cardviewDayTv.setText(Util.DayFormatting(response.getInterval()));
                break;
        }

        Util.updateMoneyLabel(binding.cardviewTotalAmountTv,response.getTotal());
        binding.carviewCloseIv.setEnabled(true);
        binding.detailCardView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onNothingSelected() {
        BarEntry entry = entries.get(entries.size()-1);
        binding.barChart2.highlightValue(entry.getX(),entry.getY(),0);
        binding.barChart2.invalidate();
    }

    public void setDataSet(BarDataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedOption = intervalOptions[position];
        switch (selectedOption){
            case "Ngày":
                interval = "day";
                label = "Doanh thu theo ngày";
                getRevenue();
                break;
            case "Tuần":

                interval = "week";
                label = "Doanh thu theo tuần";
                getRevenue();
                break;
            case "Tháng":

                interval = "month";
                label = "Doanh thu theo tháng";
                getRevenue();
                break;
            case "Quý":

                interval = "quarter";
                label = "Doanh thu theo quý";
                getRevenue();
                break;
            case "Năm":
                interval = "year";
                label = "Doanh thu theo năm";
                getRevenue();
                break;

        }
    }

    private void viewDayOfWeekLabel(boolean b) {
        if(b){
            binding.dayOfWeekCv.setVisibility(View.VISIBLE);
            return;
        }
        binding.dayOfWeekCv.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void InProgressBarchart2(boolean isIn){
        if(isIn){
            binding.detailCardView.setVisibility(View.GONE);
            binding.barChart2.setEnabled(false);
            binding.barChart2.setVisibility(View.INVISIBLE);
            binding.progressBarChart2.setVisibility(View.VISIBLE);
            return;
        }
        binding.barChart2.setEnabled(true);
        binding.barChart2.setVisibility(View.VISIBLE);
        binding.progressBarChart2.setVisibility(View.GONE);
        binding.detailCardView.setVisibility(View.VISIBLE);
    }

    private void InProgressBarchart(boolean isIn){
        if(isIn){
            binding.barChart.setEnabled(false);
            binding.barChart.setVisibility(View.INVISIBLE);
            binding.progressBarChart.setVisibility(View.VISIBLE);
            return;
        }
        binding.barChart.setEnabled(true);
        binding.barChart.setVisibility(View.VISIBLE);
        binding.progressBarChart.setVisibility(View.GONE);

    }

}