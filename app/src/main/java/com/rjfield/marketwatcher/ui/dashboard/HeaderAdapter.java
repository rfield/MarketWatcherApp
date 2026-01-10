package com.rjfield.marketwatcher.ui.dashboard;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rjfield.marketwatcher.R;

public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder> {

    public HeaderAdapter() {
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView portfolioHeaderTextView;
        public TextView tickerHeaderTextView;
        public TextView priceHeaderTextView;
        public TextView unitsHeaderTextView;
        public TextView totalPositionHeaderTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            portfolioHeaderTextView = itemView.findViewById(R.id.portfolio_header);
            tickerHeaderTextView = itemView.findViewById(R.id.ticker_header);
            priceHeaderTextView = itemView.findViewById(R.id.price_header);
            unitsHeaderTextView = itemView.findViewById(R.id.units_header);
            totalPositionHeaderTextView = itemView.findViewById(R.id.total_position_header);

            portfolioHeaderTextView.setTypeface(null, Typeface.BOLD);
            tickerHeaderTextView.setTypeface(null, Typeface.BOLD);
            priceHeaderTextView.setTypeface(null, Typeface.BOLD);
            unitsHeaderTextView.setTypeface(null, Typeface.BOLD);
            totalPositionHeaderTextView.setTypeface(null, Typeface.BOLD);
        }
    }

    @Override
    public HeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.header_text_view, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HeaderViewHolder holder, int position) {
        holder.portfolioHeaderTextView.setText("Portfolio");
        holder.tickerHeaderTextView.setText("Ticker");
        holder.priceHeaderTextView.setText("Price");
        holder.unitsHeaderTextView.setText("Units");
        holder.totalPositionHeaderTextView.setText("Total");
    }

    @Override
    public int getItemCount() {
        return 1; // Only one header item
    }
}
