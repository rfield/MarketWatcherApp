package com.rjfield.marketwatcher.service;

import android.content.Context;
import android.util.Log;

import androidx.camera.camera2.pipe.core.Timestamps;

import com.rjfield.marketwatcher.exceptions.AssetsNotFoundException;
import com.rjfield.marketwatcher.exceptions.NotificationsNotFoundException;
import com.rjfield.marketwatcher.models.UserNotification;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import account.AccountOuterClass;
import account.AccountServiceGrpc;
import account.NotificationOuterClass;
import account.NotificationServiceGrpc;

import java.time.ZoneId;


public class NotificationService {

    final static public String TAG = NotificationService.class.getCanonicalName();

    protected Context context = null;
    private String userId;


    public NotificationService(Context ctx, String id) {
        this.context = ctx;
        this.userId = id;
    }

    public List<UserNotification> ListNotificationsForUser() throws Exception {
        Log.d(TAG, "ListNotificationsForUser: for (" + userId + ")");

        List<UserNotification> aList = new ArrayList<>();

        // TO DO call the server to get the list of notications and map them
        // to the app's data structure before returning
        NotificationServiceGrpc.NotificationServiceBlockingStub client = NotificationServiceGrpc.newBlockingStub(ChannelFactory.getChannel(context));
        NotificationOuterClass.ListNotificationsReply reply = null;
        try {
            NotificationOuterClass.ListNotificationsRequest req = NotificationOuterClass.ListNotificationsRequest.newBuilder()
                    .setParent("users/" + userId)
                    .build();
            reply = client.listNotifications(req);
        }
        catch (Exception e) {
            throw new NotificationsNotFoundException("No notifications found for user");
        }

        for (NotificationOuterClass.Notification n : reply.getNotificationsList()) {
            UserNotification notification = mapNotificationsFromProto(n);
            aList.add(notification);
        }
        return aList;
    }

    private UserNotification mapNotificationsFromProto(NotificationOuterClass.Notification n) {
        UserNotification notification = new UserNotification();
        if (n != null) {
            notification.setTitle(n.getTitle());
            notification.setMessage(n.getMessage());

            Instant instant = Instant.ofEpochSecond(n.getCreatedAt().getSeconds(), n.getCreatedAt().getNanos());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd h:mm a").withZone(ZoneId.of("America/New_York"));
            notification.setCreatedAt(formatter.format(instant));
        }
        return notification;
    }

}
