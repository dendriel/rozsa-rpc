package com.rozsa.rpc;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Service locator implementation which provides the RPC solution dependencies. It allows the client to override the main
 * componenets from the solution: {@link com.rozsa.rpc.NetworkServer}, {@link com.rozsa.rpc.RpcServicesLoader}) and
 * {@link com.rozsa.rpc.RequestHandler}.
 */
public final class DependencyProvider {
    private static final Map<Class<?>, Class<?>> dependencies = new HashMap<>() {{
        put(NetworkServer.class, HttpNetworkServer.class);
        put(RequestHandler.class, HttpRequestHandler.class);
        put(RpcServicesLoader.class, ReflectionServicesLoader.class);
    }};

    private DependencyProvider() {}

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

    /**
     * Overwrites default RPCServer related implementations.
     * @param contract Target contract to be overwritten.
     * @param impl Replacement implementation from the contract.
     * @throws ClassNotFoundException if target contract can`t be overwritten or doesn't exist.
     */
    public static void overwrite(Class<?> contract, Class<?> impl) throws ClassNotFoundException {
        if (!dependencies.containsKey(contract)) {
            throw new ClassNotFoundException();
        }

        dependencies.put(contract, impl);
    }
}
