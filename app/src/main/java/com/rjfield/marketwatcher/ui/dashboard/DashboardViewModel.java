package com.rjfield.marketwatcher.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rjfield.marketwatcher.models.StockQuote;

import java.util.List;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<StockQuote>>  stockQuotes;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        stockQuotes = new MutableLiveData<>();
        mText.setValue("Click 'START' to begin streaming prices.");
    }

    public void updateBanner(String s) {
        mText.postValue(s);
    }

    public void updateTable(List<StockQuote> quotes) {
        stockQuotes.postValue(quotes);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<StockQuote>> getQuotes() {return stockQuotes;}
}