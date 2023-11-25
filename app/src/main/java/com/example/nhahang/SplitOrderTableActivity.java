package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterViewAnimator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import com.example.nhahang.Adapters.SelectOrderTableAdapter;
import com.example.nhahang.Adapters.SplitOrderTableAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.OrderModel;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivitySplitOrderTableBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplitOrderTableActivity extends AppCompatActivity{
    private ActivitySplitOrderTableBinding binding;
    private List<TableModel> selectionTableOption;
    private List<TableModel> selectNewTableOption;
    private final String NEW_ORDER_TABLE = "Tạo đơn mới";
    private String[] items = {"ABC","DEF","RESCZ","WSCXS","XAASSC","AKDOKASOD","ABC","DEF","RESCZ","WSCXS","XAASSC","AKDOKASOD"};
    private SelectOrderTableAdapter selectOTAdapter, newOTAdapter;
    private List<TableModel> tableModels;
    private ArrayAdapter<String> adapterItems;
    private String splitEnableMessage ="";
    private OrderModel orderModel;
    private SplitOrderTableAdapter splitOrderTableAdapter;
    private List<OrderItemModel> orderItemModels;
    private MyApplication mApp;
    private TableModel splitToTable;
    private AlertDialog dialog, warningDialog;
    private TableModel currentTableModel;
    private List<TableModel> changeTables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplitOrderTableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InProgress(true);

        changeTables = new ArrayList<>();
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        orderModel = (OrderModel) getIntent().getSerializableExtra("order");
        orderItemModels = (List<OrderItemModel>) getIntent().getSerializableExtra("orderitems");
        currentTableModel = (TableModel) getIntent().getSerializableExtra("currentTable");
        mApp = (MyApplication) getApplication();

        selectionTableOption = new ArrayList<>();
        selectNewTableOption = new ArrayList<>();
        getTableSelection();



        TableModel first = new TableModel();
        first.setTable_name(NEW_ORDER_TABLE);
        first.setTable_id(-1);
        first.setIs_occupied(true);
        selectionTableOption.add(first);


        selectOTAdapter = new SelectOrderTableAdapter(this,selectionTableOption);

        newOTAdapter = new SelectOrderTableAdapter(this,selectNewTableOption);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.autoCompleteTv.setAdapter(selectOTAdapter);
        binding.autoCompleteTv.setText(NEW_ORDER_TABLE);




        binding.selectOrderTableTil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOTAdapter.getFilter().filter("");
            }
        });

        binding.autoCompleteTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TableModel model = (TableModel) parent.getItemAtPosition(position);
                if(model.getTable_id() == -1){
                    splitToTable = null;
                    binding.selectNewOrderTableTil.setVisibility(View.VISIBLE);
                    binding.selectNewOrderTableTil.setEnabled(true);
                }else{
                    binding.selectNewOrderTableTil.setVisibility(View.INVISIBLE);
                    binding.selectNewOrderTableTil.setEnabled(false);
                    splitToTable = model;
                }
            }
        });


        binding.doneCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(splitToTable == null){
                    Toast.makeText(mApp, "Bạn phải chọn bàn trước!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isEnableSplit()){
                    buildWarningDialog(getSplitEnableMessage());
                    warningDialog.show();
                    return;
                }
                buildDialog(splitToTable);
                dialog.show();

            }
        });


        binding.autoCompleteTv2.setAdapter(newOTAdapter);
        binding.selectNewOrderTableTil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newOTAdapter.getFilter().filter("");
            }
        });
        binding.autoCompleteTv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                splitToTable = (TableModel) parent.getItemAtPosition(position);
            }
        });

        splitOrderTableAdapter = new SplitOrderTableAdapter(SplitOrderTableActivity.this,orderItemModels,mApp.getMenuModels());

        binding.itemsSplitRv.setLayoutManager(new LinearLayoutManager(SplitOrderTableActivity.this, RecyclerView.VERTICAL,false));
        binding.itemsSplitRv.setAdapter(splitOrderTableAdapter);

    }

    private void getTableSelection() {
        ApiService.apiService.getAllTables(new UserUidRequest(Auth.User_Uid)).enqueue(new Callback<ArrayList<TableModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TableModel>> call, Response<ArrayList<TableModel>> response) {
                if(response.isSuccessful()){
                    selectionTableOption.clear();
                    selectNewTableOption.clear();
                    for (TableModel model : response.body()){
                        if(model.is_occupied() && model.getTable_id() != orderModel.getTable_id()){
                            selectionTableOption.add(model);
                        }else if(model.getTable_id() != orderModel.getTable_id()){
                            selectNewTableOption.add(model);
                        }
                    }
                    selectOTAdapter.notifyDataSetChanged();
                    newOTAdapter.notifyDataSetChanged();
                    InProgress(false);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TableModel>> call, Throwable t) {

            }
        });
    }

    private void buildWarningDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Cảnh báo tách bàn");
        builder.setPositiveButton("Đã rõ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        warningDialog = builder.create();
    }

    private void buildDialog(TableModel tableModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = "Bạn có chắc chắn muốn tách sang "+ tableModel.getTable_name() + " không ?";
        builder.setMessage(message)
                .setTitle("Tách bàn");
        builder.setPositiveButton("Chắc chắn có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postValueToApiService();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
    }

    private void postValueToApiService() {
        binding.doneCv.setVisibility(View.GONE);
        OrderRequest request = new OrderRequest();
        request.setOrderItemModels(orderItemModels);
        request.setOrder_id(orderModel.getOrder_id());
        request.setTable_id(splitToTable.getTable_id());
        request.setUser_uid(Auth.User_Uid);
        ApiService.apiService.splitOrderTable(request).enqueue(new Callback<ArrayList<TableModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TableModel>> call, Response<ArrayList<TableModel>> response) {
                if(response.isSuccessful()){
                    List<TableModel> changeTables = new ArrayList<>();
                    changeTables.addAll(response.body());
                    Intent intentResult = new Intent();
                    intentResult.putExtra("ChangeTables", (Serializable) changeTables);
                    intentResult.putExtra("response", "ChangeOrderTable");
                    notifySplitSuccess();

                    setResult(RESULT_OK,intentResult);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TableModel>> call, Throwable t) {
                Toast.makeText(mApp, "Lỗi server", Toast.LENGTH_SHORT).show();
                binding.doneCv.setVisibility(View.VISIBLE);
            }
        });
    }

    private void notifySplitSuccess() {
        JSONObject object = new JSONObject();
        try {
            object.put("from",Auth.User_Uid);
            JSONObject message = new JSONObject();
            message.put("order_id",currentTableModel.getOrder_id());
            message.put("status","đã tách đơn của " + currentTableModel.getTable_name() + " sang " + splitToTable.getTable_name());
            message.put("old_table_id",currentTableModel.getTable_id());
            message.put("new_table_id",splitToTable.getTable_id());
            object.put("message",message);
            mApp.getWebSocketClient().send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isEnableSplit() {
        boolean isEnable = false;
        long totalQuantity = 0,totalSplitQuan = 0;
        for(OrderItemModel item : orderItemModels){
            totalQuantity += item.getQuantity();
            totalSplitQuan += item.getQuantitySplit();
        }
        if(totalQuantity != totalSplitQuan){
            for (OrderItemModel item : orderItemModels){
                if(item.isEnableSplit()){
                    isEnable = true;
                    break;
                }
            }
            setSplitEnableMessage("Bạn không thể tách khi không có số lượng tách");
        }else{
            setSplitEnableMessage("Bạn không thể tách khi không còn món trong đơn gốc");
            isEnable = false;
        }
        return isEnable;
    }

    private void InProgress(boolean b){
        if(b){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.contentRl.setVisibility(View.GONE);
            return;
        }
        binding.progressBar.setVisibility(View.GONE);
        binding.contentRl.setVisibility(View.VISIBLE);
    }

    public String getSplitEnableMessage() {
        return splitEnableMessage;
    }

    public void setSplitEnableMessage(String splitEnableMessage) {
        this.splitEnableMessage = splitEnableMessage;
    }
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getActivitySimpleName().equals(this.getClass().getSimpleName())){
            return;
        }
        if(event.getChangeTables() == null){
            NotificationModel model = event.getNotificationModel();
            NotificationModel.Message message = model.parseMessage();
        }else {
            getTableSelection();
        }

    }
}