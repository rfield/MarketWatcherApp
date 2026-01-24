package com.rjfield.marketwatcher.ui.dashboard;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.rjfield.marketwatcher.R;
import com.rjfield.marketwatcher.SharedViewModel;

import java.text.DecimalFormat;

public class FooterAdapter extends RecyclerView.Adapter<FooterAdapter.FooterViewHolder> {

    private static final String TAG = FooterAdapter.class.getCanonicalName();
    private SharedViewModel sharedViewModel = null;
    public FooterAdapter(SharedViewModel svm) {
        sharedViewModel = svm;
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView portfolioFooterTextView;
        public TextView tickerFooterTextView;
        public TextView priceFooterTextView;
        public TextView unitsFooterTextView;
        public TextView totalPositionFooterTextView;

        public FooterViewHolder(View itemView) {
            super(itemView);

            portfolioFooterTextView = itemView.findViewById(R.id.portfolio_footer);
            tickerFooterTextView = itemView.findViewById(R.id.ticker_footer);
            priceFooterTextView = itemView.findViewById(R.id.price_footer);
            unitsFooterTextView = itemView.findViewById(R.id.units_footer);
            totalPositionFooterTextView = itemView.findViewById(R.id.total_position_footer);

            portfolioFooterTextView.setTypeface(null, Typeface.BOLD);
//            tickerFooterTextView.setTypeface(null, Typeface.BOLD);
//            priceFooterTextView.setTypeface(null, Typeface.BOLD);
//            unitsFooterTextView.setTypeface(null, Typeface.BOLD);
//            totalPositionFooterTextView.setTypeface(null, T);
        }
    }

    @Override
    public FooterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.footer_text_view, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FooterViewHolder holder, int position) {
        holder.portfolioFooterTextView.setText("Total");
//        holder.tickerFooterTextView.setText("Ticker");
//        holder.priceFooterTextView.setText("Price");
//        holder.unitsFooterTextView.setText("Units");
        DecimalFormat df = new DecimalFormat("###,###.00");
        String s = String.format("%10s", df.format(sharedViewModel.GetTotalPosition()));
        holder.totalPositionFooterTextView.setText(s);
    }

    @Override
    public int getItemCount() {
        return 1; // Only one footer item
    }
}
