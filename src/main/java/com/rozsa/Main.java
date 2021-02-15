package com.rozsa;

import com.rozsa.rpc.RpcServer;
import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        RpcServer rpc = new RpcServer(8000);

//        rpc.start("com.rozsa.services");
        // or
        rpc.start("com.rozsa.services.blog", "com.rozsa.services.calc");

        System.out.println("Server is running!");
    }
}
