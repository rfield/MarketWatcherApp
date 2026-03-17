package com.rjfield.marketwatcher.util;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;


public class DataAxisValueFormatter  extends IndexAxisValueFormatter {

    private String labels[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    @Override
    public String getFormattedValue(float monthsBack) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayofMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate priceMonth = firstDayofMonth.minusMonths(6 - (int) monthsBack);
        String priceMonthAbbbr = priceMonth.getMonth().toString().substring(0, 3);
////        long milliSeconds = ((long) value) * 1000L;
////        return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(new Date(milliSeconds));
        return priceMonthAbbbr;
//        return labels[(int)monthsBack];
    }
}
