package com.rjfield.marketwatcher.ui.dashboard;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rjfield.marketwatcher.R;
import com.rjfield.marketwatcher.databinding.ActivityMainBinding;
import com.rjfield.marketwatcher.models.AssetQuote;

import java.util.List;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<AssetQuote>> assetQuotes;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        assetQuotes = new MutableLiveData<>();
        mText.setValue("Click 'START' to begin streaming prices.");
    }

    public void updateBanner(String s) {
        mText.postValue(s);
    }

    public void updateTable(List<AssetQuote> quotes) {
        assetQuotes.postValue(quotes);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<AssetQuote>> getQuotes() {return assetQuotes;}
}