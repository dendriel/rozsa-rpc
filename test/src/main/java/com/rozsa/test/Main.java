package com.rozsa.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        com.rozsa.rpc.RpcServer rpc = new com.rozsa.rpc.RpcServer(8000);

//        rpc.start("com.rozsa.services");
        // or
        rpc.start("com.rozsa.test.services.blog", "com.rozsa.test.services.calc");

        System.out.println("Server is running!");
    }
}
