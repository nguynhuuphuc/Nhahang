package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.MySharedPreferences;
import com.example.nhahang.Utils.MyWebSocketClient;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivitySplashBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    private MyApplication myApp;
    private MyWebSocketClient webSocketClient;
    private List<MenuModel> menuModels;
    private FirebaseFirestore firestore;
    private boolean isConnectWebsocket;
    private boolean isGetMenu;

    public boolean isConnectWebsocket() {
        return isConnectWebsocket;
    }

    public void setConnectWebsocket(boolean connectWebsocket) {
        isConnectWebsocket = connectWebsocket;
        startMain();
    }

    public boolean isGetMenu() {
        return isGetMenu;
    }

    public void setGetMenu(boolean getMenu) {
        isGetMenu = getMenu;
        startMain();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myApp = (MyApplication) getApplication();
        webSocketClient = myApp.getWebSocketClient();
        menuModels = myApp.getMenuModels();

        Auth.User_Uid = MySharedPreferences.getUserUid(this);
        Auth.PhoneNumber = MySharedPreferences.getPhone(this);


        if (webSocketClient == null)  {
            // Create a new WebSocket client and establish a connection
            try {
                webSocketClient = new MyWebSocketClient(new URI(Util.WEBSOCKET_URL));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            webSocketClient.connect();
            // Save the WebSocket client to the Application class
            myApp.setWebSocketClient(webSocketClient);
            setConnectWebsocket(true);
        }else{
            setConnectWebsocket(true);
        }
        if(menuModels == null){
            menuModels = new ArrayList<>();
            firestore = FirebaseFirestore.getInstance();
            firestore.collection("menu").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (DocumentSnapshot doc : task.getResult()){
                                    MenuModel model = doc.toObject(MenuModel.class);
                                    assert model != null;
                                    model.setDocumentId(doc.getId());
                                    menuModels.add(model);
                                }
                                myApp.setMenuModels(menuModels);
                                setGetMenu(true);
                            }
                        }
                    });
        }else{
            setGetMenu(true);
        }
    }

    private void startMain() {
        if(isConnectWebsocket && isGetMenu){
            authSigned();
        }
    }
    private void authSigned() {
        UserUidRequest userUidRequest = new UserUidRequest(Auth.User_Uid);
        ApiService.apiService.getEmployee(userUidRequest).enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if(response.isSuccessful()){
                    Employee employee = response.body();
                    assert employee != null;
                    MySharedPreferences.saveUserUid(getApplication(),Auth.User_Uid);
                    MySharedPreferences.savePhone(getApplication(),Auth.PhoneNumber);
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }
            }
            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                finish();
            }
        });

    }

}