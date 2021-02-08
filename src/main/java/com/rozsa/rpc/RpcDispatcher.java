package com.rozsa.rpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

class RpcDispatcher {

    private final RpcServicesLoader servicesLoader;

    public RpcDispatcher(RpcServicesLoader servicesLoader) {
        this.servicesLoader = servicesLoader;
    }

    public boolean hasService(String serviceName) {
        return servicesLoader.hasService(serviceName);
    }

    public Method getProcedure(String serviceName, String procedureName) {
        return servicesLoader.getProcedure(serviceName, procedureName);
    }

    public Object run(String serviceName, String procedureName, List<Object> params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodError {
        Object service = servicesLoader.getService(serviceName);

        Method m = getProcedure(serviceName, procedureName);
        if (m == null) {
            throw new NoSuchMethodError();
        }

        return m.invoke(service, params.toArray(new Object[0]));
    }
}
