package com.rozsa.rpc;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

// TODO: allow to startup with debug mode.
public class RpcServer {

    /**
     * Use system default to maximum TCP connections waiting at TCP socket.
     */
    private final static int defaultMaximumConnections = -1;

    private final String ip;
    private final int port;

    // TODO: abstract the transport layer.
    private HttpServer server;

    public RpcServer(int port) {
        this("localhost", port);
    }

    public RpcServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start(String... fromPackages) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        wrapUp(ip, port, fromPackages);
        server.start();
    }

    private void wrapUp(String ip, int port, String[] fromPackages) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try {
            InetSocketAddress address = new InetSocketAddress(ip, port);
            server = HttpServer.create(address, defaultMaximumConnections);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        RpcServicesLoader servicesLoader = new RpcServicesLoader(fromPackages);
        HttpRequestHandler handler = new HttpRequestHandler(servicesLoader);

        server.createContext("/", handler);
        server.setExecutor(null); // creates a default executor
    }
}
