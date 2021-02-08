package com.rozsa.services;

import com.rozsa.rpc.annotations.RpcService;

@RpcService("calc")
public class Calculator {

    public int sum(int a, int b) {
        return a + b;
    }

    public int sub(int a, int b) {
        return a - b;
    }

    public int mul(int a, int b) {
        return a * b;
    }

    public int div(int a, int b) {
        return a / b;
    }

    public double getPi() {
        return Math.PI;
    }

    public void debug() {
        System.out.println("Debug method");
    }

    // TODO: add a method that receive a list of operations and return a list of results.
    // TODO: create annotation at class level and method level for RPC
    // @RPCContainer, @RPCAction, @RPCIgnore
}
