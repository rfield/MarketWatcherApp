package com.rjfield.marketwatcher.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rjfield.marketwatcher.MainActivity;
import com.rjfield.marketwatcher.SharedViewModel;
import com.rjfield.marketwatcher.databinding.FragmentHomeBinding;
import com.rjfield.marketwatcher.exceptions.AssetsNotFoundException;
import com.rjfield.marketwatcher.exceptions.AuthenticationException;
import com.rjfield.marketwatcher.exceptions.UserNotFoundException;
import com.rjfield.marketwatcher.models.User;
import com.rjfield.marketwatcher.models.Asset;
import com.rjfield.marketwatcher.service.AssetService;
import com.rjfield.marketwatcher.service.UserService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    final static public String TAG = HomeFragment.class.getCanonicalName();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private FragmentHomeBinding binding;
    HomeViewModel homeViewModel = null;
    SharedViewModel sharedViewModel = null;
    UserService userService = null;
    AssetService assetService = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        sharedViewModel =
                new ViewModelProvider(getActivity()).get(SharedViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final Button loginButton = binding.loginButton;
        loginButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        final String userName = binding.userNameEditText.getText().toString();
        final String password = binding.passwordEditText.getText().toString();

        Log.d(TAG, "onClick: Login button pressed for (" + userName + "/" + password + ")");

        // Send the external service request to a background thread
        executorService.execute(() -> {
            try {
                userService = new UserService(getContext().getApplicationContext(), userName, password);
                User u = userService.login();
                sharedViewModel.SetUser(u);
                homeViewModel.updateBanner("Welcome, " + u.getGivenName() + "!");

                assetService = new AssetService(getContext().getApplicationContext(), u.getId());
                List<Asset> assetList = assetService.ListAssetsForUser();
                sharedViewModel.SetAssets(assetList);

                Log.d(TAG, "Enabling menu items");
                MainActivity ma = (MainActivity) getActivity();
                ma.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ma.enableViews(true);
                        // or
                        // enableAllItems(enableState);
                    }
                });
            }
            catch (AuthenticationException e) {
                Log.d(TAG, "Authentication failure");
                homeViewModel.updateBanner("Incorrect username or password");
            }
            catch (UserNotFoundException e) {
                Log.d(TAG, "User not found exception");
                homeViewModel.updateBanner("Username not found");
            }
            catch (AssetsNotFoundException e) {
                Log.d(TAG, "No assets found for user");
                homeViewModel.updateBanner("No assets found");
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                homeViewModel.updateBanner("User Authentication task interrupted");
            }
            catch (Exception e) {
                Log.d(TAG, "Authentication failure");
                homeViewModel.updateBanner("Unexpected error");
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}