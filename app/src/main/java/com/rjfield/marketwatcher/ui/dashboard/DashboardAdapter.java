package com.rjfield.marketwatcher.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rjfield.marketwatcher.R;
import com.rjfield.marketwatcher.models.Asset;
import com.rjfield.marketwatcher.models.StockQuote;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder> {

    private static final String TAG = DashboardAdapter.class.getCanonicalName();

    private List<StockQuote> stockQuotes;

    public static class DashboardViewHolder extends RecyclerView.ViewHolder {

        public TextView tickerTextView;
        public TextView priceTextView;

        public DashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tickerTextView = itemView.findViewById(R.id.ticker);
            priceTextView = itemView.findViewById(R.id.price);
        }
    }

    public DashboardAdapter() {
        stockQuotes = new ArrayList<StockQuote>();
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
        StockQuote currentItem = stockQuotes.get(position);
        holder.tickerTextView.setText(currentItem.getTicker());
        DecimalFormat df = new DecimalFormat("0.000");
        holder.priceTextView.setText(df.format(currentItem.getPrice()));
    }

    @Override
    public int getItemCount() {
        return stockQuotes.size();
    }

    public void initializeStockQuotes(List<Asset> aList){
        for(Asset a: aList) {
            stockQuotes.add(new StockQuote(a.getTicker(), 0.0));
        }
    }

    public void updateOne(StockQuote s) {
        stockQuotes.set(0, s);
    }

    public void updateQuotes(List<StockQuote> lsq) {
        for (int i = 0; i < lsq.size(); i++) {
            for (int j = 0; j < stockQuotes.size(); j++ ) {
                if (lsq.get(i).getTicker().equalsIgnoreCase(stockQuotes.get(j).getTicker())) {
                    StockQuote nq = new StockQuote(lsq.get(i).getTicker(), lsq.get(i).getPrice());
                    stockQuotes.set(j, nq);
                }
            }
        }
    }
}
