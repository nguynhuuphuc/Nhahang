package com.example.nhahang;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Interfaces.WebSocketListener;
import com.example.nhahang.Utils.MyWebSocketClient;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityTestSocketBinding;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class TestSocket extends AppCompatActivity implements WebSocketListener{
    private ActivityTestSocketBinding binding;
    private MyWebSocketClient myWebSocketClient;
    private Timer reconnectionTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestSocketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        

        try {
            myWebSocketClient = new MyWebSocketClient(new URI(Util.WEBSOCKET_URL),null);


        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        myWebSocketClient.connect();


        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( myWebSocketClient != null && myWebSocketClient.isOpen()){
                    myWebSocketClient.send(binding.input.getText().toString());
                }

            }
        });

        binding.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(myWebSocketClient != null && myWebSocketClient.isClosed()){
                  myWebSocketClient.reconnect();
              }
            }
        });

        myWebSocketClient.setWebSocketListener(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myWebSocketClient != null){
            myWebSocketClient.close();
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        binding.connect.setEnabled(false);
    }

    @Override
    public void onMessage(String message) {


    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if(remote){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(myWebSocketClient != null && myWebSocketClient.isClosed()){
                        try {
                            Thread.sleep(3000);
                            myWebSocketClient.reconnect();
                        } catch (InterruptedException ignored) {
                        }

                    }
                }
            });
        }

    }

    @Override
    public void onError(Exception ex) {

    }
}