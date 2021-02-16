package com.rozsa.rpc;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DependencyProvider {
    private static final Map<Class<?>, Class<?>> dependencies = new HashMap<>() {{
        put(NetworkServer.class, HttpNetworkServer.class);
        put(RequestHandler.class, HttpRequestHandler.class);
        put(RpcServicesLoader.class, ServicesLoader.class);
    }};

    static <T> T provide(Class<?> clazz, Object... initArgs)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        List<Class<?>> constructorParamTypes = new ArrayList<>();
        Arrays.stream(initArgs).forEach(a -> constructorParamTypes.add(a.getClass()));
        Class<?>[] parameterTypes = constructorParamTypes.toArray(new Class<?>[0]);

        return provide(clazz, parameterTypes, initArgs);
    }

    static <T> T provide(Class<?> clazz, Class<?>[] parameterTypes, Object... initArgs)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Class<?> targetClazz = dependencies.get(clazz);
        return (T)targetClazz.getConstructor(parameterTypes).newInstance(initArgs);
    }

    public static void overwrite(Class<?> contract, Class<?> impl) throws ClassNotFoundException {
        if (!dependencies.containsKey(contract)) {
            throw new ClassNotFoundException();
        }

        dependencies.put(contract, impl);
    }
}
