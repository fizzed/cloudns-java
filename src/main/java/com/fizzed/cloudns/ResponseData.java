package com.fizzed.cloudns;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseData {

    private String id;

    public String getId() {
        return id;
    }

    public ResponseData setId(String id) {
        this.id = id;
        return this;
    }

}