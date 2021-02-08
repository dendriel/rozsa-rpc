package com.rozsa.rpc;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

class RpcDispatcher {
    private final RpcServicesLoader servicesLoader;

    public RpcDispatcher(RpcServicesLoader servicesLoader) {
        this.servicesLoader = servicesLoader;
    }

    public boolean hasService(String serviceName) {
        return servicesLoader.hasService(serviceName);
    }

    public RpcServiceHandler.RpcProcedureHandler getProcedure(String serviceName, String procedureName) {
        return servicesLoader.getProcedure(serviceName, procedureName);
    }

    public Object run(String serviceName, String procedureName, List<Object> params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodError {
        RpcServiceHandler handler = servicesLoader.getService(serviceName);
        return handler.invoke(procedureName, params);
    }
}
