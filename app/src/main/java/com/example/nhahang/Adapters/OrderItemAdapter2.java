package com.example.nhahang.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Interfaces.IOrderItemDetail;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.OrderItemModel2;
import com.example.nhahang.R;

import java.util.ArrayList;
import java.util.List;

public class OrderItemAdapter2 extends RecyclerView.Adapter<OrderItemAdapter2.ViewHolder> {

    private Context context;
    private List<OrderItemModel2> orderItemModel2s;
    private List<MenuModel> menuModels;
    private FragmentManager fragmentManager;
    private IOrderItemDetail iOrderItemDetail;

    public OrderItemAdapter2(Context context, List<OrderItemModel2> orderItemModel2s,List<MenuModel> menuModels) {
        this.context = context;
        this.orderItemModel2s = orderItemModel2s;
        this.menuModels = menuModels;
    }

    public void setOnChangeOrderItemListener(IOrderItemDetail iOrderItemDetail){
        this.iOrderItemDetail = iOrderItemDetail;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setData(List<OrderItemModel2> orderItemModel2s){
        this.orderItemModel2s = orderItemModel2s;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderItemAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orderitem2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter2.ViewHolder holder, int position) {
        OrderItemModel2 model2 = orderItemModel2s.get(position);
        if(model2 == null ) return;

        holder.status.setText(model2.getStatus());

        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(context,model2.getOrderItemModels(),menuModels,fragmentManager);
        orderItemAdapter.setOnChangeOrderItemListener(new IOrderItemDetail() {
            @Override
            public void onOrderItemChangeListener() {
                if(iOrderItemDetail != null){
                    iOrderItemDetail.onOrderItemChangeListener();
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
        holder.productsRv.setLayoutManager(layoutManager);
        holder.productsRv.setAdapter(orderItemAdapter);


    }

    @Override
    public int getItemCount() {
        if(orderItemModel2s != null ) return  orderItemModel2s.size();
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView status;
        private RecyclerView productsRv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.orderItemsStatusTv);
            productsRv = itemView.findViewById(R.id.productsRv);
        }
    }
}
