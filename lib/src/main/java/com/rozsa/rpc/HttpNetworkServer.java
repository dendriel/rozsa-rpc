package com.rozsa.rpc;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

class HttpNetworkServer implements NetworkServer {
    /**
     * Use system default to maximum TCP connections waiting at TCP socket.
     */
    private final static int defaultMaximumConnections = -1;

    private final String ip;
    private final Integer port;
    private HttpServer server;

    public HttpNetworkServer(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void start(RequestHandler handler) throws IOException {
        InetSocketAddress address = new InetSocketAddress(ip, port);
        server = HttpServer.create(address, defaultMaximumConnections);

        server.createContext("/", (HttpRequestHandler)handler);
        server.setExecutor(null);

        server.start();
    }
}
