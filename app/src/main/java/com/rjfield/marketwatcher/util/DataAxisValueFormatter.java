package com.rjfield.marketwatcher.util;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class DataAxisValueFormatter  extends IndexAxisValueFormatter {

    @Override
    // The implementation here is a bit of a kludge, and should probably
    // be refactored. Months back is passed in as 0 for the price 6 months back,
    // 1 for the price 5 months back, etc and 6 for the most recent price.
    // This necessitates the odd calculation of priceMonth below.
    public String getFormattedValue(float monthsBack) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate priceMonth = firstDayOfMonth.minusMonths(6 - (int) monthsBack);
        return priceMonth.getMonth().toString().substring(0, 3);
    }
}
