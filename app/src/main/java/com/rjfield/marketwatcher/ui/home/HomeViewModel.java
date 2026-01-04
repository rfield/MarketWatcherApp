package com.rjfield.marketwatcher.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Enter your credentials to begin.");
    }

    public void updateBanner(String s) {
        mText.postValue(s);
    }
    public LiveData<String> getText() {
        return mText;
    }
}