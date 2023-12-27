package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhahang.Adapters.MessageAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.ConversationModel;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.IgnoreEvent;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.MessageModel;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.Requests.ServerRequest;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.databinding.ActivityMessageBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    private ActivityMessageBinding binding;
    private ConversationModel conversation;
    private boolean isKeyboardVisible;
    private MessageAdapter messageAdapter;
    private List<MessageModel> messageModelList;
    private MyApplication mApp;
    private List<Employee> employees;
    private Employee authEmployee;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolBar);
        mApp = (MyApplication) getApplication();
        employees = mApp.getEmployees();
        setToobarNavIcon();
        listenerScreenChange();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.sendMessageBtn.setVisibility(View.GONE);
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        for (Employee employee : employees){
            if(employee.getUser_uid().equals(Auth.User_Uid)){
                authEmployee = employee;
                break;
            }
        }
        conversation = (ConversationModel) getIntent().getSerializableExtra("conversation");
        readMessages();
        binding.toolbarTitle.setText(conversation.getFull_name());
        binding.conversationRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isKeyboardVisible){
                    hideKeyboard();
                }
                return false;
            }
        });
        EventBus.getDefault().post(new IgnoreEvent(conversation.getId(),MessageActivity.class.getSimpleName()));
        

        binding.sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send message
                sendMessage();
            }
        });



        binding.messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewSendBtn(!s.toString().trim().isEmpty());
            }
        });


        messageModelList = new ArrayList<>();
        messageAdapter = new MessageAdapter(employees);
        binding.conversationRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        binding.conversationRv.setAdapter(messageAdapter);
        getConversation();
    }

    private void readMessages() {
        ApiService.apiService.readMessages(new ServerRequest(conversation.getId())).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
        Intent intentResult = new Intent();
        conversation.setUn_read(0);
        intentResult.putExtra("conversation",conversation);
        setResult(RESULT_OK,intentResult);
    }

    private void sendMessage() {
        String content = binding.messageEt.getText().toString().trim();

        JSONObject object = new JSONObject();
        try {
            object.put("isCustomer",true);
            object.put("conversation_id",conversation.getId());
            object.put("employee",authEmployee.toJson());
            object.put("from", Auth.User_Uid);
            object.put("content",content);
            JSONObject message = new JSONObject();
            message.put("status","đã gửi tin nhắn") ;
            object.put("message",message);
            object.put("action","CONVERSATION");
            mApp.getWebSocketClient().send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MessageModel messageModel = new MessageModel(authEmployee.getEmployee_id(),content);
        messageModelList.add(messageModel);
        messageAdapter.notifyDataSetChanged();
        binding.conversationRv.scrollToPosition(messageModelList.size()-1);
        binding.messageEt.setText("");
    }

    private void getConversation() {
        ApiService.apiService.getMessages(new ServerRequest(new Date(),conversation.getId())).enqueue(new Callback<ArrayList<MessageModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MessageModel>> call, Response<ArrayList<MessageModel>> response) {
                if(response.isSuccessful()){
                    messageModelList.addAll(response.body());
                    messageAdapter.setData(messageModelList);
                    binding.conversationRv.scrollToPosition(messageModelList.size()-1);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MessageModel>> call, Throwable t) {

            }
        });
    }

    private void viewSendBtn(boolean isView){
        if(isView){
            binding.sendMessageBtn.setVisibility(View.VISIBLE);
            //binding.selectPictureBtn.setVisibility(View.GONE);
            return;
        }
        //binding.selectPictureBtn.setVisibility(View.VISIBLE);
        binding.sendMessageBtn.setVisibility(View.GONE);
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.conversationRv.getWindowToken(), 0);
            binding.messageEt.clearFocus();
        }
    }
    private void listenerScreenChange() {
        // Lắng nghe sự kiện thay đổi trạng thái của layout
        binding.getRoot().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int heightDiff = binding.getRoot().getRootView().getHeight() - binding.getRoot().getHeight();

                if (heightDiff > 100) { // Điều kiện có thể thay đổi tùy thuộc vào thiết lập của bạn
                    if (!isKeyboardVisible) {
                        // Bàn phím hiển thị
                        isKeyboardVisible = true;
                    }
                } else {
                    if (isKeyboardVisible) {
                        // Bàn phím ẩn đi
                        isKeyboardVisible = false;
                    }
                }

                return true;
            }
        });
    }
    private void setToobarNavIcon() {
        Drawable navIcon = binding.toolBar.getNavigationIcon();
        // Tint the navigation icon with the desired color.
        if (navIcon != null) {
            navIcon = DrawableCompat.wrap(navIcon).mutate(); // Ensure the original drawable is not modified.
            DrawableCompat.setTint(navIcon, ContextCompat.getColor(this, R.color.white)); // Replace R.color.your_color with your color resource.
            binding.toolBar.setNavigationIcon(navIcon);
        }
    }

    @Override
    public void finish() {
        IgnoreEvent event = new IgnoreEvent(conversation.getId(),MessageActivity.class.getSimpleName(),false);
        EventBus.getDefault().post(event);
        super.finish();
        overridePendingTransition(R.anim.no_animation,R.anim.slide_out_right);
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
    public void onMessageEvent(MessageModel message) {
        if(message.getConversation_id() == conversation.getId()){
            if(message.getSender_id() != authEmployee.getEmployee_id()){
                messageModelList.add(message);
                int lastPosition = messageModelList.indexOf(message);
                messageAdapter.notifyItemChanged(lastPosition);
                binding.conversationRv.scrollToPosition(lastPosition);
            }

        }
    }
}