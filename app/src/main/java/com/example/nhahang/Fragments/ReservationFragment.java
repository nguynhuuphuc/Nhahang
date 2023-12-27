package com.example.nhahang.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.example.nhahang.Adapters.CategoryTableAdapter;
import com.example.nhahang.Adapters.LocalDateAdapter;

import com.example.nhahang.Adapters.ReservationsAdapter;
import com.example.nhahang.Interfaces.ApiService;

import com.example.nhahang.Interfaces.IOnClickDateItemListener;

import com.example.nhahang.Models.LocationModel;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.Requests.ServerRequest;

import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.ReservationDetailActivity;


import com.example.nhahang.databinding.FragmentReservationsBinding;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Locale;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private FragmentReservationsBinding binding;
    private String location = "01";
    private List<LocationModel> locationList = new ArrayList<>();
    private CategoryTableAdapter categoryTableAdapter;
    private List<LocalDate> localDates;
    private LocalDateAdapter localDateAdapter;
    private DateTimeFormatter formatter;
    private LocalDate daySelected;

    private List<ReservationModel> reservations;
    private ReservationsAdapter reservationsAdapter;

    private int dateItemSelected = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReservationsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        daySelected = LocalDate.now();

        formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM, yyyy", new Locale("vi"));

        localDates = getDates();
        localDateAdapter = new LocalDateAdapter(getContext(), localDates, new IOnClickDateItemListener() {
            @Override
            public void onClick(int position) {
                // Sử dụng formatter để chuyển LocalDate thành chuỗi
                dateItemSelected = position;
                LocalDate date = localDates.get(position);
                daySelected = date;
                String formattedDate = date.format(formatter);
                binding.dateTextView.setText(formattedDate);
                getReservations();
            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),7);
        binding.daysRecyclerView.setLayoutManager(layoutManager);
        binding.daysRecyclerView.setAdapter(localDateAdapter);

        reservations = new ArrayList<>();
        reservationsAdapter = new ReservationsAdapter(getContext(),reservations);

        binding.reservationsRv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        binding.reservationsRv.setAdapter(reservationsAdapter);
        reservationsAdapter.setOnClickItemListener(new ReservationsAdapter.OnClickItemListener() {
            @Override
            public void onClick(int position) {
                ReservationModel reservation = reservations.get(position);
                Intent intent = new Intent(getContext(), ReservationDetailActivity.class);
                intent.putExtra("reservation",reservation);
                startActivity(intent);

            }
        });
        getReservations();

        binding.SwipeRefreshLayout.setOnRefreshListener(this);


        return view;
    }

    private void getReservations() {
        InProgress(true);
        ApiService.apiService.getReservationsByDay(new ServerRequest(daySelected.toString())).enqueue(new Callback<ArrayList<ReservationModel>>() {
            @Override
            public void onResponse(Call<ArrayList<ReservationModel>> call, Response<ArrayList<ReservationModel>> response) {
                if(response.isSuccessful()){
                    reservations.clear();
                    reservations.addAll(response.body());
                    reservationsAdapter.notifyDataSetChanged();
                    InProgress(false);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ReservationModel>> call, Throwable t) {
                Toast.makeText(getContext() , "Server err", Toast.LENGTH_SHORT).show();
                InProgress(false);

            }
        });
    }

    private List<LocalDate> getDates() {
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(formatter);
        binding.dateTextView.setText(formattedDate);
        localDates.add(today);

        // Thêm 6 ngày tiếp theo vào List
        for (int i = 1; i <= 6; i++) {
            localDates.add(today.plusDays(i));
        }
        return localDates;
    }




    void InProgress(boolean isIn){
        if(isIn){
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.progressBar.setVisibility(View.GONE);

        }

    }


    @Override
    public void onRefresh() {
        binding.SwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getToActivity().equals("Reservation")){
            NotificationModel notify = event.getNotificationModel();
            if(notify.getAction().equals("BOOKING_TABLE")){
                reservations.add(notify.getReservation());
                reservationsAdapter.sortItem();
                return;
            }
            if(!event.getReservationModels().isEmpty()){
                Toast.makeText(getContext(), ""+event.getReservationModels().size(), Toast.LENGTH_SHORT).show();
                for(ReservationModel model : event.getReservationModels()){
                    LocalDateTime localDateTime = model.getReservation_time().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    if(daySelected.equals(localDateTime.toLocalDate())){
                        reservationsAdapter.updateItem(model);
                    }
                }
                reservationsAdapter.sortItem();
                if(EventBus.getDefault().removeStickyEvent(event)){
                    event.setReservationModels(new ArrayList<>());
                }
            }
        }

    }
}