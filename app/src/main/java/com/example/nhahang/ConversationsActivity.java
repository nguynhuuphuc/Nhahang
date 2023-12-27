package com.example.nhahang;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.example.nhahang.Adapters.ConversationAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.ConversationModel;
import com.example.nhahang.Models.MessageModel;
import com.example.nhahang.databinding.ActivityConversationsBinding;
import com.google.protobuf.Api;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConversationsActivity extends AppCompatActivity {
    private ActivityConversationsBinding binding;
    private MyApplication mApp;
    private List<ConversationModel> conversationModelList;
    private ConversationAdapter conversationAdapter;
    private ActivityResultLauncher<Intent> launcher;
    private boolean isKeyboardVisible;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConversationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mApp = (MyApplication) getApplication();
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setToobarNavIcon();
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listenerScreenChange();
        launcherRegister();
        conversationModelList = new ArrayList<>();
        conversationAdapter = new ConversationAdapter(ConversationsActivity.this,conversationModelList,mApp.getEmployees());
        conversationAdapter.setOnClickItemListener(new ConversationAdapter.OnClickItemListener() {
            @Override
            public void onClick(ConversationModel conversation) {
                Intent intent = new Intent(ConversationsActivity.this,MessageActivity.class);
                intent.putExtra("conversation",conversation);
                startActivityWithAnim(intent);

                //clicked item
            }
        });
        binding.conversationsRv.setLayoutManager(new LinearLayoutManager(ConversationsActivity.this, RecyclerView.VERTICAL,false));
        binding.conversationsRv.setAdapter(conversationAdapter);
        getConversations();

        binding.conversationsRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isKeyboardVisible){
                    hideKeyboard();
                }

                return false;
            }
        });

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                conversationAdapter.getFilter().filter(s.toString());
            }
        });

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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.conversationsRv.getWindowToken(), 0);
            binding.searchEt.clearFocus();
        }
    }

    private void startActivityWithAnim(Intent intent) {
        launcher.launch(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.no_animation);
    }

    private void launcherRegister() {
        launcher =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == RESULT_OK ){
                    Intent data = o.getData();
                    ConversationModel model = (ConversationModel) data.getSerializableExtra("conversation");
                    conversationAdapter.updateItemConversationRead(model.getId());
                }
            }
        });
    }

    private void getConversations() {
        if(!conversationModelList.isEmpty()){
            conversationModelList.clear();
        }
        ApiService.apiService.getConversations().enqueue(new Callback<ArrayList<ConversationModel>>() {
            @Override
            public void onResponse(Call<ArrayList<ConversationModel>> call, Response<ArrayList<ConversationModel>> response) {
                if(response.isSuccessful()){
                    conversationModelList.addAll(response.body());
                    conversationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ConversationModel>> call, Throwable t) {

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
       conversationAdapter.updateItemConversation(message);
    }
}