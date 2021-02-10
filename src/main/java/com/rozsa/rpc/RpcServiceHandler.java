package com.rozsa.rpc;

import com.rozsa.rpc.annotations.RpcProcedure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class RpcServiceHandler {
    private final String serviceName;
    private final Object instance;

    private final HashMap<String, List<RpcProcedureHandler>> procedures;

    RpcServiceHandler(String serviceName, Object instance) {
        this.serviceName = serviceName;
        this.instance = instance;

        procedures = new HashMap<>();
    }

    public void wrapUp() {
        Method[] methods = instance.getClass().getMethods();
        for (Method m : methods) {
            RpcProcedureHandler procedureHandler = wrapUpProcedure(m);
            if (procedureHandler == null) {
                continue;
            }

            String procedureName = procedureHandler.getProcedureName();
            procedures.putIfAbsent(procedureName, new ArrayList<>());
            List<RpcProcedureHandler> procedureHandlers = procedures.get(procedureName);
            procedureHandlers.add(procedureHandler);
        }
    }

    private RpcProcedureHandler wrapUpProcedure(Method m) {
        RpcProcedure procedure = m.getAnnotation(RpcProcedure.class);
        if (procedure == null) {
            return null;
        }

        String procedureName = procedure.name();
        if (procedureName.isEmpty()) {
            procedureName = m.getName();
        }

        RpcProcedureHandler procedureHandler = this.new RpcProcedureHandler(procedureName, m);
        procedureHandler.wrapUp();
        return procedureHandler;
    }

    public RpcProcedureHandler getProcedureByName(String procedureName) {
        List<RpcProcedureHandler> procedureHandlers = procedures.get(procedureName);
        if (procedureHandlers == null) {
            return null;
        }

        // TODO: find procedure handler by parameters.
        return procedureHandlers.get(0);
    }

    public List<RpcProcedureHandler> getProcedures(String procedureName) {
        List<RpcProcedureHandler> procedureHandlers = procedures.get(procedureName);
        if (procedureHandlers == null) {
            return new ArrayList<>();
        }

        return procedureHandlers;
    }

    public class RpcProcedureHandler {
        private final String procedureName;
        private final Method method;
        private Type[] paramTypes;

        public RpcProcedureHandler(String procedureName, Method method) {
            this.procedureName = procedureName;
            this.method = method;
        }

        private String getProcedureName() {
            return procedureName;
        }

        public Object run(List<Object> params) throws InvocationTargetException, IllegalAccessException {
            return method.invoke(instance, params.toArray(new Object[0]));
        }

        public Type[] getParameterTypes() {
            return paramTypes;
        }

        private void wrapUp() {
            paramTypes = method.getGenericParameterTypes();
        }
    }
}
