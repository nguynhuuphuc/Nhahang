package com.example.nhahang.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nhahang.Adapters.OrderKitchenViewPagerAdapter;
import com.example.nhahang.R;
import com.example.nhahang.databinding.FragmentOrderKitchenBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderKitchenFragment extends Fragment {
    private FragmentOrderKitchenBinding binding;
    private OrderKitchenViewPagerAdapter viewPagerAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderKitchenBinding.inflate(getLayoutInflater());
        setViewPager();

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 1) {
                    tab.setIcon(R.drawable.map_icon);
                    tab.setText("Sơ đồ");
                } else {
                    tab.setIcon(R.drawable.list_alt_icon);
                    tab.setText("Danh sách");
                }

            }
        }).attach();


        return binding.getRoot();
    }

    private void setViewPager() {
        viewPagerAdapter = new OrderKitchenViewPagerAdapter(this);
        binding.viewPager.setAdapter(viewPagerAdapter);
    }
}
