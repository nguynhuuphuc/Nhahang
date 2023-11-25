package com.example.nhahang.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nhahang.Models.TableModel;

public class AttributeWatcherViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isPhoneExistsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPasswordExistsLiveData = new MutableLiveData<>();
    private final MutableLiveData<TableModel> updateTableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isReady = new MutableLiveData<>();

    public MutableLiveData<TableModel> getUpdateTableLiveData() {
        return updateTableLiveData;
    }
    public void setUpdateTableLiveData(TableModel model){
        this.updateTableLiveData.setValue(model);
    }

    public LiveData<Boolean> isReady(){
        return isReady;
    }
    public void  setIsReady(boolean value){
        isReady.setValue(value);
    }

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
