package com.example.nhahang.Utils;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.nhahang.Interfaces.WebSocketListener;
import com.example.nhahang.TestSocket;
import com.example.nhahang.ViewModels.WebSocketViewModel;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;


public class MyWebSocketClient extends WebSocketClient {
    private WebSocketListener webSocketListener;
    private WebSocketViewModel viewModel;


    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public MyWebSocketClient(URI serverUri,WebSocketViewModel viewModel) {
        super(serverUri);
        this.viewModel = viewModel;
    }
    public void setWebSocketListener(WebSocketListener listener) {
        this.webSocketListener = listener;
    }
    public void setViewModel(WebSocketViewModel viewModel){
        this.viewModel = viewModel;
    }



    @Override
    public void onOpen(ServerHandshake handshakedata) {
        if(isListenerNotNull()){
            webSocketListener.onOpen(handshakedata);
        }

    }

    @Override
    public void onMessage(String message) {
        if(viewModel != null){
            viewModel.updateWebSocketMessage(message);
        }

        if (isListenerNotNull()) {
            webSocketListener.onMessage(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if(isListenerNotNull()){
            webSocketListener.onClose(code,  reason,  remote);
        }

    }

    @Override
    public void onError(Exception ex) {

    }

    private boolean isListenerNotNull(){
        return webSocketListener != null;
    }
}
