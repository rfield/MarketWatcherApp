package com.rjfield.marketwatcher;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rjfield.marketwatcher.models.Asset;
import com.rjfield.marketwatcher.models.AssetQuote;
import com.rjfield.marketwatcher.models.User;
import com.rjfield.marketwatcher.ui.home.HomeFragment;

import java.util.List;

import user.UserOuterClass;

// The SharedViewModel provides singleton access to common data for multiple
// fragments or activities.
// Classes accessing the SharedViewModel must acquire the handle to it via
// the Android ViewModelProvider, or they will not get access to the shared
// model, but instead instatiate a copy for themselves, defeating the purpose.
public class SharedViewModel extends ViewModel {

    final static public String TAG = SharedViewModel.class.getCanonicalName();

    private final MutableLiveData<User> currentUser;
    private final MutableLiveData<List<AssetQuote>> assets;

    public SharedViewModel() {
        currentUser = new MutableLiveData<>();
        assets = new MutableLiveData<>();
    }

    public void SetUser(User u) {
        Log.d(TAG, "SetUser(): " + u);
        currentUser.postValue(u);
        Log.d(TAG, "SetUser() after post: " + currentUser.getValue());

    }

    public User GetUser() {
        Log.d(TAG, "GetUser(): " + currentUser.getValue());
        return currentUser.getValue();
    }

    public void SetAssets(List<AssetQuote> la) {
        Log.d(TAG, "SetAssets(): " + la);
        assets.postValue(la);
    }

    public List<AssetQuote> ListAssets() {
        Log.d(TAG, "ListAssets(): " + assets.getValue());
        return assets.getValue();
    }

    public Double GetTotalPosition() {
        Double total = 0.0;
        for(AssetQuote a: assets.getValue()) {
            total += a.getHoldingAmount() * a.getPrice();
        }
        return total;
    }
    public LiveData<User> GetUser2() {
        return currentUser;
    }
}