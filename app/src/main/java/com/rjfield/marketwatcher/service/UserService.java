package com.rjfield.marketwatcher.service;

import android.content.Context;
import android.util.Log;

import com.rjfield.marketwatcher.exceptions.AssetsNotFoundException;
import com.rjfield.marketwatcher.exceptions.AuthenticationException;
import com.rjfield.marketwatcher.exceptions.UserNotFoundException;
import com.rjfield.marketwatcher.models.Asset;
import com.rjfield.marketwatcher.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import account.AssetOuterClass;
import account.AssetServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.android.AndroidChannelBuilder;
import user.UserOuterClass;
import user.UserServiceGrpc;

public class UserService {

    final static public String TAG = UserService.class.getCanonicalName();
    final static public String USER_AGENT = "MarketWatcherApp";

    protected ManagedChannel channel = null;
    protected Context context = null;
    private final String userName;
    private final String password;

    public UserService(Context context, String userName, String password) {
        this.context = context;
        this.userName = userName;
        this.password = password;
    }
    public User login() throws Exception {
        Log.d(TAG, "login:  for (" + userName + "/" + password + ")");

        // Here we perform the required calls to the external server

        // OK to block - the responsibility of calling this method on a
        // background thread lies with the UI components that require this

        // Set up the connection
        UserServiceGrpc.UserServiceBlockingStub grpcClient = UserServiceGrpc.newBlockingStub(getChannel());

        UserOuterClass.AuthenticateUserReply authenticateUserReply = null;
        UserOuterClass.GetUserReply getUserReply = null;

        try {
            UserOuterClass.AuthenticateUserRequest authenticateUserRequest = UserOuterClass.AuthenticateUserRequest.newBuilder()
                    .setUsername(userName)
                    .setPassword(password)
                    .build();
            authenticateUserReply = grpcClient.authenticateUser(authenticateUserRequest);
        }
        catch (Exception e) {
            throw new AuthenticationException("Incorrect username or password");
        }

        try {
            UserOuterClass.GetUserRequest getUserRequest = UserOuterClass.GetUserRequest.newBuilder()
                    .setUserId(authenticateUserReply.getUserId())
                    .build();
            getUserReply =  grpcClient.getUser(getUserRequest);
        }
        catch (Exception e) {
            throw new UserNotFoundException("Username not found");
        }

        Log.d(TAG, "User is: " + getUserReply.getUser().getGivenName());
        return mapUserFromProto(getUserReply.getUser());
    }

    public void logout() {
        Log.d(TAG, "logout: for (" + userName + ")");
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

    private User mapUserFromProto(UserOuterClass.User u) {
        User user = new User();
        if (u != null) {
            user.setId(u.getUserId());
            user.setUserName(u.getCredentials().getUsername());
            user.setPasswordHash(u.getCredentials().getPasswordHash());
            user.setToken(u.getCredentials().getToken());
            user.setGivenName(u.getGivenName());
        }
        return user;
    }

}


