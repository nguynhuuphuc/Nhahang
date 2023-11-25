package com.example.nhahang.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CheckVerifyViewModel extends ViewModel {
    private static final MutableLiveData<Boolean> isVerify = new MutableLiveData<>();

    public LiveData<Boolean> getIsVerify() {
        return isVerify;
    }

    public void setIsVerify(boolean value) {
        isVerify.setValue(value);
    }
}
