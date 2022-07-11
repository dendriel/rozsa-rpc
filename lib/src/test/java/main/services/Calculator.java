package main.services;

import com.rozsa.rpc.annotations.RpcProcedure;
import com.rozsa.rpc.annotations.RpcService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RpcService("calc")
public class Calculator {
    @RpcProcedure
    public void debug() {
        System.out.println("Debug method");
        // TODO: set test env to validate code execution
    }

    @RpcProcedure
    public int sum(int a, Integer b) {
        return a + b;
    }

    @RpcProcedure
    public short cut(short a, Short b) {
        return (short) (a/2 + b/2);
    }

    @RpcProcedure
    public long enlarge(Long a, long b) {
        return (a*2 + b*2);
    }

    @RpcProcedure
    public float sub(float a, Float b) {
        return a - b;
    }

    @RpcProcedure
    public Double twice(double a) {
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
    public boolean inversor(boolean value) {
        return !value;
    }

    @RpcProcedure
    public String reverse(String value) {
        return new StringBuilder(value).reverse().toString();
    }

    @RpcProcedure
    public Date nextDay(Date value) {
        return new Date(value.getTime() + (1000 * 60 * 60 * 24));
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
}
