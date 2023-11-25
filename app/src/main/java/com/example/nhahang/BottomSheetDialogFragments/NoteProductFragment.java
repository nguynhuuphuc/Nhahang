package com.example.nhahang.BottomSheetDialogFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.nhahang.Interfaces.IClickViewInNoteProductListeners;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.DishModel;
import com.example.nhahang.R;
import com.example.nhahang.databinding.FragmentNoteProductBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NoteProductFragment extends BottomSheetDialogFragment {
    private IClickViewInNoteProductListeners iClickViewInNoteProductListeners;
    private DishModel vt;
    private MenuModel model;
    private FragmentNoteProductBottomSheetBinding binding;
    private int quantityProduct;
    private double totalPriceProduct;
    private boolean discountUnit = false;
    private DecimalFormat decimalFormat;
    private boolean ignoreFormatting;


    public NoteProductFragment(MenuModel model, DishModel vt, IClickViewInNoteProductListeners iClickViewInNoteProductListeners){
        this.model = model;
        this.iClickViewInNoteProductListeners = iClickViewInNoteProductListeners;
        this.vt = vt;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        binding = FragmentNoteProductBottomSheetBinding.inflate(getLayoutInflater());
        View viewDialog = binding.getRoot();
        bottomSheetDialog.setContentView(viewDialog);

        setFormatMoney();
        setData();
        binding.closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        binding.cardviewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vt.setDiscountUnit(discountUnit);
                vt.setValueDiscount(binding.discountEt.getText().toString().trim());
                vt.setQuantity(binding.quantityNumberEt.getText().toString().trim());
                vt.setNote(binding.noteEt.getText().toString());
                vt.setTotalPriceProduct(binding.totalPrice.getText().toString().trim());
                iClickViewInNoteProductListeners.onClickListener(null,IClickViewInNoteProductListeners.SAVED_NOTE);
                bottomSheetDialog.dismiss();
            }
        });

        binding.quantityNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                caculateTotalPriceProduct();
            }
        });
        binding.discountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ignoreFormatting) {
                    return;
                }
                ignoreFormatting = true;
                // Loại bỏ các ký tự phân tách hàng nghìn và thập phân
                String text = s.toString().replaceAll("[.,]", "");

                try {
                    // Chuyển đổi thành số double và định dạng lại theo kiểu tiền tệ Việt Nam
                    double amount = Double.parseDouble(text);
                    String formattedAmount = decimalFormat.format(amount);

                    // Cập nhật giá trị mới vào EditText
                    binding.discountEt.setText(formattedAmount);
                    binding.discountEt.setSelection(formattedAmount.length());
                    caculateTotalPriceProduct();
                } catch (NumberFormatException e) {
                    // Xảy ra lỗi khi chuyển đổi giá trị, không làm gì
                }
                ignoreFormatting = false;

            }
        });

        binding.toggleGroupDiscount.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                double price = Double.parseDouble(model.getPrice().toString().trim().replace(",",""));
                if(binding.btnPercent.isChecked()){
                    discountUnit = true;
                    int percent = Math.round((float) (1 - (totalPriceProduct/(price * quantityProduct)))*100);
                    if(percent == 0) binding.discountEt.setText("");
                    else binding.discountEt.setText(String.valueOf(percent));

                }else{
                    discountUnit = false;
                    int discount = (int) (price - (totalPriceProduct / quantityProduct));
                    if(discount != 0) binding.discountEt.setText(String.valueOf(discount));
                    else binding.discountEt.setText("");
                }
                caculateTotalPriceProduct();
            }
        });
        binding.plussIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityProduct = Integer.parseInt(binding.quantityNumberEt.getText().toString()) + 1;
                binding.quantityNumberEt.setText(String.valueOf(quantityProduct));
            }
        });
        binding.minusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = binding.quantityNumberEt.getText().toString();
                if(!number.equals("1")){
                    quantityProduct = Integer.parseInt(binding.quantityNumberEt.getText().toString()) - 1;
                    binding.quantityNumberEt.setText(String.valueOf(quantityProduct));
                }else{
                    iClickViewInNoteProductListeners.onClickListener(null,IClickViewInNoteProductListeners.REMOVE_PRODUCT);
                    bottomSheetDialog.dismiss();
                }
            }
        });

        BottomSheetBehavior behavior = BottomSheetBehavior.from((View)viewDialog.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        return bottomSheetDialog;
    }

    private void setFormatMoney() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("#,###",symbols);
    }

    private void caculateTotalPriceProduct() {
        double price = Double.parseDouble(model.getPrice().replace(",",""));
        if(discountUnit){
            //percent
            float percent;
            String sPercent = binding.discountEt.getText().toString().trim();
            if(sPercent.isEmpty() || sPercent.equals("0")) percent = 1;
            else percent = 1 - (Float.parseFloat(sPercent) / 100);
            totalPriceProduct = price*quantityProduct*percent;
        }else{
            double discount;
            if(binding.discountEt.getText().toString().trim().isEmpty()){
                discount = 0;
            }else {
                discount = Double.parseDouble(binding.discountEt.getText().toString().trim().replace(",",""));
            }
            totalPriceProduct = (price - discount) * quantityProduct;
        }
        binding.totalPrice.setText(decimalFormat.format(totalPriceProduct));

    }


    private void setData() {
        binding.nameProductTv.setText(model.getName());
        Glide.with(requireContext()).load(model.getImg()).into(binding.imageProductIv);
        binding.priceProductTv.setText(model.getPrice());
        binding.quantityNumberEt.setText(vt.getQuantity());
        if(vt.isDiscountUnit()){
            binding.toggleGroupDiscount.check(R.id.btn_percent);
            discountUnit = true;
        }
        if(!vt.getValueDiscount().isEmpty()){
            binding.discountEt.setText(vt.getValueDiscount());
        }
        if(vt.getNote() != null){
            binding.noteEt.setText(vt.getNote());
        }
        quantityProduct = Integer.parseInt(binding.quantityNumberEt.getText().toString().trim());
        caculateTotalPriceProduct();
    }

}
