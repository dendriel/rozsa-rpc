package com.rozsa.rpc;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestHandler  implements HttpHandler {

    private final Gson gson;

    private final RpcDispatcher dispatcher;

    protected HttpRequestHandler(RpcDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        gson = new Gson();
    }

    private List<Object> getParams(BufferedReader br, Class<?>[] paramTypes) throws IllegalArgumentException {

        List<Object> params = new ArrayList<>();
        int paramTypesIndex = 0;

        if (paramTypes.length == 0) {
            return params;
        }

        JsonArray jsonArray = JsonParser.parseReader(br).getAsJsonArray();

        if (paramTypes.length != jsonArray.size()) {
            throw new IllegalArgumentException("Unexpected number of parameters. Expected: " + paramTypes.length + "; Received: " + jsonArray.size());
        }

        for (JsonElement element : jsonArray) {
            if (paramTypesIndex >= paramTypes.length) {
                break;
            }

            Class<?> type = paramTypes[paramTypesIndex++];

            if (element.isJsonNull()) {
                params.add(null);
            }
            else if (element.isJsonObject()) {
                Object param = gson.fromJson(element.getAsJsonObject(), type);
                params.add(param);
            }
            else if(element.isJsonArray()) {
                Object[] param = (Object[])gson.fromJson(element.getAsJsonArray(), type);
                params.add(param);
            }
            else if(element.isJsonPrimitive()) {
                Object param = gson.fromJson(element.getAsJsonPrimitive(), type);
                params.add(param);
            }
        }

        return params;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        try {
            doHhandle(t);
        }
        catch (Exception e) {
            sendError(t, HttpURLConnection.HTTP_INTERNAL_ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    public void doHhandle(HttpExchange t) throws IOException {
        InputStreamReader isr = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        URI uri = t.getRequestURI();
        String path = uri.getPath();
        String[] pathParts = path.split("/");

        if (pathParts.length <= 2) {
            sendError(t, HttpURLConnection.HTTP_BAD_REQUEST, RpcErrors.INVALID_ACTION);
            return;
        }

        String serviceName = pathParts[1];
        if (!dispatcher.hasService(serviceName)) {
            sendError(t, HttpURLConnection.HTTP_NOT_FOUND, RpcErrors.SERVICE_NOT_FOUND);
            return;
        }

        String procedureName = pathParts[2];
        Method m = dispatcher.getProcedure(serviceName, procedureName);
        if (m == null) {
            sendError(t, HttpURLConnection.HTTP_NOT_FOUND, RpcErrors.PROCEDURE_NOT_FOUND);
            return;
        }

        Object res;
        try {
            List<Object> params = getParams(br, m.getParameterTypes());
            res = dispatcher.run(serviceName, procedureName, params);
        }
        catch (IllegalArgumentException e) {
            sendError(t, HttpURLConnection.HTTP_BAD_REQUEST, RpcErrors.INVALID_ARGS_COUNT);
            return;
        }
        catch (NoSuchMethodError e) {
            sendError(t, HttpURLConnection.HTTP_NOT_FOUND, RpcErrors.PROCEDURE_NOT_FOUND);
            return;
        }
        catch (Exception e) {
            sendError(t, HttpURLConnection.HTTP_INTERNAL_ERROR, e.getMessage());
            e.printStackTrace();
            return;
        }

        ResultDto result = new ResultDto(res);
        String resultRaw = gson.toJson(result);

        sendSuccess(t, resultRaw);
    }

    private void sendError(HttpExchange t, final int errorCode, final String errorMessage) throws IOException {
        sendResponse(t, errorCode, errorMessage, "text/plain");
    }

    private void sendSuccess(HttpExchange t, String data) throws IOException {
        sendResponse(t, HttpURLConnection.HTTP_OK, data, "application/json");
    }

    private void sendResponse(HttpExchange t, int code, String data, String contentType) throws IOException {
        t.getResponseHeaders().add("Content-Type", contentType);
        t.sendResponseHeaders(code, data.length());
        OutputStream os = t.getResponseBody();
        os.write(data.getBytes());
        os.close();
        t.close();
    }
}
