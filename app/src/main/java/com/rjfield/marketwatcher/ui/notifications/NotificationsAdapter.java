package com.rjfield.marketwatcher.ui.notifications;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rjfield.marketwatcher.R;
import com.rjfield.marketwatcher.models.AssetQuote;
import com.rjfield.marketwatcher.models.UserNotification;
import com.rjfield.marketwatcher.ui.dashboard.DashboardAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>{

    private static final String TAG = NotificationsAdapter.class.getCanonicalName();
    private List<UserNotification> notifications;

    public static class NotificationsViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView createdAtTextView;
        public TextView messageTextView;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notification_title);
            createdAtTextView = itemView.findViewById(R.id.notification_created_at);
            messageTextView = itemView.findViewById(R.id.notification_message);
        }
    }


    public NotificationsAdapter.NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new NotificationsAdapter.NotificationsViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull NotificationsAdapter.NotificationsViewHolder holder, int position) {
        UserNotification currentItem = notifications.get(position);
        holder.titleTextView.setText(currentItem.getTitle());
        holder.createdAtTextView.setText(currentItem.getCreatedAt());
        holder.messageTextView.setText(currentItem.getMessage());
    }

    public int getItemCount() {
            return notifications.size();
    }

    public NotificationsAdapter() {
        notifications = new ArrayList<UserNotification>();
    }

    public void updateNotifications(List<UserNotification> nList) {
        notifications.clear();
        notifications.addAll(nList);
    }
}
