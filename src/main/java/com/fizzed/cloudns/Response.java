package com.fizzed.cloudns;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    private String status;
    private String statusDescription;
    private ResponseData data;

    public String getStatus() {
        return status;
    }

    public Response setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public Response setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
        return this;
    }

    public ResponseData getData() {
        return data;
    }

    public Response setData(ResponseData data) {
        this.data = data;
        return this;
    }

    // helpers

    public boolean isFailed() {
        return this.status != null && this.status.equalsIgnoreCase("failed");
    }

    public boolean isSuccess() {
        return this.status != null && this.status.equalsIgnoreCase("success");
    }

}