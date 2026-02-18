package com.rjfield.marketwatcher.service;

import static com.rjfield.marketwatcher.util.ResourceNameUtils.UserIdFromResourceName;

import android.content.Context;
import android.util.Log;

import com.rjfield.marketwatcher.exceptions.AssetsNotFoundException;
import com.rjfield.marketwatcher.models.Asset;
import com.rjfield.marketwatcher.models.AssetQuote;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import account.AccountOuterClass;
import account.AccountServiceGrpc;
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

    public List<AssetQuote> ListAssetsForUser() throws Exception {
        Log.d(TAG, "ListAssetsForUser: for (" + userId + ")");

        // Here we want to list all the assets for the given user.
        // To do this, must understand the heirarchy of the user/account/asset data structures
        // provided in the server's proto definitions. See the 'google.api.resource' and
        // 'google.api.resource_reference' annotations to understand the hierarchy.

        // First we get a list of accounts for this user...
        AccountServiceGrpc.AccountServiceBlockingStub accountClient = AccountServiceGrpc.newBlockingStub(ChannelFactory.getChannel(context));
        AccountOuterClass.ListAccountsReply listAccountsReply = null;
        try {
            AccountOuterClass.ListAccountsRequest listAccountsRequest = AccountOuterClass.ListAccountsRequest.newBuilder()
                    .setParent("users/" + userId)
                    .build();
            listAccountsReply = accountClient.listAccounts(listAccountsRequest);
        }
        catch (Exception e) {
            throw new AssetsNotFoundException("No assets found for user");
        }

        // Then we get a list of assets for each of those accounts...
        List<AssetQuote> aList = new ArrayList<>();
        AssetServiceGrpc.AssetServiceBlockingStub assetClient = AssetServiceGrpc.newBlockingStub(ChannelFactory.getChannel(context));
        AssetOuterClass.ListAssetsReply listAssetsReply = null;
        try {
            for(AccountOuterClass.Account a: listAccountsReply.getAccountsList()) {
                AssetOuterClass.ListAssetsRequest listAssetsRequest = AssetOuterClass.ListAssetsRequest.newBuilder()
                        .setParent(a.getName())
                        .build();
                listAssetsReply = assetClient.listAssets(listAssetsRequest);
                for (AssetOuterClass.Asset ast : listAssetsReply.getAssetsList()) {
                    AssetQuote asset = mapAssetsFromProto(a,ast);
                    aList.add(asset);
                }
            }
        }
        catch (Exception e) {
            throw new AssetsNotFoundException("No assets found for user");
        }

        return aList;
    }

    private AssetQuote mapAssetsFromProto(AccountOuterClass.Account acc, AssetOuterClass.Asset a) {
        AssetQuote asset = new AssetQuote();
        if (a != null) {
//            asset.setUserName(UserIdFromResourceName(a.getName()));
            asset.setAccountName(acc.getAccountName());
            asset.setTicker(a.getTicker());
            asset.setPrice(0.0);
            asset.setHoldingAmount(a.getHoldingAmount());
        }
        return asset;
    }
}
