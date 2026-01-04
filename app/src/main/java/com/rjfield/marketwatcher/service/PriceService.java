package com.rjfield.marketwatcher.service;

import android.util.Log;
import android.content.Context;

import com.rjfield.marketwatcher.models.Asset;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import account.AccountServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.android.AndroidChannelBuilder;
import io.grpc.stub.StreamObserver;
import price.PriceOuterClass;
import price.PriceServiceGrpc;


public class PriceService {
    final static public String TAG = PriceService.class.getCanonicalName();
    final static public String USER_AGENT = "MarketWatcherApp";

    protected ManagedChannel channel = null;

    private Context context = null;

    public StreamObserver<PriceOuterClass.StreamPricesReply> observer = null;

    public PriceService(Context context) {
        this.context = context;
    }

    public void startStream(List<Asset> aList) {
        Log.d(TAG, "Starting stream");
        PriceServiceGrpc.PriceServiceStub grpcClient = PriceServiceGrpc.newStub(getChannel());

//        PriceOuterClass.StreamPricesRequest req = PriceOuterClass.StreamPricesRequest.newBuilder()
//                .addPriceIds("MSFT")
//                .addPriceIds("GOOG")
//                .addPriceIds("AAPL")
//                .build();

        PriceOuterClass.StreamPricesRequest.Builder b = PriceOuterClass.StreamPricesRequest.newBuilder();
        for (Asset a: aList) {
            b.addPriceIds(a.getTicker());
        }
        PriceOuterClass.StreamPricesRequest req = b.build();

        grpcClient.streamPrices(req, observer);

//                new StreamObserver<PriceOuterClass.StreamPricesReply>() {
//            @Override
//            public void onNext(PriceOuterClass.StreamPricesReply value) {
//                Log.d(TAG, "Got price: " + value.getPrice());
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                Log.d(TAG, "Got onError: ");
//
//            }
//
//            @Override
//            public void onCompleted() {
//                Log.d(TAG, "On Completed called");
//            }
//        });

    }

    public void endStream() {
        Log.d(TAG, "Ending stream");

    }

    protected Channel getChannel() {
        if (channel == null) {
            channel = AndroidChannelBuilder.forAddress("mac.lan", 50051)
                    .context(context)
//                    .intercept(interceptor)
                    .userAgent(USER_AGENT)
                    .usePlaintext()
                    .idleTimeout(60, TimeUnit.SECONDS)
                    .build();
        }
        return channel;
    }
}
