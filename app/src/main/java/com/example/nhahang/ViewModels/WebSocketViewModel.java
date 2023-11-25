package com.example.nhahang.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nhahang.Utils.MyWebSocketClient;
import com.example.nhahang.Utils.Util;

import java.net.URI;

public class WebSocketViewModel extends ViewModel {
   private MyWebSocketClient webSocketClient;
   private MutableLiveData<String> webSocketMessage = new MutableLiveData<>();


    public void connectWebSocket() {
        if (webSocketClient == null) {
            webSocketClient = new MyWebSocketClient(URI.create(Util.WEBSOCKET_URL),this);
            webSocketClient.setViewModel(this);
            webSocketClient.connect();
        }
    }

    public void sendMessage(String message) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
        }
    }
    public LiveData<String> getWebSocketMessage() {
        return webSocketMessage;
    }
    public void updateWebSocketMessage(String message) {
        webSocketMessage.postValue(message);
    }

}
