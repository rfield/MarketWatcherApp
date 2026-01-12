package com.rjfield.marketwatcher.ui.dashboard;

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
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rjfield.marketwatcher.SharedViewModel;
import com.rjfield.marketwatcher.databinding.FragmentDashboardBinding;
import com.rjfield.marketwatcher.models.AssetQuote;
import com.rjfield.marketwatcher.models.User;
import com.rjfield.marketwatcher.service.PriceService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.grpc.stub.StreamObserver;
import price.PriceOuterClass;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    final static public String TAG = DashboardFragment.class.getCanonicalName();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private DashboardViewModel dashboardViewModel = null;
    private SharedViewModel sharedViewModel = null;
    private DashboardAdapter dashboardAdapter = null;
    private HeaderAdapter headerAdapter = null;
    private FragmentDashboardBinding binding;
    private PriceService priceService = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        sharedViewModel =
                new ViewModelProvider(getActivity()).get(SharedViewModel.class);

        User user = sharedViewModel.GetUser();
        if (user != null) {
            dashboardViewModel.updateBanner( /*user.getGivenName() + */ "Click START to begin streaming");
        }

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        headerAdapter = new HeaderAdapter();
        dashboardAdapter = new DashboardAdapter();
        ConcatAdapter concatAdapter = new ConcatAdapter(headerAdapter, dashboardAdapter);

        RecyclerView recyclerView = binding.stockList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(concatAdapter);

        LiveData<List<AssetQuote>> quotes = dashboardViewModel.getQuotes();
        quotes.observe(getViewLifecycleOwner(), lsq -> {
            dashboardAdapter.updateQuotes(lsq);
            dashboardAdapter.notifyDataSetChanged();
        });

        final Button startButton = binding.startButton;
        startButton.setOnClickListener(this);

        dashboardAdapter.initializeStockQuotes(sharedViewModel.ListAssets());
        dashboardAdapter.notifyDataSetChanged();

        return root;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: Start button pressed");

        dashboardViewModel.updateBanner("Live Pricing for your Holdings");
        dashboardAdapter.notifyDataSetChanged();

        // For processing the replies of a gRPC stream, we define a
        // custom StreamObserver. It is convenient to do this inline here,
        // where we have access to the view model which needs to be
        // updated when we receive items on the stream.
        StreamObserver<PriceOuterClass.StreamPricesReply> observer =
                new StreamObserver<PriceOuterClass.StreamPricesReply>() {
                    @Override
                    public void onNext(PriceOuterClass.StreamPricesReply value) {
                        Log.d(TAG, "Got price: " + value.getPrice());
                        Log.d(TAG, "Updating table");

                        AssetQuote s = new AssetQuote(value.getPrice().getPriceId(), value.getPrice().getPrice());
                        List<AssetQuote> ls = new ArrayList<>();
                        ls.add(s);
                        dashboardViewModel.updateTable(ls);
                    }
                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "Got onError: ");
                        Log.d(TAG, t.toString());
                        t.printStackTrace();

                    }
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "On Completed called");
                        dashboardViewModel.updateBanner("Live Pricing Paused");
                    }
                };

        // Importantly, we wrap our invocation of services, like the PriceService
        // in a separate thread. We DO NOT want to block the main UI thread as this
        // will freeze the user interface for the duration of the call.
        // We define an observer above to catch replies and attach it to the
        // incoming stream.
        executorService.execute(() -> {
                priceService = new PriceService(getContext().getApplicationContext());
                priceService.observer = observer;
                priceService.startStream(sharedViewModel.ListAssets());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}