package com.rozsa.rpc;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

class RpcServicesLoader {
    private final HashMap<String, Object> services;

    RpcServicesLoader() {
        this.services = new HashMap<>();
    }

    public boolean hasService(String serviceName) {
        return services.containsKey(serviceName);
    }

    public Object getService(String serviceName) {
        return services.get(serviceName);
    }

    public Method getProcedure(String serviceName, String procedureName) {
        Object service = services.get(serviceName);
        if (service == null) {
            return null;
        }

        // TODO: map the methods.
        Method[] methods = service.getClass().getMethods();
        for (Method m : methods) {
            if (m.getName().equals(procedureName)) {
                return m;
            }
        }

        return null;
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
            Object service = type.getDeclaredConstructor().newInstance();
            String serviceName = service.getClass().getAnnotation(RpcService.class).value();
            if (serviceName.isEmpty()) {
                serviceName = type.getSimpleName();
            }

            services.put(serviceName, service);
        }
    }
}
