package com.rjfield.marketwatcher.ui.details;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;


import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.rjfield.marketwatcher.SharedViewModel;
import com.rjfield.marketwatcher.databinding.StockDetailsDialogBinding;
import com.rjfield.marketwatcher.models.AssetQuote;
import com.rjfield.marketwatcher.models.ChartDataPoint;

import com.rjfield.marketwatcher.util.DataAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class StockDetailsFragment extends DialogFragment implements View.OnClickListener {

    private StockDetailsDialogBinding binding;
    private static final String ARG_TICKER = "ticker";
    private static final String TAG = StockDetailsFragment.class.getCanonicalName();

    private LineChart lineChart;
    private SharedViewModel sharedViewModel;



    public static StockDetailsFragment newInstance(String ticker) {
        StockDetailsFragment fragment = new StockDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TICKER, ticker); // Put data into the bundle
        fragment.setArguments(args);     // Attach the bundle to the fragment
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = StockDetailsDialogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView bannerTextView = binding.textStockDetails;
        String ticker = getArguments().getString(ARG_TICKER);
        bannerTextView.setText("Price History: " + ticker);

        lineChart = binding.stockChart;
        populateLineChart(ticker);

        final Button okButton = binding.okButton;
        okButton.setOnClickListener(this);

        return root;
    }

    private void populateLineChart(String ticker) {

        // 0. Get this historical prices for this ticker from the SharedViewModel
        List<Double> prices = new ArrayList<>();
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
        List<AssetQuote> aList = sharedViewModel.ListAssets();
        for (AssetQuote a: aList) {
            if (a.getTicker().equals(ticker)) {
                prices = a.getHistoricalPrices();
                break;
            }
        }
        Log.d(TAG, "Ticker: " + ticker);
        Log.d(TAG, "Number of prices: " + prices.size());
        Log.d(TAG, "Prices: " + prices);

        // 1. Start creating ChartDataPoints for the LineChart
        // Loop over last 6 months of data; custom renderer expects the x-axis
        // to be the number of months back the price corresponds to
        // Note that this assumes the server returns the last 6 months
        // of prices, which is does, currently.
        List<ChartDataPoint> chartDataPoints = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            chartDataPoints.add(new ChartDataPoint(i, prices.get(i).floatValue()));
        }

        // 2. Create a list of Entry objects (each Entry is an x,y pair)
        List<Entry> entries = new ArrayList<>();
        for (ChartDataPoint cp : chartDataPoints) {
            entries.add(new Entry(cp.getTimestampInSeconds(), cp.getValueY()));
        }

        // 3. Create a DataSet object from the entries list
        LineDataSet dataSet = new LineDataSet(entries, "Daily Closing Price"); // Add a label for the legend

        // 4. Customize the dataSet
        dataSet.setColor(Color.CYAN);
        dataSet.setCircleColor(Color.CYAN);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(false);

        // 5. Create a LineData object with the dataSet
        LineData lineData = new LineData(dataSet);

        // 6. Set the data to the chart
        lineChart.setData(lineData);

        // 7.  Set up formatting
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new DataAxisValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Ensure labels don't overlap
        xAxis.setLabelRotationAngle(-45); // Optional: rotate labels

        // 8. Refresh the chart (animate calls invalidate)
        lineChart.getDescription().setEnabled(false); // Disable description label
        lineChart.animateY(1000); // Animate the chart
        lineChart.invalidate(); // Refresh the chart
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            // Set the width and height to MATCH_PARENT
            int dialogWidth = WindowManager.LayoutParams.MATCH_PARENT;
            int dialogHeight = WindowManager.LayoutParams.WRAP_CONTENT; // or MATCH_PARENT

            // Apply the new layout parameters
            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
