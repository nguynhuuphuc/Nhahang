package com.example.nhahang.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.OrderModel;
import com.example.nhahang.R;

import java.util.List;


public class SplitOrderTableAdapter extends RecyclerView.Adapter<SplitOrderTableAdapter.ViewHolder> {
    private Context context;
    private List<OrderItemModel> orderItemModels;
    private List<MenuModel> menuModels;
    private ISplitTextChange iSplitTextChange;


    public interface ISplitTextChange{
        public void onChangeListener(int position,int quantity);
    }


    public SplitOrderTableAdapter(Context context, List<OrderItemModel> orderItemModels,List<MenuModel> menuModels) {
        this.context = context;
        this.orderItemModels = orderItemModels;
        this.menuModels = menuModels;
    }

    public void setISplitTextChange(ISplitTextChange iSplitTextChange){
        this.iSplitTextChange = iSplitTextChange;
    }

    @NonNull
    @Override
    public SplitOrderTableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_split_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SplitOrderTableAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final boolean ignored = false;
        holder.stt.setText(String.valueOf(position+1));
        OrderItemModel model = orderItemModels.get(position);
        holder.splitQuantityEt.setText("0");
        if(model == null) return;
        for(MenuModel item : menuModels){
            if(item.getDocumentId().equals(model.getMenu_item_id())){
                holder.nameTv.setText(item.getName());
            }
        }
        holder.quantityEt.setText(String.valueOf(model.getQuantity()));
        holder.quantityEt.setEnabled(false);
        holder.quantityEt.setTextColor(context.getColor(R.color.black));

        holder.splitPlusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.quantityEt.getText().toString().equals("0")){
                    return;
                }
                int splitQuan = Integer.parseInt(holder.splitQuantityEt.getText().toString()) + 1;
                holder.splitQuantityEt.setText(String.valueOf(splitQuan));
            }
        });

        holder.splitQuantityEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    holder.quantityEt.setText(String.valueOf(model.getQuantity()));
                    holder.splitQuantityEt.setText("0");
                    holder.splitQuantityEt.setSelection(1);
                }
                removeLeadingZeros(s);
                // Cập nhật giá trị của E1 theo công thức E1 = E1 - E2
                updateQuantityEt(model.getQuantity(),holder.quantityEt,holder.splitQuantityEt);
                //Lưu giá tri tách
                int quantity = 0;
                if(!s.toString().isEmpty()){
                    quantity = Integer.parseInt(s.toString());
                }
                if(quantity == 0){
                    model.setEnableSplit(false);
                }else{
                    model.setEnableSplit(true);
                    model.setQuantitySplit(quantity);
                }

            }
        });

        holder.splitMinusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.splitQuantityEt.getText().toString().equals("0")){
                    return;
                }
                int splitQuan = Integer.parseInt(holder.splitQuantityEt.getText().toString()) - 1;
                holder.splitQuantityEt.setText(String.valueOf(splitQuan));
            }
        });


    }

    private void removeLeadingZeros(Editable s) {
        while (s.length() > 1 && s.charAt(0) == '0') {
            s.delete(0, 1);
        }
    }

    private void updateQuantityEt(final int modelQuan, EditText quantityEt, EditText splitQuantityEt) {
        // Lấy giá trị của E1 và E2
        String valueE2 = splitQuantityEt.getText().toString();
        
        if (valueE2.isEmpty()) {
            valueE2 = "0";
        }

        int result = modelQuan - Integer.parseInt(valueE2);
        if(result < 0){
            result = 0;
            splitQuantityEt.setText(String.valueOf(modelQuan));
            splitQuantityEt.setSelection(String.valueOf(modelQuan).length());
        }
        quantityEt.setText(String.valueOf(result));

    }


    @Override
    public int getItemCount() {
        if(orderItemModels != null) return orderItemModels.size();
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTv, stt;
        private EditText quantityEt,splitQuantityEt;
        private ImageView plusIv,minusIv,splitPlusIv, splitMinusIv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.itemNameTv);
            stt = itemView.findViewById(R.id.stt);
            quantityEt = itemView.findViewById(R.id.quantityNumberEt);
            splitQuantityEt = itemView.findViewById(R.id.quantitySplitNumberEt);
            plusIv = itemView.findViewById(R.id.plussIv);
            minusIv = itemView.findViewById(R.id.minusIv);
            splitPlusIv = itemView.findViewById(R.id.plussSplitIv);
            splitMinusIv = itemView.findViewById(R.id.minusSplitIv);
        }
    }
}
