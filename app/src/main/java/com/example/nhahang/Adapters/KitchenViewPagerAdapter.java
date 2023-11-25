package com.example.nhahang.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.nhahang.Fragments.OrderKitchenFragment;
import com.example.nhahang.Fragments.UserFragment;

public class KitchenViewPagerAdapter extends FragmentStateAdapter {

    public KitchenViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new OrderKitchenFragment();
            case 1:
                return new UserFragment();
            default:
                return new OrderKitchenFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
