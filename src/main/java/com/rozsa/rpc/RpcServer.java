package com.rozsa.rpc;

import com.sun.net.httpserver.HttpServer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Set;

// TODO: allow to startup with debug mode.
public class RpcServer {

    /**
     * Use system default to maximum TCP connections waiting at TCP socket.
     */
    private final static int defaultMaximumConnections = -1;

    private final String ip;
    private final int port;

    // service name - data
    private final HashMap<String, Object> services;

    // TODO: abstract the transport layer.
    private HttpServer server;

    public RpcServer(int port) {
        this("localhost", port);
    }

    public RpcServer(String ip, int port) {
        this.ip = ip;
        this.port = port;

        services = new HashMap<>();
    }

    public void start(String fromPackage) {
        try {
            loadServices(fromPackage);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        wrapUp(ip, port);
        server.start();
    }

    private void wrapUp(String ip, int port) {
        try {
            InetSocketAddress address = new InetSocketAddress(ip, port);
            server = HttpServer.create(address, defaultMaximumConnections);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        RpcDispatcher dispatcher = new RpcDispatcher(services);
        HttpRequestHandler handler = new HttpRequestHandler(dispatcher);

        server.createContext("/", handler);
        server.setExecutor(null); // creates a default executor
    }

    private void loadServices(String fromPackage) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(fromPackage))
                .setScanners(new SubTypesScanner(),
                            new TypeAnnotationsScanner()) //.filterResultsBy(optionalFilter)),
                .filterInputsBy(new FilterBuilder().includePackage(fromPackage))
        );

//        Set<Class<? extends Object>> sr = reflections.getSubTypesOf(Object.class);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(RpcService.class);

        for (Class<?> type : annotated) {
            Object service = type.getDeclaredConstructor().newInstance();
            String serviceName = service.getClass().getAnnotation(RpcService.class).value();
            if (serviceName.isEmpty()) {
                serviceName = type.getSimpleName();
            }

            services.put(serviceName, service);
        }
    }
}
