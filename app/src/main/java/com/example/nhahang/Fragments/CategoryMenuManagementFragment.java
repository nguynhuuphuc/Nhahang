package com.example.nhahang.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nhahang.databinding.FragmentCategoryMenuManagementBinding;


public class CategoryMenuManagementFragment extends Fragment {
    private FragmentCategoryMenuManagementBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryMenuManagementBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}
