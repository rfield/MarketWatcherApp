package com.rjfield.marketwatcher.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rjfield.marketwatcher.models.AssetQuote;
import com.rjfield.marketwatcher.models.UserNotification;

import java.util.List;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<UserNotification>> notifications;


    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        notifications = new MutableLiveData<>();
        mText.setValue("No notifications currently");
    }


    public LiveData<String> getText() {
        return mText;
    }

    public void updateBanner(String s) {
        mText.postValue(s);
    }

    public void updateNotifications(List<UserNotification> nList) {
        if (nList.size() <= 0) {
            updateBanner("No notifications currently");
        }
        else {
            updateBanner("Recent Notifications");
        }
        notifications.postValue(nList);
    }

    public LiveData<List<UserNotification>> getNotifications() {return notifications;}
}