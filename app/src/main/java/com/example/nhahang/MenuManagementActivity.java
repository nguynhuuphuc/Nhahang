package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.nhahang.Adapters.ItemInMenuManagementAdapter;
import com.example.nhahang.Adapters.ViewPagerAdapter;
import com.example.nhahang.Adapters.ViewPagerMenuManagementAdapter;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.databinding.ActivityMenuManagementBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MenuManagementActivity extends AppCompatActivity {

    private ActivityMenuManagementBinding binding;
    private SearchView searchView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        setUpViewPager();
        binding.viewPager.setUserInputEnabled(false);
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_products:
                        binding.viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_type_products:
                        binding.viewPager.setCurrentItem(1);
                        break;
                }
                return true;
            }
        });

    }
    private void setUpViewPager() {
        ViewPagerMenuManagementAdapter viewPagerAdapter = new ViewPagerMenuManagementAdapter(MenuManagementActivity.this);
        binding.viewPager.setAdapter(viewPagerAdapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        binding.bottomNavigation.setSelectedItemId(R.id.action_products);
                        break;
                    case 1:
                        binding.bottomNavigation.setSelectedItemId(R.id.action_type_products);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }

}