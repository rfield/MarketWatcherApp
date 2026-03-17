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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        Random rand = new Random();
        List<ChartDataPoint> chartDataPoints = new ArrayList<>();
        // Loop over last 6 days of data; custom renderer expects the x-axis
        // to be the number of months back the price corresponds to
        for (int i = 0; i < 6; i++) {
//            LocalDate dt = LocalDate.now(ZoneId.systemDefault()).minusDays(i);
//            long ts = dt.atTime(17, 0).toEpochSecond(ZoneId.systemDefault().getRules().getOffset(Instant.now()));
//            float px = rand.nextFloat() * (200.0f - 100.0f) + 100.0f;
            chartDataPoints.add(new ChartDataPoint(i, prices.get(i).floatValue()));
//            chartDataPoints.add(new ChartDataPoint(i, px));
        }

        // 1. Create a list of Entry objects (each Entry is an x,y pair)
        List<Entry> entries = new ArrayList<>();
        for (ChartDataPoint cp : chartDataPoints) {
            entries.add(new Entry(cp.getTimestampInSeconds(), cp.getValueY()));
        }
//        entries.add(new Entry(0, 10));
//        entries.add(new Entry(1, 15));
//        entries.add(new Entry(2, 7));
//        entries.add(new Entry(3, 20));
//        entries.add(new Entry(4, 12));
//        entries.add(new Entry(5, 18));
        // Add more entries as needed

        // 2. Create a DataSet object from the entries list
        LineDataSet dataSet = new LineDataSet(entries, "Daily Closing Price"); // Add a label for the legend

        // Customize the dataSet (optional)
        dataSet.setColor(Color.CYAN);
        dataSet.setCircleColor(Color.CYAN);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(false);

        // 3. Create a LineData object with the dataSet
        LineData lineData = new LineData(dataSet);

        // 4. Set the data to the chart
        lineChart.setData(lineData);

        // 4.5  Set up formatting
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new DataAxisValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Ensure labels don't overlap
        xAxis.setLabelRotationAngle(-45); // Optional: rotate labels

        // 5. Refresh the chart (animate calls invalidate)
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
//            getDialog().getWindow().setLayout(300, 400);

            // Make background transparent if you have rounded corners or margins in XML
//             getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
