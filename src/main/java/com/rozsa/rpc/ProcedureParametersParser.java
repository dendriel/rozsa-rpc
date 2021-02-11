package com.rozsa.rpc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.rozsa.rpc.ProcedureParametersParser.JsonParameterType.*;

class ProcedureParametersParser {

    private final Gson gson;

    ProcedureParametersParser(Gson gson) {
        this.gson = gson;
    }

    // is the type primitive in "json's eyes"? xD
    private boolean isPrimitive(Type type) {
        return type == Double.class || type == Float.class || type == Long.class ||
                type == Integer.class || type == Short.class || type == Character.class ||
                type == Byte.class || type == Boolean.class || type == String.class ||
                type == double.class || type == float.class || type == long.class ||
                type == int.class || type == short.class || type == char.class ||
                type == byte.class || type == boolean.class;
    }

    private boolean isNotPrimitive(Type type) {
        return !isPrimitive(type);
    }

    private boolean isArray(Type type) {
        if (type instanceof ParameterizedTypeImpl) {
            Class<?> rawType = ((ParameterizedTypeImpl) type).getRawType();
            /* Could have used something more generic, but it would made it more complex.
              * For instance: rawType.getSuperclass().[...] .getSuperclass().isAssignableFrom(Collection.class)
              */
            return rawType == List.class || rawType == Collection.class || rawType == Queue.class ||
                    rawType == Deque.class || rawType == Set.class;
        }

        return ((Class) type).isArray();
    }

    private boolean isNotArray(Type type) {
        return !isArray(type);
    }

    private boolean isExpectedParamType(Type param, JsonParameterType expectedType) {
        switch (expectedType) {
            case PRIMITIVE:
                return isPrimitive(param);
            case OBJECT:
                return isNotPrimitive(param) && isNotArray(param);
            case ARRAY:
                return isArray(param);
            case NULL:
                return true;
            default:
                return false;
        }
    }

    private List<RpcServiceHandler.RpcProcedureHandler> filterProcedures(List<RpcServiceHandler.RpcProcedureHandler> procedures, JsonParameterType paramType, int pos) {
        return procedures.stream()
                .filter(p -> isExpectedParamType(p.getParameterTypes()[pos], paramType))
                .collect(Collectors.toList());
    }

    private List<RpcServiceHandler.RpcProcedureHandler> filterProceduresByParamsCount(final List<RpcServiceHandler.RpcProcedureHandler> procedures, int count) {
        return procedures.stream()
                .filter(p -> p.getParameterTypes().length == count)
                .collect(Collectors.toList());
    }

    // We could create a hash from parameter types at wrapUp to avoid searching through procedures every time. But hash would not work well when receiving NULL args.
    public RpcServiceHandler.RpcProcedureHandler findProcedureByArgs(JsonArray jsonArray, final List<RpcServiceHandler.RpcProcedureHandler> procedures)
            throws IllegalArgumentException, NoSuchMethodException {
        if (procedures.size() == 1) {
            return procedures.get(0);
        }

        List<RpcServiceHandler.RpcProcedureHandler> remaining = new ArrayList<>(procedures);
        remaining = filterProceduresByParamsCount(remaining, jsonArray.size());

        int paramTypesIndex = 0;
        for (JsonElement element : jsonArray) {
            if (element.isJsonPrimitive()) {
                remaining = filterProcedures(remaining, PRIMITIVE, paramTypesIndex);
            }
            else if (element.isJsonObject()) {
                remaining = filterProcedures(remaining, OBJECT, paramTypesIndex);
            }
            else if (element.isJsonArray()) {
                remaining = filterProcedures(remaining, ARRAY, paramTypesIndex);
            }
            else if (element.isJsonNull()) {
                remaining = filterProcedures(remaining, NULL, paramTypesIndex);
            }
            else {
                throw new InternalError("Unknown json element found! " + element.getAsString());
            }

            if (remaining.size() == 0) {
                break;
            }

            paramTypesIndex++;
        }

        if (remaining.size() == 0) {
            throw new NoSuchMethodException();
        }

        if (remaining.size() > 1) {
            throw new IllegalArgumentException();
        }

        return remaining.get(0);
    }

    public List<Object> getParams(JsonArray jsonArray, Type[] paramTypes) throws IllegalArgumentException {
        List<Object> params = new ArrayList<>();

        if (paramTypes.length == 0) {
            return params;
        }

        int paramTypesIndex = 0;
        for (JsonElement element : jsonArray) {
            if (paramTypesIndex >= paramTypes.length) {
                break;
            }

            Type type = paramTypes[paramTypesIndex++];

            if (element.isJsonNull()) {
                params.add(null);
            }
            else if (element.isJsonObject()) {
                Object param = gson.fromJson(element.getAsJsonObject(), type);
                params.add(param);
            }
            else if(element.isJsonArray()) {
                Object param = gson.fromJson(element.getAsJsonArray(), type);
                params.add(param);
            }
            else if(element.isJsonPrimitive()) {
                Object param = gson.fromJson(element.getAsJsonPrimitive(), type);
                params.add(param);
            }
        }

        return params;
    }

    protected enum JsonParameterType {
        PRIMITIVE,
        OBJECT,
        ARRAY,
        NULL
    }
}
