package com.example.nhahang.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.nhahang.Fragments.CategoryMenuManagementFragment;
import com.example.nhahang.Fragments.HomeFragment;
import com.example.nhahang.Fragments.MenuFragment;
import com.example.nhahang.Fragments.MenuManagementFragment;
import com.example.nhahang.Fragments.ReservationFragment;
import com.example.nhahang.Fragments.UserFragment;

public class ViewPagerMenuManagementAdapter extends FragmentStateAdapter {
    public ViewPagerMenuManagementAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MenuManagementFragment();
            case 1:
                return new CategoryMenuManagementFragment();
            default:
                return new MenuManagementFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
