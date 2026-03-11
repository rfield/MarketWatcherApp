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
import com.rjfield.marketwatcher.models.AssetQuote;
import com.rjfield.marketwatcher.models.User;
import com.rjfield.marketwatcher.models.Asset;
import com.rjfield.marketwatcher.models.UserNotification;
import com.rjfield.marketwatcher.service.AssetService;
import com.rjfield.marketwatcher.service.NotificationService;
import com.rjfield.marketwatcher.service.PriceService;
import com.rjfield.marketwatcher.service.UserService;

import java.util.ArrayList;
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
    PriceService priceService = null;
    NotificationService notificationService = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        sharedViewModel =
                new ViewModelProvider(getActivity()).get(SharedViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Attach the view model to the UI components it represents
        // The UI will update upon notification that the data in the model has changed
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

        // Wrap the external service request in a background thread so
        // our application doesn't freeze,
        // and update the view models when calls complete.
        executorService.execute(() -> {
            try {
                // Log the user in with their credentials
                userService = new UserService(getContext().getApplicationContext(), userName, password);
                User u = userService.login();
                sharedViewModel.SetUser(u);
                homeViewModel.updateBanner("Welcome, " + u.getGivenName() + "!");

                // Retrieve the assets for this user from the database
                assetService = new AssetService(getContext().getApplicationContext(), u.getId());
                List<AssetQuote> assetList = assetService.ListAssetsForUser();
                sharedViewModel.SetAssets(assetList);

                // Get current pricing for each asset in the portfolio
                priceService = new PriceService(getContext().getApplicationContext());
                List<String> priceIds = new ArrayList<String>();
                for(AssetQuote a: assetList) {
                    priceIds.add(a.getTicker());
                }
                List<Double> prices = priceService.getPrices(priceIds);
                for (int i = 0; i < assetList.size(); i++) {
                    assetList.get(i).setPrice(prices.get(i));
                }

                notificationService = new NotificationService(getContext().getApplicationContext(), u.getId());
                List<UserNotification> nList = notificationService.ListNotificationsForUser();
                sharedViewModel.setNotifications(nList);

                // This part is tricky, and peculiar to this app.
                // We'd like to enable the other items in the bottom
                // navigation after a successful login, but we perform login on
                // a background thread. For UI data, this is not a problem
                // because we use the view model
                // framework to communicate updates asynchronously.
                // For direct UI updates, like enabling menus, we must
                // communicate this back on the main thread. Fortunately,
                // AppCompatActivity (and classes extending it) provide
                // a means of executing back on the main thread.
                Log.d(TAG, "Enabling menu items");
                MainActivity ma = (MainActivity) getActivity();
                ma.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ma.enableViews(true);
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