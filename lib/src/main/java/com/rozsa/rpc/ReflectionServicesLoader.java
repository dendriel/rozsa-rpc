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

class ReflectionServicesLoader implements RpcServicesLoader, RpcServicesProvider {
    private final HashMap<String, RpcServiceHandler> services;
    private final String[] packages;

    public ReflectionServicesLoader(String... packages) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.packages = packages;
        this.services = new HashMap<>();

        wrapUp();
    }

    public RpcServiceHandler getServiceByName(String name) {
        return services.get(name);
    }

    private void wrapUp() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (String p : packages) {
            load(p);
        }
    }

    private void load(String fromPackage) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(fromPackage))
                .setScanners(new SubTypesScanner(),
                        new TypeAnnotationsScanner())
                .filterInputsBy(new FilterBuilder().includePackage(fromPackage))
        );

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
