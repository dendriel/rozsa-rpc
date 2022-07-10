package main.services;

import com.rozsa.rpc.annotations.RpcProcedure;
import com.rozsa.rpc.annotations.RpcService;

import java.util.ArrayList;
import java.util.List;

@RpcService("calc")
public class Calculator {

    @RpcProcedure
    public int sum(int a, Integer b) {
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
    public float sub(float a, Float b) {
        return a - b;
    }

    @RpcProcedure
    public Double twice(Double a) {
        return a * 2;
    }

    @RpcProcedure
    public char bigger(char a, Character b) {
        if (a > b) {
            return a;
        }
        return b;
    }

    @RpcProcedure
    public byte next(byte b) {
        return ++b;
    }

    @RpcProcedure
    public void debug() {
        System.out.println("Debug method");
    }

    @RpcProcedure
    public boolean inversor(boolean value) {
        return !value;
    }
}
