package com.example.nhahang.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.PaidOrderModel;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Util;


import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class PaidOrdersManagementAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<PaidOrderModel> paidOrderModels;
    private List<Employee> employees;

    public void setData(List<PaidOrderModel> paidOrderModels, List<Employee> employees){
        this.paidOrderModels = paidOrderModels;
        this.employees = employees;
        notifyDataSetChanged();
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
            convertView.setTag(itemViewHolder);
        }else{
            itemViewHolder = (ItemViewHolder) convertView.getTag();
        }
        PaidOrderModel model = paidOrderModels.get(position);
        itemViewHolder.timeTv.setText(Util.TimeFormatting(model.getPaid_time()));
        itemViewHolder.paidOrderIdTv.setText(String.valueOf(model.getOrder_id()));
        Util.updateMoneyLabel(itemViewHolder.totalAmountTv,model.getTotal_amount());
        itemViewHolder.quantityTv.setText(String.valueOf(model.getTotal_quantity()));
        itemViewHolder.paymentNameTv.setText(model.getPayment_method_name());


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
    }
}
