package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;


import com.example.nhahang.Adapters.KitchenViewPagerAdapter;
import com.example.nhahang.Adapters.ViewPagerAdapter;
import com.example.nhahang.databinding.ActivityKitchenMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class KitchenMainActivity extends AppCompatActivity {
    private ActivityKitchenMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKitchenMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setUpViewPager();
        binding.viewPager.setUserInputEnabled(false);

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.order:
                        binding.viewPager.setCurrentItem(0);
                        break;
                    case R.id.user:
                        binding.viewPager.setCurrentItem(1);
                        break;
                }
                return true;
            }
        });

    }
    private void setUpViewPager() {
        KitchenViewPagerAdapter viewPagerAdapter = new KitchenViewPagerAdapter(KitchenMainActivity.this);
        binding.viewPager.setAdapter(viewPagerAdapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        binding.toolbarTitle.setText("Đơn món");
                        binding.bottomNavigation.setSelectedItemId(R.id.order);
                        break;
                    case 1:
                        binding.toolbarTitle.setText("Hồ sơ");
                        binding.bottomNavigation.setSelectedItemId(R.id.user);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }

}