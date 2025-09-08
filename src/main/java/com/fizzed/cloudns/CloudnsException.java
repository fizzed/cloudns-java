package com.fizzed.cloudns;

import java.io.IOException;

public class CloudnsException extends IOException {

    public CloudnsException(String message) {
        super(message);
    }

    public CloudnsException(String message, Throwable cause) {
        super(message, cause);
    }

}