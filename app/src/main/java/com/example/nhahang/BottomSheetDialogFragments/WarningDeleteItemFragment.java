package com.example.nhahang.BottomSheetDialogFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nhahang.Interfaces.IClickViewInWaringDelete;
import com.example.nhahang.databinding.FragmentNoteProductBottomSheetBinding;

import com.example.nhahang.databinding.FragmentWarningDeleteItemBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class WarningDeleteItemFragment extends BottomSheetDialogFragment {
    private FragmentWarningDeleteItemBinding binding;
    private IClickViewInWaringDelete iClickViewInWaringDelete;

    public WarningDeleteItemFragment(IClickViewInWaringDelete iClickViewInWaringDelete){
        this.iClickViewInWaringDelete = iClickViewInWaringDelete;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = FragmentWarningDeleteItemBinding.inflate(getLayoutInflater());
        View viewDialog = binding.getRoot();
        bottomSheetDialog.setContentView(viewDialog);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        binding.acceptTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickViewInWaringDelete.onClick(IClickViewInWaringDelete.ACCEPT);
                bottomSheetDialog.dismiss();
            }
        });

        BottomSheetBehavior behavior = BottomSheetBehavior.from((View)viewDialog.getParent());
        behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        return bottomSheetDialog;
    }
}
