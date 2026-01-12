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

    protected Context context = null;
    private final String userName;
    private final String password;

    public UserService(Context context, String userName, String password) {
        this.context = context;
        this.userName = userName;
        this.password = password;
    }

    // The UserService is a client-side service object that provides
    // a means for the application to access outside resources for User information,
    // namely the gRPC service.
    // Notice that it is OK to "block" in the client-side service object,
    // because it is the responsibility of the calling code to execute the
    // service calls in a background thread. See HomeFragment.onClick().
    // Note also that the login() service method returns a simple model object, User,
    // using the mapUserFromProto() method.
    // It does NOT return the generated gRPC class directly as we do not want the
    // complexities of the gRPC classes leaking into the rest of the application,
    // especially because we don't really "own" the definition of those classes in this app -
    // they are owned by the server.
    // Finally, note that this is NOT how we do authentication in a production
    // application. This "login()" is really just an illustration for gRPC. There are
    // many resources for Android User Authentication, including
    // https://developer.android.com/security/fraud-prevention/authentication
    public User login() throws Exception {
        Log.d(TAG, "login:  for (" + userName + "/" + password + ")");

        UserServiceGrpc.UserServiceBlockingStub grpcClient = UserServiceGrpc.newBlockingStub(ChannelFactory.getChannel(context));

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


