package com.example.projectecripto.model;

import com.google.gson.Gson;

public class SignedData {
    private String data;
    private String signature;

    public SignedData(String data, String signature) {
        this.data = data;
        this.signature = signature;
    }

    public String getData() {
        return data;
    }

    public String getSignature() {
        return signature;
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public static SignedData fromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, SignedData.class);
    }
}
