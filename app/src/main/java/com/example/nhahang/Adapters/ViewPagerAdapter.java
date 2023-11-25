package com.example.nhahang.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.nhahang.Fragments.HomeFragment;
import com.example.nhahang.Fragments.MenuFragment;
import com.example.nhahang.Fragments.ReservationFragment;
import com.example.nhahang.Fragments.UserFragment;
import com.example.nhahang.Interfaces.IUpdateTablesListener;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new MenuFragment();
            case 2:
                return new ReservationFragment();
            case 3:
                return new UserFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
