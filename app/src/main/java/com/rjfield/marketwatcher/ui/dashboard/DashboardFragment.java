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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rjfield.marketwatcher.SharedViewModel;
import com.rjfield.marketwatcher.databinding.FragmentDashboardBinding;
import com.rjfield.marketwatcher.models.StockQuote;
import com.rjfield.marketwatcher.models.User;
import com.rjfield.marketwatcher.service.PriceService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.grpc.stub.StreamObserver;
import price.PriceOuterClass;
import user.UserOuterClass;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    final static public String TAG = DashboardFragment.class.getCanonicalName();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private DashboardViewModel dashboardViewModel = null;
    private SharedViewModel sharedViewModel = null;
    private DashboardAdapter dashboardAdapter = null;
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
            dashboardViewModel.updateBanner(user.getGivenName() + "Click START to begin streaming");
        }

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        dashboardAdapter = new DashboardAdapter();
        RecyclerView recyclerView = binding.stockList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(dashboardAdapter);

        LiveData<List<StockQuote>> quotes = dashboardViewModel.getQuotes();
        quotes.observe(getViewLifecycleOwner(), lsq -> {
//            dashboardAdapter.updateOne(lsq.get(0));
            dashboardAdapter.updateQuotes(lsq);
            dashboardAdapter.notifyDataSetChanged();
        });

        final Button startButton = binding.startButton;
        startButton.setOnClickListener(this);

        dashboardAdapter.initializeStockQuotes(sharedViewModel.ListAssets());

        return root;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: Start button pressed");

        dashboardViewModel.updateBanner("Live Pricing for your Holdings");
//        dashboardAdapter.initializeStockQuotes();
        dashboardAdapter.notifyDataSetChanged();

        StreamObserver<PriceOuterClass.StreamPricesReply> observer =
                new StreamObserver<PriceOuterClass.StreamPricesReply>() {
                    @Override
                    public void onNext(PriceOuterClass.StreamPricesReply value) {
                        Log.d(TAG, "Got price: " + value.getPrice());
//                        DecimalFormat df = new DecimalFormat("0.000");
//                        Log.d(TAG, "Updating banner");
//                        dashboardViewModel.updateBanner(value.getPrice().getPriceId() + " : " + df.format(value.getPrice().getPrice()));
                        Log.d(TAG, "Updating table");

                        StockQuote s = new StockQuote(value.getPrice().getPriceId(), value.getPrice().getPrice());
                        List<StockQuote> ls = new ArrayList<>();
                        ls.add(s);
                        dashboardViewModel.updateTable(ls);
                        // TO DO
                        // Can't do this directly. Call the view model here to update the table
                        // the view model should have an observer that calls the adapter
                        // or something like that
//                        dashboardAdapter.updateOne(value.getPrice().getPriceId(), value.getPrice().getPrice());
//                        dashboardAdapter.notifyDataSetChanged();
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

        executorService.execute(() -> {
//            try {
                // Simulate a long-running operation
//                Thread.sleep(3000); // 3 seconds

                priceService = new PriceService(getContext().getApplicationContext());
                priceService.observer = observer;
                priceService.startStream(sharedViewModel.ListAssets());

                // Can't update the view model here like we do
                // with a blocking gRPC call -
                // need to do that in the stream observer

//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                dashboardViewModel.updateBanner("Task interrupted!");
//            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}