package com.rozsa.rpc;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestHandler  implements HttpHandler {

    private Gson gson;

    private final RpcDispatcher dispatcher;

    protected HttpRequestHandler(RpcDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        gson = new Gson();
    }

    private List<Object> getParams(BufferedReader br, Class<?>[] paramTypes) {

        List<Object> params = new ArrayList<>();
        int paramTypesIndex = 0;

        JsonArray jsonArray = JsonParser.parseReader(br).getAsJsonArray();

        if (paramTypes.length != jsonArray.size()) {
            throw new IllegalArgumentException("Unexpected number of paramenters. Expected: " + paramTypes.length + "; Received: " + jsonArray.size());
        }

        for (JsonElement element : jsonArray) {

            if (paramTypesIndex >= paramTypes.length) {
                break;
            }

            Class<?> type = paramTypes[paramTypesIndex++];

            if (element.isJsonNull()) {
                params.add(null);
                continue;
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
        } catch (Exception e) {
            String message = e.getMessage();

            t.getResponseHeaders().add("Content-Type", "text/plain");
            t.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, message.length());
            OutputStream os = t.getResponseBody();
            os.write(message.getBytes());
            os.close();
            t.close();

            System.out.println(e + "\n");
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
            t.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            t.close();
            return;
        }

        String serviceName = pathParts[1];
        String procedureName = pathParts[2];

        if (!dispatcher.hasService(serviceName)) {
            t.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            t.close();
            return;
        }

        Method m = dispatcher.getProcedure(serviceName, procedureName);
        if (m == null) {
            t.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            t.close();
            return;
        }

        List<Object> params = getParams(br, m.getParameterTypes());

        Object res;
        try {
            res = dispatcher.run(serviceName, procedureName, params);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
            t.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            t.close();
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
            t.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            t.close();
            return;
        }

        ResultDto result = new ResultDto();
        result.setRes(res);
        String resultRaw = gson.toJson(result);

        t.getResponseHeaders().add("Content-Type", "application/json");
        t.sendResponseHeaders(HttpURLConnection.HTTP_OK, resultRaw.length());
        OutputStream os = t.getResponseBody();
        os.write(resultRaw.getBytes());
        os.close();
        t.close();
    }
}
