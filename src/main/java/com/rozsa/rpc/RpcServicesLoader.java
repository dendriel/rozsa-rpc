package com.rozsa.rpc;

import com.rozsa.rpc.annotations.RpcService;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

class RpcServicesLoader {
    private final HashMap<String, RpcServiceHandler> services;

    RpcServicesLoader() {
        this.services = new HashMap<>();
    }

    public boolean hasService(String serviceName) {
        return services.containsKey(serviceName);
    }

    public RpcServiceHandler getService(String serviceName) {
        return services.get(serviceName);
    }

    public RpcServiceHandler.RpcProcedureHandler getProcedure(String serviceName, String procedureName) {
        RpcServiceHandler service = services.get(serviceName);
        if (service == null) {
            return null;
        }

        return service.getProcedureByName(procedureName);
    }

    void load(String fromPackage) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(fromPackage))
                .setScanners(new SubTypesScanner(),
                        new TypeAnnotationsScanner()) //.filterResultsBy(optionalFilter)),
                .filterInputsBy(new FilterBuilder().includePackage(fromPackage))
        );

//        Set<Class<? extends Object>> sr = reflections.getSubTypesOf(Object.class);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(RpcService.class);

        for (Class<?> type : annotated) {
            Object instance = type.getDeclaredConstructor().newInstance();
            String serviceName = instance.getClass().getAnnotation(RpcService.class).value();
            if (serviceName.isEmpty()) {
                serviceName = type.getSimpleName();
            }

            RpcServiceHandler serviceInfo = new RpcServiceHandler(serviceName, instance);
            serviceInfo.wrapUp();

            services.put(serviceName, serviceInfo);
        }
    }
}
