package com.example.nhahang.BottomSheetDialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.nhahang.Interfaces.INoteOrderItem;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.FragmentNoteProductBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NoteOrderItemFragment extends BottomSheetDialogFragment {
    private FragmentNoteProductBottomSheetBinding binding;
    private OrderItemModel orderItem;
    private MenuModel menuModel;
    private boolean ignoreFormatting;
    private DecimalFormat decimalFormat;
    private OrderItemModel orderItemOlder;
    private INoteOrderItem iNoteOrderItem;



    public NoteOrderItemFragment(OrderItemModel orderItem, MenuModel menuModel) {
        this.orderItem = orderItem;
        this.menuModel = menuModel;
    }

    public void setINoteOrderItem(INoteOrderItem iNoteOrderItem){
        this.iNoteOrderItem = iNoteOrderItem;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = FragmentNoteProductBottomSheetBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(binding.getRoot());
        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        setView();
        setFormatMoney();

        binding.closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        binding.plussIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityText = binding.quantityNumberEt.getText().toString().trim();
                int quantity = Integer.parseInt(quantityText)+1;
                binding.quantityNumberEt.setText(Integer.toString(quantity));
            }
        });
        binding.minusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityText = binding.quantityNumberEt.getText().toString().trim();
                int quantity = Integer.parseInt(quantityText);
                if(quantity > 1)
                {
                    quantity -=1;
                    binding.quantityNumberEt.setText(Integer.toString(quantity));
                }

            }
        });


        binding.cardviewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String discountText = binding.discountEt.getText().toString().trim();
                String noteText = binding.noteEt.getText().toString().trim();
                String quantityText = binding.quantityNumberEt.getText().toString().trim();
                if(!discountText.isEmpty()){
                    if(binding.btnVnd.isChecked()){
                        double discountAmount = Double.parseDouble(discountText.replaceAll("[.,]", ""));
                        orderItem.setDiscount_amount(discountAmount);
                    }
                    if(binding.btnPercent.isChecked()){
                        int discountPercentage = Integer.parseInt(discountText);
                        orderItem.setDiscount_percentage(discountPercentage);
                    }
                }
                if(!noteText.isEmpty()){
                    orderItem.setNote(noteText);
                }
                int quantity = Integer.parseInt(quantityText);
                if(quantity != orderItem.getQuantity()){
                    orderItem.setQuantity(quantity);
                }
                bottomSheetDialog.dismiss();
            }
        });



        binding.btnPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String discountText = binding.discountEt.getText().toString().trim().replaceAll("[.,]", "");
               if(discountText.isEmpty()){
                       return;
               }
               String quantityText = binding.quantityNumberEt.getText().toString().trim();
               int quantity = Integer.parseInt(quantityText);
               String totalPriceText = binding.totalPrice.getText().toString().trim().replaceAll("[.,₫]", "");
               double totalPriceProduct = Double.parseDouble(totalPriceText);
                int percent = Math.round((float) (1 - (totalPriceProduct/(orderItem.getItem_price() * quantity)))*100);
                if(percent == 0) binding.discountEt.setText("");
                else binding.discountEt.setText(String.valueOf(percent));
            }
        });

        binding.btnVnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String discountText = binding.discountEt.getText().toString().trim();
                if(discountText.isEmpty()){
                    return;
                }
                String quantityText = binding.quantityNumberEt.getText().toString().trim();
                int quantity = Integer.parseInt(quantityText);
                String totalPriceText = binding.totalPrice.getText().toString().trim().replaceAll("[.,₫]", "");
                double totalPriceProduct = Double.parseDouble(totalPriceText);
                int discount = (int) (orderItem.getItem_price() - (totalPriceProduct / quantity));
                if(discount != 0) binding.discountEt.setText(String.valueOf(discount));

            }
        });

        ViewTreeObserver.OnGlobalLayoutListener keyboardVisibilityListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                binding.getRoot().getWindowVisibleDisplayFrame(r);
                int screenHeight =  binding.getRoot().getHeight();

                // Calculate the height of the keyboard
                int keypadHeight = screenHeight - r.bottom;

                // If the height of the keyboard is less than a certain threshold, it's considered hidden
                boolean isKeyboardVisible = keypadHeight > screenHeight * 0.15; // Adjust this threshold as needed

                if (isKeyboardVisible) {
                    // The keyboard is currently visible
                    // Handle the event here

                } else {
                    // The keyboard is currently hidden
                    // Handle the event here
                    binding.discountEt.clearFocus();
                    binding.noteEt.clearFocus();
                    binding.quantityNumberEt.clearFocus();
                }
            }
        };
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(keyboardVisibilityListener);


        binding.quantityNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String text = s.toString();
                if(text.isEmpty()){
                    binding.quantityNumberEt.setText("1");
                    binding.quantityNumberEt.setSelection(1);
                }else{
                    binding.quantityNumberEt.setSelection(text.length());
                }
                calculateTotalPriceProduct();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.discountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (ignoreFormatting) {
                    return;
                }
                ignoreFormatting = true;
                // Loại bỏ các ký tự phân tách hàng nghìn và thập phân
                String text = s.toString().replaceAll("[.,]", "");

                try {
                    if(binding.btnVnd.isChecked())
                    {
                        // Chuyển đổi thành số double và định dạng lại theo kiểu tiền tệ Việt Nam
                        double amount = Double.parseDouble(text);
                        if(amount >= orderItem.getItem_price()) amount = orderItem.getItem_price();

                        String formattedAmount = decimalFormat.format(amount);
                        // Cập nhật giá trị mới vào EditText
                        binding.discountEt.setText(formattedAmount);
                        binding.discountEt.setSelection(formattedAmount.length());

                    }
                    if(binding.btnPercent.isChecked()){
                        int percentage = Integer.parseInt(text);
                        if(percentage > 100) percentage = 100;
                        binding.discountEt.setText(String.valueOf(percentage));
                        binding.discountEt.setSelection(String.valueOf(percentage).length());
                    }
                    calculateTotalPriceProduct();
                } catch (NumberFormatException e) {
                    // Xảy ra lỗi khi chuyển đổi giá trị, không làm gì
                }
                ignoreFormatting = false;
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });



        return bottomSheetDialog;
    }

    private void setView() {
        binding.nameProductTv.setText(menuModel.getName());
        Glide.with(requireContext()).load(menuModel.getImg()).into(binding.imageProductIv);
        Util.updateMoneyLabel(binding.priceProductTv,orderItem.getItem_price());
        binding.quantityNumberEt.setText(String.valueOf(orderItem.getQuantity()));
        if(orderItem.getDiscount_percentage() == 0 && orderItem.getDiscount_amount() == 0){
            double totalPrice = orderItem.getItem_price() * orderItem.getQuantity();
            Util.updateMoneyLabel(binding.totalPrice,totalPrice);
        }else{
            if(orderItem.getDiscount_amount() > 0){
                binding.toggleGroupDiscount.check(R.id.btn_vnd);
                Util.updateMoneyEditText(binding.discountEt,orderItem.getDiscount_amount());
                double totalPrice = (orderItem.getItem_price() - orderItem.getDiscount_amount()) * orderItem.getQuantity();
                Util.updateMoneyLabel(binding.totalPrice,totalPrice);
            }
            if(orderItem.getDiscount_percentage() > 0){
                binding.toggleGroupDiscount.check(R.id.btn_percent);
                binding.discountEt.setText(String.valueOf(orderItem.getDiscount_percentage()));
                double totalPrice = (orderItem.getItem_price()*(100 - orderItem.getDiscount_percentage())/100) * orderItem.getQuantity();
                Util.updateMoneyLabel(binding.totalPrice,totalPrice);
            }
        }
        if(orderItem.getNote()!= null && !orderItem.getNote().isEmpty()){
            binding.noteEt.setText(orderItem.getNote());
        }

    }
    private void calculateTotalPriceProduct() {
        String discountText = binding.discountEt.getText().toString().trim();
        if(discountText.isEmpty()){
            discountText = "0";
        }
        String quantityText = binding.quantityNumberEt.getText().toString().trim();
        int quantity = Integer.parseInt(quantityText);
        double totalPrice = 0;
        if(binding.btnVnd.isChecked()){
            double discountAmount = Double.parseDouble(discountText.replaceAll("[.,]", ""));
            totalPrice = (orderItem.getItem_price() - discountAmount) * quantity;
            Util.updateMoneyLabel(binding.totalPrice,totalPrice);
        }
        if (binding.btnPercent.isChecked()){
            int discountPercentage = Integer.parseInt(discountText);
            totalPrice = (orderItem.getItem_price()*(100 - discountPercentage)/100) * quantity;
            Util.updateMoneyLabel(binding.totalPrice,totalPrice);
        }

    }
    private void setFormatMoney() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("#,###",symbols);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(iNoteOrderItem != null){
            iNoteOrderItem.setOnDismissListener();
        }
    }
}
