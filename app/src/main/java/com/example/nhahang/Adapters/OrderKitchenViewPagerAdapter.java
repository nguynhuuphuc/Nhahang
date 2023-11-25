package com.example.nhahang.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.nhahang.Fragments.HomeFragment;
import com.example.nhahang.Fragments.ListOrderFragment;
import com.example.nhahang.Fragments.MapKitchenFragment;
import com.example.nhahang.Fragments.OrderKitchenFragment;

public class OrderKitchenViewPagerAdapter extends FragmentStateAdapter {


    public OrderKitchenViewPagerAdapter(@NonNull OrderKitchenFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ListOrderFragment();
            case 1:
                return new MapKitchenFragment();
            default :
                return new ListOrderFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
