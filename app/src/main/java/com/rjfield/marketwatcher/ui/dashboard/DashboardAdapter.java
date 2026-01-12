package com.rjfield.marketwatcher.ui.dashboard;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rjfield.marketwatcher.R;
import com.rjfield.marketwatcher.models.Asset;
import com.rjfield.marketwatcher.models.AssetQuote;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder> {

    private static final String TAG = DashboardAdapter.class.getCanonicalName();

    private List<AssetQuote> stockQuotes;

    public static class DashboardViewHolder extends RecyclerView.ViewHolder {

        public TextView portfolioTextView;

        public TextView tickerTextView;
        public TextView priceTextView;
        public TextView unitsTextView;
        public TextView totalPositionTextView;

        public DashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            portfolioTextView = itemView.findViewById(R.id.portfolio);
            tickerTextView = itemView.findViewById(R.id.ticker);
            priceTextView = itemView.findViewById(R.id.price);
            unitsTextView = itemView.findViewById(R.id.units);
            totalPositionTextView = itemView.findViewById(R.id.total_position);

            portfolioTextView.setTypeface(null, Typeface.BOLD_ITALIC);
            tickerTextView.setTypeface(null, Typeface.BOLD);
            totalPositionTextView.setTypeface(Typeface.MONOSPACE);
        }
    }

    public DashboardAdapter() {
        stockQuotes = new ArrayList<AssetQuote>();
    }

    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_quote_item, parent, false);
        return new DashboardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int position) {
        AssetQuote currentItem = stockQuotes.get(position);
        holder.tickerTextView.setText(currentItem.getTicker());
        DecimalFormat df = new DecimalFormat("###.00");
        holder.priceTextView.setText(df.format(currentItem.getPrice()));

        Log.d(TAG, "Holding amount: " + currentItem.getHoldingAmount());
        Log.d(TAG, "Total: " + currentItem.getHoldingAmount()*currentItem.getPrice());

        df = new DecimalFormat("###");
        holder.unitsTextView.setText(df.format(currentItem.getHoldingAmount()));
        df = new DecimalFormat("###,###.00");
        String s = String.format("%10s", df.format(currentItem.getHoldingAmount() * currentItem.getPrice()));
        holder.totalPositionTextView.setText(s);

        // Only display the portfolio name if it is the first, or different
        // from the one above. Old timers call this a "control break". :-)
        String prevPortfolioName = "";
        if (position > 0) {
            prevPortfolioName = stockQuotes.get(position-1).getAccountName();
        }
        if (currentItem.getAccountName().equalsIgnoreCase(prevPortfolioName)) {
            holder.portfolioTextView.setText("");
        }
        else {
            holder.portfolioTextView.setText(currentItem.getAccountName());
        }
    }

    @Override
    public int getItemCount() {
        return stockQuotes.size();
    }

    public void initializeStockQuotes(List<Asset> aList){
        for(Asset a: aList) {
            stockQuotes.add(new AssetQuote(a.getAccountName(), a.getTicker(), 0.0, a.getHoldingAmount()));
        }
    }

    public void updateOne(AssetQuote s) {
        stockQuotes.set(0, s);
    }

    public void updateQuotes(List<AssetQuote> lsq) {
        for (int i = 0; i < lsq.size(); i++) {
            for (int j = 0; j < stockQuotes.size(); j++ ) {
                if (lsq.get(i).getTicker().equalsIgnoreCase(stockQuotes.get(j).getTicker())) {
                    AssetQuote nq = new AssetQuote(
                            stockQuotes.get(j).getAccountName(),
                            stockQuotes.get(j).getTicker(),
                            lsq.get(i).getPrice(),
                            stockQuotes.get(j).getHoldingAmount());
                    stockQuotes.set(j, nq);
                }
            }
        }
    }
}
