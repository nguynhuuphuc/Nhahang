package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Adapters.ViewPagerAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;


import com.google.firebase.auth.FirebaseAuth;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


    public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        FirebaseAuth auth = FirebaseAuth.getInstance();


        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Auth.User_Uid = getIntent().getStringExtra("user_uid");
        Auth.PhoneNumber = getIntent().getStringExtra("phoneNumber");
        if(Auth.User_Uid == null){
            Auth.User_Uid = auth.getUid();
        }
        authSigined();

        setUpViewPager();
        binding.viewPager.setUserInputEnabled(false);



        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        binding.viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_menu:
                        binding.viewPager.setCurrentItem(1);
                        break;
                    case R.id.action_reservation:
                        binding.viewPager.setCurrentItem(2);
                        break;
                    case R.id.action_user:
                        binding.viewPager.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });

        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.nav_quanLyMenu:
                        startActivity(new Intent(MainActivity.this,MenuManagementActivity.class));
                        break;
                    case R.id.nav_quanLyNV:
                        startActivity(new Intent(MainActivity.this,StaffManagementActivity.class));                        break;
                }
                return true;
            }
        });

        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) binding.navigationView.getLayoutParams();

            // Đặt chiều rộng của NavigationView là một nửa của màn hình
        params.width = getResources().getDisplayMetrics().widthPixels / 2;

            // Cập nhật layout params mới
        binding.navigationView.setLayoutParams(params);

    }

    private void authSigined() {
            UserUidRequest userUidRequest = new UserUidRequest(Auth.User_Uid);
            ApiService.apiService.getEmployee(userUidRequest).enqueue(new Callback<Employee>() {
                @Override
                public void onResponse(Call<Employee> call, Response<Employee> response) {
                    if(response.isSuccessful()){
                        Employee employee = response.body();
                        assert employee != null;
                        String s = "Xin chào "+ employee.getFull_name();
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                        if(employee.getPosition_id().equals("QL")){
                            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                            binding.drawerLayout.addDrawerListener(toggle);
                            toggle.syncState();
                        }
                        Intent finishIntent = new Intent(LoginActivity.ACTION_FINISH_ACTIVITY);
                        sendBroadcast(finishIntent);
                    }
                }
                @Override
                public void onFailure(Call<Employee> call, Throwable t) {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }
            });

    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(MainActivity.this);
        binding.viewPager.setAdapter(viewPagerAdapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        binding.toolbarTitle.setText("Đặt bàn");
                        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
                        break;
                    case 1:
                        binding.toolbarTitle.setText("Thực đơn");
                        binding.bottomNavigation.setSelectedItemId(R.id.action_menu);
                        break;
                    case 2:
                        binding.toolbarTitle.setText("Bàn đã đặt");
                        binding.bottomNavigation.setSelectedItemId(R.id.action_reservation);
                        break;
                    case 3:
                        binding.toolbarTitle.setText("Hồ sơ");
                        binding.bottomNavigation.setSelectedItemId(R.id.action_user);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }


    @Override
    public void onBackPressed() {

        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }
}