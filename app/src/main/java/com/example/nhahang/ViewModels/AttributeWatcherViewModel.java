package com.example.nhahang.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AttributeWatcherViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isPhoneExistsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPasswordExistsLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getIsPhoneExistsLiveData() {
        return isPhoneExistsLiveData;
    }

    public LiveData<Boolean> getIsPasswordExistsLiveData() {
        return isPasswordExistsLiveData;
    }

    public void setIsPhoneExistsLiveData(boolean value){
        isPhoneExistsLiveData.setValue(value);
    }
    public void setIsPasswordExistsLiveData(boolean value){
        isPasswordExistsLiveData.setValue(value);
    }
}
