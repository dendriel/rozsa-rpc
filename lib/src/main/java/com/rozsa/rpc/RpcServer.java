package com.rozsa.rpc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.rozsa.rpc.DependencyProvider.*;

// TODO: allow to startup with debug mode.
public class RpcServer {
    private final String ip;
    private final int port;

    public RpcServer(int port) {
        this("localhost", port);
    }

    public RpcServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start(String... fromPackages) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        NetworkServer networkServer = provide(NetworkServer.class, ip, port);

        RpcServicesProvider servicesProvider = provide(RpcServicesLoader.class, (Object) fromPackages);
        RequestHandler handler = provide(RequestHandler.class, new Class<?>[]{RpcServicesProvider.class}, servicesProvider);

        networkServer.start(handler);
    }
}
