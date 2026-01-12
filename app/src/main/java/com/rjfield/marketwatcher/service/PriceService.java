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


// PriceService is a client-side service object.
// It provides an interface for the application to communicate with the gRPC service
// for Price information.
// Note well that we do NOT extend android.app.Service. This is unnecessary because
// we only require the service to run while the user is interacting with our
// application, and we are not providing the service to another application.
// See https://developer.android.com/develop/background-work/services
// if your application requires either of these features.
public class PriceService {
    final static public String TAG = PriceService.class.getCanonicalName();

    private Context context = null;

    public StreamObserver<PriceOuterClass.StreamPricesReply> observer = null;

    public PriceService(Context context) {
        this.context = context;
    }

    public void startStream(List<Asset> aList) {
        Log.d(TAG, "Starting stream");
        PriceServiceGrpc.PriceServiceStub grpcClient = PriceServiceGrpc.newStub(ChannelFactory.getChannel(context));

        PriceOuterClass.StreamPricesRequest.Builder b = PriceOuterClass.StreamPricesRequest.newBuilder();
        for (Asset a: aList) {
            b.addPriceIds(a.getTicker());
        }
        PriceOuterClass.StreamPricesRequest req = b.build();

        grpcClient.streamPrices(req, observer);
    }

    public void endStream() {
        Log.d(TAG, "Ending stream");
    }
}
