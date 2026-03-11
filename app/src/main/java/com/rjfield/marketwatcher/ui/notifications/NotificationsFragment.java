package com.rjfield.marketwatcher.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rjfield.marketwatcher.SharedViewModel;
import com.rjfield.marketwatcher.databinding.FragmentNotificationsBinding;
import com.rjfield.marketwatcher.models.AssetQuote;
import com.rjfield.marketwatcher.models.UserNotification;
import com.rjfield.marketwatcher.service.NotificationService;
import com.rjfield.marketwatcher.ui.dashboard.DashboardFragment;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationsFragment extends Fragment implements View.OnClickListener {

    final static public String TAG = NotificationsFragment.class.getCanonicalName();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private SharedViewModel sharedViewModel = null;
    private NotificationsViewModel notificationsViewModel = null;
    private NotificationsAdapter notificationsAdapter = null;
    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        sharedViewModel =
                new ViewModelProvider(getActivity()).get(SharedViewModel.class);

        List<UserNotification> nList = sharedViewModel.getNotifications();
        if (nList.size() == 0) {
            notificationsViewModel.updateBanner("No notifications currently");
        }
        else {
            notificationsViewModel.updateBanner("Recent Notifications");
        }

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        notificationsAdapter = new NotificationsAdapter();

        RecyclerView recyclerView = binding.notificationList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(notificationsAdapter);

        LiveData<List<UserNotification>> notifications = notificationsViewModel.getNotifications();
        notifications.observe(getViewLifecycleOwner(), lsu -> {
            notificationsAdapter.updateNotifications(lsu);
            notificationsAdapter.notifyDataSetChanged();
        });

        final Button refreshButton = binding.refreshButton;
        refreshButton.setOnClickListener(this);

        notificationsAdapter.updateNotifications(sharedViewModel.getNotifications());
        notificationsAdapter.notifyDataSetChanged();

        return root;
    }

    public void onClick(View v) {
        Log.d(TAG, "onClick: Refresh button pressed");

        String userId = sharedViewModel.GetUser().getId();
        executorService.execute(() -> {
            try {
                NotificationService notificationService = new NotificationService(getContext().getApplicationContext(), userId);
                List<UserNotification> nList = notificationService.ListNotificationsForUser();
                sharedViewModel.setNotifications(nList);
            }
            catch (Exception e) {
                Log.d(TAG, "Error retrieving notifications");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}