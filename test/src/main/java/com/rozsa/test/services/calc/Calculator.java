package com.rozsa.test.services.calc;

import com.rozsa.rpc.annotations.RpcProcedure;
import com.rozsa.rpc.annotations.RpcService;

import java.util.ArrayList;
import java.util.List;

@RpcService("calc")
public class Calculator {

    @RpcProcedure
    public int sum(int a, int b) {
        return a + b;
    }

    @RpcProcedure
    public List<Integer> sum(List<Integer> a, Integer b) {
        List<Integer> result = new ArrayList<>();
        for (Integer integer : a) {
            Integer sum = integer + b;
            result.add(sum);
        }

        return result;
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
