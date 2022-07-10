package main;

import com.rozsa.rpc.RpcServer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class TestRpcServer {
    public TestRpcServer() throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        RpcServer rpc = new RpcServer(8080);

        rpc.start("main.services");

        System.out.println("Server is running!");
    }
}
