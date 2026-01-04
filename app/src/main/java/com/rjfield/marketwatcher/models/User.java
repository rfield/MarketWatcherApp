package com.rjfield.marketwatcher.models;

public class User {

    private String id;
    private String userName;
    private String passwordHash;
    private String token;
    private String givenName;
    private String familyName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword_hash() {
        return passwordHash;
    }

    public void setPasswordHash(String password_hash) {
        this.passwordHash = password_hash;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String given_name) {
        this.givenName = given_name;
    }

    public String getFamily_name() {
        return familyName;
    }

    public void setFamily_name(String family_name) {
        this.familyName = family_name;
    }
}
