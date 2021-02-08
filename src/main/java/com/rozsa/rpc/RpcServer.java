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

    public void start(String fromPackage) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        wrapUp(ip, port, fromPackage);
        server.start();
    }

    private void wrapUp(String ip, int port, String fromPackage) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try {
            InetSocketAddress address = new InetSocketAddress(ip, port);
            server = HttpServer.create(address, defaultMaximumConnections);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        RpcServicesLoader servicesLoader = new RpcServicesLoader();
        servicesLoader.load(fromPackage);
        RpcDispatcher dispatcher = new RpcDispatcher(servicesLoader);
        HttpRequestHandler handler = new HttpRequestHandler(dispatcher);

        server.createContext("/", handler);
        server.setExecutor(null); // creates a default executor
    }

//    private void loadServices(String fromPackage) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
//        Reflections reflections = new Reflections(new ConfigurationBuilder()
//                .setUrls(ClasspathHelper.forPackage(fromPackage))
//                .setScanners(new SubTypesScanner(),
//                            new TypeAnnotationsScanner()) //.filterResultsBy(optionalFilter)),
//                .filterInputsBy(new FilterBuilder().includePackage(fromPackage))
//        );
//
////        Set<Class<? extends Object>> sr = reflections.getSubTypesOf(Object.class);
//        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(RpcService.class);
//
//        for (Class<?> type : annotated) {
//            Object service = type.getDeclaredConstructor().newInstance();
//            String serviceName = service.getClass().getAnnotation(RpcService.class).value();
//            if (serviceName.isEmpty()) {
//                serviceName = type.getSimpleName();
//            }
//
//            services.put(serviceName, service);
//        }
//    }
}
