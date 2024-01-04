package com.example.nhahang.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.PaidOrderModel;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Util;


import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class PaidOrdersManagementAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<PaidOrderModel> paidOrderModels;
    private List<Employee> employees;
    private OnItemClickListener onItemClickListener;

    public void setData(List<PaidOrderModel> paidOrderModels, List<Employee> employees){
        this.paidOrderModels = paidOrderModels;
        this.employees = employees;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onClicked(PaidOrderModel paidOrderModel);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder headerViewHolder;
        if(convertView == null){
            headerViewHolder = new HeaderViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_his_header,parent,false);
            headerViewHolder.dateHeaderTv  = convertView.findViewById(R.id.dateHeaderTv);
            convertView.setTag(headerViewHolder);
        }else{
            headerViewHolder = (HeaderViewHolder) convertView.getTag();
        }
        headerViewHolder.dateHeaderTv.setText(paidOrderModels.get(position).getDateHeader());
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return paidOrderModels.get(position).getDateHeader().hashCode();
    }

    @Override
    public int getCount() {
        if(paidOrderModels != null){
            return paidOrderModels.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return paidOrderModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder itemViewHolder;
        if(convertView == null){
            itemViewHolder = new ItemViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paid_order,parent,false);
            itemViewHolder.timeTv = convertView.findViewById(R.id.timeTv);
            itemViewHolder.paidOrderIdTv = convertView.findViewById(R.id.paidOrderIdTv);
            itemViewHolder.createdByTv = convertView.findViewById(R.id.createdByTv);
            itemViewHolder.paymentNameTv = convertView.findViewById(R.id.paymentNameTv);
            itemViewHolder.quantityTv = convertView.findViewById(R.id.quantityTv);
            itemViewHolder.totalAmountTv = convertView.findViewById(R.id.totalAmountTv);
            itemViewHolder.cardViewItem = convertView.findViewById(R.id.cardViewItem);
            convertView.setTag(itemViewHolder);
        }else{
            itemViewHolder = (ItemViewHolder) convertView.getTag();
        }
        PaidOrderModel model = paidOrderModels.get(position);
        itemViewHolder.timeTv.setText(Util.TimeFormatting(model.getPaid_time()));
        itemViewHolder.paidOrderIdTv.setText(String.valueOf(model.getOrder_id()));
        double total = 0;
        if(model.getDiscount_amount() > 0){
            total = model.getTotal_amount() - model.getDiscount_amount();
        }else
        if(model.getDiscount_percentage() > 0){
            total = Util.tinhTienSauGiamGia(model.getTotal_amount(),model.getDiscount_percentage());
        }
        if (total != 0){
            Util.updateMoneyLabel(itemViewHolder.totalAmountTv,total);
        }else{
            Util.updateMoneyLabel(itemViewHolder.totalAmountTv,model.getTotal_amount());
        }

        itemViewHolder.quantityTv.setText(String.valueOf(model.getTotal_quantity()));
        itemViewHolder.paymentNameTv.setText(model.getPayment_method_name());
        itemViewHolder.cardViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onClicked(model);
                }
            }
        });



        for(int i = 0; i < employees.size(); i++){
            if(employees.get(i).getUser_uid().equals(model.getCreated_by())){
                itemViewHolder.createdByTv.setText(employees.get(i).getFull_name());
                break;
            }
            int j = employees.size()-1 -i;
            if(employees.get(j).getUser_uid().equals(model.getCreated_by())){
                itemViewHolder.createdByTv.setText(employees.get(j).getFull_name());
                break;
            }
        }




        return convertView;
    }

    public class HeaderViewHolder{
        private TextView dateHeaderTv;
    }
    public class ItemViewHolder{
        private TextView timeTv,paidOrderIdTv,createdByTv,paymentNameTv,quantityTv,totalAmountTv;
        private CardView cardViewItem;
    }
}
