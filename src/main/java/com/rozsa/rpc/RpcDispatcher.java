package com.rozsa.rpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class RpcDispatcher {

    private final Map<String, Object> services;

    public RpcDispatcher(Map<String, Object> services) {
        this.services = services;
    }

    public boolean hasService(String serviceName) {
        return services.containsKey(serviceName);
    }

    public boolean hasProcedure(String serviceName, String procedureName) {
        Object service = services.get(serviceName);
        if (service == null) {
            return false;
        }

        Method[] methods = service.getClass().getMethods();
        for (Method m : methods) {
            if (m.getName().equals(procedureName)) {
                return true;
            }
        }

        return false;
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

    public Object run(String serviceName, String procedureName, List<Object> params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodError {
        Object service = services.get(serviceName);

        Method m = getProcedure(serviceName, procedureName);
        if (m == null) {
            throw new NoSuchMethodError();
        }

        return m.invoke(service, params.toArray(new Object[0]));
    }
}
