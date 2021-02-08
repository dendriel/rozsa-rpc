package com.rozsa.rpc;

class RpcErrors {
    public static final String SERVICE_NOT_FOUND = "Requested service was not found";
    public static final String PROCEDURE_NOT_FOUND = "Requested procedure was not found";
    public static final String INVALID_ACTION = "Service or procedure is missing from request URI path.";
    public static final String INVALID_ARGS_COUNT = "Received invalid number of arguments.";
}
