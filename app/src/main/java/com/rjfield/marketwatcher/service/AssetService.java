package com.rjfield.marketwatcher.service;

import android.content.Context;
import android.util.Log;

import com.rjfield.marketwatcher.exceptions.AssetsNotFoundException;
import com.rjfield.marketwatcher.models.Asset;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import account.AssetOuterClass;
import account.AssetServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.android.AndroidChannelBuilder;

public class AssetService {
    final static public String TAG = AssetService.class.getCanonicalName();
    private String userId;

    protected Context context = null;

    public AssetService(Context ctx, String id) {
        this.context = ctx;
        this.userId = id;
    }

    public List<Asset> ListAssetsForUser() throws Exception {
        AssetServiceGrpc.AssetServiceBlockingStub assetClient = AssetServiceGrpc.newBlockingStub(ChannelFactory.getChannel(context));
        AssetOuterClass.ListAssetsForUserReply listAssetsForUserReply = null;

        try {
            AssetOuterClass.ListAssetsForUserRequest listAssetsForUserRequest = AssetOuterClass.ListAssetsForUserRequest.newBuilder()
                    .setUserId(userId)
                    .build();
            listAssetsForUserReply = assetClient.listAssetsForUser(listAssetsForUserRequest);
        }
        catch (Exception e) {
            throw new AssetsNotFoundException("No assets found for user");
        }

        Log.d(TAG, "Assets: " + listAssetsForUserReply.getAssetsList());
        List<Asset> aList = new ArrayList<>();
        for(AssetOuterClass.Asset a: listAssetsForUserReply.getAssetsList()) {
            Asset asset = mapAssetsFromProto(a);
            aList.add(asset);
        }
        return aList;
    }

    private Asset mapAssetsFromProto(AssetOuterClass.Asset a) {
        Asset asset = new Asset();
        if (a != null) {
            asset.setUserName(a.getUserId());
            asset.setAccountName(a.getAccountName());
            asset.setTicker(a.getTicker());
            asset.setHoldingAmount(a.getHoldingAmount());
        }
        return asset;
    }
}
