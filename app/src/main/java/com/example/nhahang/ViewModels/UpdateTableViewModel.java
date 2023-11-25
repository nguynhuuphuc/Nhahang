package com.example.nhahang.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nhahang.Models.TableModel;

public class UpdateTableViewModel extends ViewModel {
    private final MutableLiveData<TableModel> updateTable = new MutableLiveData<>();

    public LiveData<TableModel> getUpdateTable(){
        return updateTable;
    }
    public void setUpdateTable(TableModel tableModel){
        updateTable.setValue(tableModel);
    }
}
