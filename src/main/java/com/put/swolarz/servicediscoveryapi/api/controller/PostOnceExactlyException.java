package com.put.swolarz.servicediscoveryapi.api.controller;

public class PostOnceExactlyException extends Exception {

    public PostOnceExactlyException(String poeToken) {
        super(String.format("Duplicate POST request: %s", poeToken));
    }
}
