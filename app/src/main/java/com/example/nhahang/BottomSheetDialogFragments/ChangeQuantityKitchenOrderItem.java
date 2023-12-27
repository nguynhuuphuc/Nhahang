package com.example.nhahang.BottomSheetDialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.nhahang.Interfaces.IClickViewInWaringDelete;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.FragmentChangeQuantityKitchenOrderItemBinding;
import com.example.nhahang.databinding.FragmentWarningDeleteItemBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class ChangeQuantityKitchenOrderItem extends BottomSheetDialogFragment {
    private FragmentChangeQuantityKitchenOrderItemBinding binding;
    private OrderItemModel item;
    private MenuModel dish;
    private OnQuantityListener onQuantityListener;
    private int newQ,removeQ;

    public int getNewQ() {
        return newQ;
    }

    public void setNewQ(int newQ) {
        this.newQ = newQ;
    }

    public int getRemoveQ() {
        return removeQ;
    }

    public void setRemoveQ(int removeQ) {
        this.removeQ = removeQ;
    }

    public ChangeQuantityKitchenOrderItem(OrderItemModel item, MenuModel dish) {
        this.item = item;
        this.dish = dish;
    }

    public void setOnQuantityListener(OnQuantityListener onQuantityListener) {
        this.onQuantityListener = onQuantityListener;
    }

    public interface OnQuantityListener{
        void onChange(int removeQ,int newQ);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = FragmentChangeQuantityKitchenOrderItemBinding.inflate(getLayoutInflater());
        View viewDialog = binding.getRoot();
        bottomSheetDialog.setContentView(viewDialog);

        Glide.with(this).load(dish.getImg()).into(binding.imageProductIv);
        binding.nameProductTv.setText(dish.getName());
        Util.updateMoneyLabel(binding.priceProductTv,item.getItem_price());
        binding.quantityEt.setText(String.valueOf(item.getQuantity()));

        binding.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQ = Integer.parseInt(binding.quantityEt.getText().toString().trim()) + 1;
                if(newQ > item.getQuantity()){

                    return;
                }
                binding.quantityEt.setText(String.valueOf(newQ));
            }
        });

        binding.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQ = Integer.parseInt(binding.quantityEt.getText().toString().trim()) - 1;
                if(newQ == 0){
                    return;
                }
                binding.quantityEt.setText(String.valueOf(newQ));
            }
        });

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        binding.acceptTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQ = Integer.parseInt(binding.quantityEt.getText().toString().trim());
                int removeQ = item.getQuantity() - newQ;
                if(removeQ != 0){
                    //remove
                    setNewQ(newQ);
                    setRemoveQ(removeQ);
                    buildDeleteOrderItemAlertDialog();
                    return;
                }
                bottomSheetDialog.dismiss();

            }
        });


        BottomSheetBehavior behavior = BottomSheetBehavior.from((View)viewDialog.getParent());
        behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        return bottomSheetDialog;
    }

    private void buildDeleteOrderItemAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        String message = "Bạn có chắc chắn muốn thay đổi số lượng của "+ dish.getName()+ " không ?";
        builder.setMessage(message)
                .setTitle("Thay đổi số lượng");
        builder.setPositiveButton("Chắc chắn có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onQuantityListener.onChange(removeQ,newQ);
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
