package com.example.nhahang.Abstracts;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager linearLayoutManager;

    public PaginationScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = linearLayoutManager.getChildCount();
        int totalItemCount = linearLayoutManager.getItemCount();
        int fistVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

        if(isLoading() || isLastPage()){
            return;
        }

        if(fistVisibleItemPosition >= 0 && visibleItemCount + fistVisibleItemPosition >= totalItemCount){
            loadMoreItem();
        }
    }

    public abstract void loadMoreItem();
    public abstract boolean isLoading();
    public abstract boolean isLastPage();
}
