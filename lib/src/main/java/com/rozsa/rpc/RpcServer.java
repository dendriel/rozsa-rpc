package com.rozsa.rpc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.rozsa.rpc.DependencyProvider.*;

/**
 * RpcServer is the entry point for the RPC solution. Its wrap-up the network ({@link com.rozsa.rpc.NetworkServer}),
 * services and procedures scanning ({@link com.rozsa.rpc.RpcServicesLoader}) and the calls dispatcher
 * ({@link com.rozsa.rpc.RequestHandler}).
 */
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

    /**
     * Starts the RPC server serving the services from specified packages.
     * @param fromPackages Target packages to be scanned for services and procedures.
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public void start(String... fromPackages) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        NetworkServer networkServer = provide(NetworkServer.class, ip, port);

        // TODO: may be clearer to provide the service loader and provider separately.
        RpcServicesProvider servicesProvider = provide(RpcServicesLoader.class, (Object) fromPackages);
        RequestHandler handler = provide(RequestHandler.class, new Class<?>[]{RpcServicesProvider.class}, servicesProvider);

        networkServer.start(handler);
    }
}
