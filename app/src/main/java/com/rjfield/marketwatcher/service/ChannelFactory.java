package com.rjfield.marketwatcher.service;

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.util.Log;

import com.rjfield.marketwatcher.R;

import java.util.concurrent.TimeUnit;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.android.AndroidChannelBuilder;

public class ChannelFactory {

    final static public String TAG = ChannelFactory.class.getCanonicalName();

    final static public String USER_AGENT = "MarketWatcherApp";
    private static ManagedChannel channel = null;

    public static Channel getChannel(Context context) {
        if (channel == null) {
            String host = context.getResources().getString(R.string.server_host);
            int port = Integer.parseInt(context.getResources().getString(R.string.server_port));
            Log.d(TAG, "Host: " + host + " Port: " + port );

            channel = AndroidChannelBuilder.forAddress(host, port)
//            channel = AndroidChannelBuilder.forAddress("mac.lan", 50051)
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
