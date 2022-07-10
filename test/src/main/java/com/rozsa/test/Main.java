package com.rozsa.test;

import com.rozsa.rpc.RpcServer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        RpcServer rpc = new RpcServer(8000);

//        rpc.start("com.rozsa.services");
        // or
        rpc.start("com.rozsa.test.services.blog", "com.rozsa.test.services.calc");

        System.out.println("Server is running!");
    }
}
