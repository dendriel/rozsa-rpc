package com.rozsa.services.calc;

import com.rozsa.rpc.annotations.RpcProcedure;
import com.rozsa.rpc.annotations.RpcService;

@RpcService("calc")
public class Calculator {

    @RpcProcedure
    public int sum(int a, int b) {
        return a + b;
    }

    @RpcProcedure
    public int sub(int a, int b) {
        return a - b;
    }

    @RpcProcedure
    public int mul(int a, int b) {
        return a * b;
    }

    @RpcProcedure
    public int div(int a, int b) {
        return a / b;
    }

    @RpcProcedure
    public double getPi() {
        return Math.PI;
    }

    @RpcProcedure
    public void debug() {
        System.out.println("Debug method");
    }
}
