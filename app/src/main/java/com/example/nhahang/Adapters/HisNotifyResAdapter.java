package com.example.nhahang.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.OrderItemsHisModel;
import com.example.nhahang.MyApplication;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Util;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class HisNotifyResAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<OrderItemsHisModel> orderItemsHisModels;
    private List<MenuModel> menuModels;
    private MyApplication myApp;
    private List<Employee> employees;

    public void setData(List<OrderItemsHisModel> orderItemsHisModels,MyApplication myApp){
        this.orderItemsHisModels = orderItemsHisModels;
        this.myApp = myApp;
        this.menuModels = myApp.getMenuModels();
        this.employees = myApp.getEmployees();
        notifyDataSetChanged();
    }

    @Override
    public View getHeaderView(int position, View view, ViewGroup parent) {
        HeaderViewHolder headerViewHolder;
        if (view == null){
            headerViewHolder = new HeaderViewHolder();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_his_header,parent,false);
            headerViewHolder.dateHeaderTv = view.findViewById(R.id.dateHeaderTv);
            view.setTag(headerViewHolder);
        }else{
            headerViewHolder = (HeaderViewHolder) view.getTag();
        }
        headerViewHolder.dateHeaderTv.setText(orderItemsHisModels.get(position).getDateHeader());
        return view;
    }

    @Override
    public long getHeaderId(int position) {
        return orderItemsHisModels.get(position).getDateHeader().hashCode();
    }

    @Override
    public int getCount() {
        if(orderItemsHisModels != null){
            return orderItemsHisModels.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return orderItemsHisModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ItemViewHolder itemViewHolder;
        if(view == null){
            itemViewHolder = new ItemViewHolder();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orderitem_history,parent,false);
            itemViewHolder.time = view.findViewById(R.id.timeTv );
            itemViewHolder.changed_by = view.findViewById(R.id.changed_byTv);
            itemViewHolder.menu_item = view.findViewById(R.id.menuItemTv);
            view.setTag(itemViewHolder);
        }else {
            itemViewHolder = (ItemViewHolder) view.getTag();
        }
        OrderItemsHisModel model = orderItemsHisModels.get(position);

        for(Employee employee : employees){
            if(employee.getUser_uid().equals(model.getChanged_by())){
                itemViewHolder.changed_by.setText(employee.getFull_name());
                break;
            }
        }
        itemViewHolder.time.setText(Util.TimeFormatting(model.getTimestamp()));
        StringBuilder menu_itemS = new StringBuilder();
        for(OrderItemsHisModel.Item item : model.getItems()){
            String nameMenuItem = "";
            for (MenuModel menuItem : menuModels){
                if(menuItem.getDocumentId().equals(item.getMenu_item_id())){
                    nameMenuItem = menuItem.getName();
                    break;
                }
            }
            if(item.getAction_type().equals("add")){
                menu_itemS.append("+").append(item.getQuantity()).append(" ").append(nameMenuItem).append("\n");
            }else {
                menu_itemS.append("-").append(item.getQuantity()).append(" ").append(nameMenuItem).append("\n");
            }
        }
        itemViewHolder.menu_item.setText(menu_itemS.toString());

        return view;
    }

    public class HeaderViewHolder{
        private TextView dateHeaderTv;
    }
    public class ItemViewHolder{
        private TextView time,changed_by,menu_item;
    }
}
