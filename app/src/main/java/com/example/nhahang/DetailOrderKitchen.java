package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Adapters.DetailOrderKitchenAdapter;
import com.example.nhahang.BottomSheetDialogFragments.ChangeQuantityKitchenOrderItem;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.OrderKitchenModel;
import com.example.nhahang.Models.Requests.OrderKitchenRequest;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Models.Requests.ServerRequest;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.databinding.ActivityDetailOrderKitchenBinding;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailOrderKitchen extends AppCompatActivity {
    private ActivityDetailOrderKitchenBinding binding;
    private OrderKitchenModel orderKitchenModel;
    private List<OrderItemModel> orderItemModels = new ArrayList<>();
    private DetailOrderKitchenAdapter adapter;
    private MyApplication mApp;
    private boolean isConfirm,isServDish;
    private WebSocketClient webSocketClient;
    private boolean isEnableDelete = false;
    private AlertDialog deleteAlertDialog;
    private String status;
    private boolean isListEmpty = false;
    private boolean isMenuVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailOrderKitchenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InProgress(false);

        mApp = (MyApplication) getApplication();
        webSocketClient = mApp.getWebSocketClient();

        orderKitchenModel = (OrderKitchenModel) getIntent().getSerializableExtra("OrderKitchenModel");

        binding.toolbar.setTitle(orderKitchenModel.getTable_name());

        if(orderKitchenModel.getStatus_id() == 1){
            //Chờ xác nhận
            isEnableDelete = true;
            viewConfirm(false);
            viewServDish(false);
        }else if(orderKitchenModel.getStatus_id() == 2){
            //Đã xác nhận
            isEnableDelete = false;
            viewConfirm(true);
        }else if(orderKitchenModel.getStatus_id() == 3){
            //Đã trả món cho quầy
            isEnableDelete = false;
            viewConfirm(true);
            viewServDish(true);
        }
        if(isEnableDelete){
            buildDeleteAlertDialog();
        }

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServDish){
                    orderKitchenModel.setStatus_id(OrderKitchenModel.ORDER_SERV);
                    Intent intentResult = new Intent();
                    intentResult.putExtra("OrderKitchenModel", orderKitchenModel);
                    setResult(RESULT_OK,intentResult);
                    finish();
                }
                if(isConfirm){
                    Intent intentResult = new Intent();
                    orderKitchenModel.setStatus_id(OrderKitchenModel.ORDER_CONFIRM);
                    intentResult.putExtra("OrderKitchenModel", orderKitchenModel);
                    setResult(RESULT_OK,intentResult);
                    finish();
                }
                if(isListEmpty){
                    Intent intentResult = new Intent();
                    intentResult.putExtra("OrderKitchenModel",orderKitchenModel);
                    intentResult.putExtra("action",OrderKitchenModel.ORDER_KITCHEN_EMPTY);
                    setResult(RESULT_OK,intentResult);
                    finish();
                }
                finish();
            }
        });
        // Lấy tham chiếu đến Drawable của navIcon
        Drawable navIcon =  binding.toolbar.getNavigationIcon();

        // Đặt màu cho navIcon
        if (navIcon != null) {
            PorterDuffColorFilter colorFilter = new PorterDuffColorFilter( getColor(R.color.white), PorterDuff.Mode.SRC_ATOP );
            navIcon.setColorFilter(colorFilter);
        }

        binding.productsRv.setLayoutManager(new LinearLayoutManager(DetailOrderKitchen.this,LinearLayoutManager.VERTICAL,false));
        adapter = new DetailOrderKitchenAdapter(DetailOrderKitchen.this,orderItemModels,mApp.getMenuModels());
        adapter.setOnClickItem(new DetailOrderKitchenAdapter.OnClickItem() {
            @Override
            public void onClick(OrderItemModel item,MenuModel dish) {
                ChangeQuantityKitchenOrderItem bottomSheet = new ChangeQuantityKitchenOrderItem(item,dish);
                bottomSheet.show(getSupportFragmentManager(),bottomSheet.getTag());
                bottomSheet.setOnQuantityListener(new ChangeQuantityKitchenOrderItem.OnQuantityListener() {
                    @Override
                    public void onChange(int removeQ,int newQ) {
                        item.setQuantity(newQ);
                        postToChangeQuantity(item,removeQ);
                        bottomSheet.dismiss();

                    }
                });
            }

            @Override
            public void onDelete(OrderItemModel item) {
                for(MenuModel dish : mApp.getMenuModels()){
                    if(dish.getDocumentId().equals(item.getMenu_item_id())){
                        buildDeleteOrderItemAlertDialog(item,dish);
                    }
                }
            }
        });
        adapter.setOnListEmpty(new DetailOrderKitchenAdapter.OnListEmpty() {
            @Override
            public void listEmpty() {
                isListEmpty = true;
            }
        });
        binding.productsRv.setAdapter(adapter);

        getOrderItems();

        binding.servDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InProgress(true);
                OrderRequest orderRequest = new OrderRequest();
                orderRequest.setOrderItemModels(orderItemModels);
                ApiService.apiService.servOrderItemsCheckList(orderRequest).enqueue(new Callback<ArrayList<OrderItemModel>>() {
                    @Override
                    public void onResponse(Call<ArrayList<OrderItemModel>> call, Response<ArrayList<OrderItemModel>> response) {
                        if(response.isSuccessful()){
                            viewServDish(isServDish = true);
                            if(!orderItemModels.isEmpty()) orderItemModels.clear();
                            orderItemModels.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            notifyKitchenServ();
                            InProgress(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<OrderItemModel>> call, Throwable t) {
                        Toast.makeText(mApp, "Lỗi server", Toast.LENGTH_SHORT).show();
                        InProgress(false);

                    }
                });
            }
        });

        binding.confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InProgress(true);
                OrderRequest orderRequest = new OrderRequest();
                orderRequest.setOrderItemModels(orderItemModels);
                ApiService.apiService.confirmOrderItemsCheckList(orderRequest).enqueue(new Callback<ArrayList<OrderItemModel>>() {
                    @Override
                    public void onResponse(Call<ArrayList<OrderItemModel>> call, Response<ArrayList<OrderItemModel>> response) {
                        if(response.isSuccessful()){
                            viewConfirm(isConfirm = true);
                            if(!orderItemModels.isEmpty()) orderItemModels.clear();
                            orderItemModels.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            notifyKitchenConfirm();
                            isMenuVisible = false;
                            invalidateOptionsMenu();
                            InProgress(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<OrderItemModel>> call, Throwable t) {
                        Toast.makeText(mApp, "Lỗi server", Toast.LENGTH_SHORT).show();
                        InProgress(false);

                    }
                });
            }
        });

    }

    private void postToChangeQuantity(OrderItemModel item, int removeQ) {
        InProgress(true);
        ApiService.apiService.kitchenChangeOrderItemQuantity(new ServerRequest(item)).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(mApp, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    status = getStatusOrderItemModel(item,removeQ);
                    notifyKitchenDeleteOrderItems();
                    adapter.notifyItemChanged(orderItemModels.indexOf(item));
                    InProgress(false);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void buildDeleteOrderItemAlertDialog(OrderItemModel item, MenuModel dish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = "Bạn có chắc chắn muốn hủy "+ item.getQuantity()+ " "+ dish.getName()+ " không ?";
        builder.setMessage(message)
                .setTitle("Hủy Đơn");
        builder.setPositiveButton("Chắc chắn có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postToDeleteOrderItem(item);
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

    private void postToDeleteOrderItem(OrderItemModel item) {
        ApiService.apiService.kitchenDeleteOrderItem(new ServerRequest(orderKitchenModel,item)).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(mApp, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    status = getStatusOrderItemModel(item);
                    notifyKitchenDeleteOrderItems();
                    adapter.itemRemoving(item);
                }

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(mApp, "Server err", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getStatusOrderItemModel(OrderItemModel item,int removeQuantity) {
        for(MenuModel dish : mApp.getMenuModels()){
            if(dish.getDocumentId().equals(item.getMenu_item_id())){
                String status = "đã hủy " + removeQuantity + " "+ dish.getName()+
                        " của " + orderKitchenModel.getTable_name();
                return status;
            }
        }
        return null;

    }

    private String getStatusOrderItemModel(OrderItemModel item) {
        for(MenuModel dish : mApp.getMenuModels()){
            if(dish.getDocumentId().equals(item.getMenu_item_id())){
                String status = "đã hủy " + item.getQuantity() + " "+ dish.getName()+
                        " của " + orderKitchenModel.getTable_name();
                return status;
            }
        }
        return null;

    }

    private void notifyKitchenDeleteOrderItems() {
        JSONObject object = new JSONObject();
        try {
            object.put("action","DELETE_ORDER_ITEMS");
            object.put("from", Auth.User_Uid);
            object.put("position","B");
            JSONObject message = new JSONObject();
            message.put("order_id",orderKitchenModel.getOrder_id());
            message.put("status",status);
            object.put("message",message);
            webSocketClient.send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void buildDeleteAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = "Bạn có chắc chắn muốn hủy đơn không ?";
        builder.setMessage(message)
                .setTitle("Hủy Đơn");
        builder.setPositiveButton("Chắc chắn có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postToDeleteOrder();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        deleteAlertDialog = builder.create();
    }

    private void postToDeleteOrder() {
        ApiService.apiService.kitchenDeleteOrder(new ServerRequest(orderKitchenModel)).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(mApp, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    notifyKitchenDeleteOrder();
                    Intent intent = new Intent();
                    intent.putExtra("OrderKitchenModel",orderKitchenModel);
                    intent.putExtra("action",OrderKitchenModel.ORDER_DELETED);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void notifyKitchenDeleteOrder() {
        JSONObject object = new JSONObject();
        try {
            object.put("action","DELETE_ORDER_ITEMS");
            object.put("from", Auth.User_Uid);
            object.put("position","B");
            JSONObject message = new JSONObject();
            message.put("order_id",orderKitchenModel.getOrder_id());
            message.put("status","đã hủy " + orderKitchenModel.getQuantity()+ " món của " + orderKitchenModel.getTable_name());
            object.put("message",message);
            webSocketClient.send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void notifyKitchenServ() {

        JSONObject object = new JSONObject();
        try {
            object.put("from", Auth.User_Uid);
            object.put("position","B");
            JSONObject message = new JSONObject();
            message.put("order_id",orderKitchenModel.getOrder_id());
            message.put("status","trả " + orderKitchenModel.getQuantity()+ " món cho " + orderKitchenModel.getTable_name());
            object.put("message",message);
            webSocketClient.send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void notifyKitchenConfirm() {

        JSONObject object = new JSONObject();
        try {
            object.put("from", Auth.User_Uid);
            object.put("position","B");
            JSONObject message = new JSONObject();
            message.put("order_id",orderKitchenModel.getOrder_id());
            message.put("status","đã xác nhận " + orderKitchenModel.getQuantity()+ " món cho " + orderKitchenModel.getTable_name());
            object.put("message",message);
            webSocketClient.send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getOrderItems() {
        OrderKitchenRequest request = new OrderKitchenRequest(
                orderKitchenModel.getOrder_id(),
                orderKitchenModel.getNotify_time(),
                orderKitchenModel.getStatus_id());

        ApiService.apiService.getOrderItemKitchen(request).enqueue(new Callback<ArrayList<OrderItemModel>>() {
            @Override
            public void onResponse(Call<ArrayList<OrderItemModel>> call, Response<ArrayList<OrderItemModel>> response) {
                if(response.isSuccessful()){
                    if(!orderItemModels.isEmpty()) orderItemModels.clear();
                    orderItemModels.addAll(response.body());
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<ArrayList<OrderItemModel>> call, Throwable t) {

            }
        });
    }




    void viewConfirm(boolean isConfirm){
        if(isConfirm){
            //Đã xác nhận
            binding.confirmOrder.setEnabled(false);
            binding.confirmOrder.setText("Đã xác nhận");
            binding.confirmOrder.setBackgroundResource(R.drawable.radius_light_blue_background);
        }else{
            //Chưa xác nhận chờ được xác nhận
            binding.confirmOrder.setEnabled(true);
            binding.confirmOrder.setText("Xác nhận");
            binding.confirmOrder.setBackgroundResource(R.drawable.radius_sky_blue_background);
        }

    }

    void  viewServDish(boolean isServ){
        if(isServ){
            binding.servDish.setEnabled(false);
            binding.servDish.setText("Đã trả món");
            binding.servDish.setBackgroundResource(R.drawable.radius_light_blue_background);
        }else{
            binding.servDish.setEnabled(true);
            binding.servDish.setText("Trả món");
            binding.servDish.setBackgroundResource(R.drawable.radius_sky_blue_background);
        }
    }
    private void InProgress(boolean isIn){
        if(isIn){
            binding.ProgressBar.setVisibility(View.VISIBLE);
            binding.contentRl.setVisibility(View.GONE);
            return;
        }
        binding.ProgressBar.setVisibility(View.GONE);
        binding.contentRl.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!isEnableDelete) return false;
        getMenuInflater().inflate(R.menu.menu_detail_order_kitchen,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_delete){
            deleteAlertDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        try {
            MenuItem yourMenuItem = menu.findItem(R.id.action_delete);
            // Ẩn hoặc hiển thị menu item tùy thuộc vào trạng thái của biến isMenuVisible
            yourMenuItem.setVisible(isMenuVisible);


        }catch (Exception e){}
        return super.onPrepareOptionsMenu(menu);
    }
}