package com.rozsa.rpc;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpRequestHandler  implements HttpHandler {
    private final Gson gson;
    private final RpcServicesLoader servicesLoader;
    private final ProcedureParametersParser parametersParser;

    protected HttpRequestHandler(RpcServicesLoader servicesLoader) {
        this.servicesLoader = servicesLoader;

        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        parametersParser = new ProcedureParametersParser(gson);
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        try {
            doHhandle(t);
        }
        catch (Exception e) {
            sendError(t, HttpURLConnection.HTTP_INTERNAL_ERROR, e.toString());
            e.printStackTrace();
        }
    }

    public void doHhandle(HttpExchange t) throws IOException {
        InputStreamReader isr = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        URI uri = t.getRequestURI();
        String path = uri.getPath();
        String[] pathParts = path.split("/");
        int pathPartsIdx = 1;

        if (pathParts.length <= 2) {
            sendError(t, HttpURLConnection.HTTP_BAD_REQUEST, RpcErrors.INVALID_ACTION);
            return;
        }

        // TODO: test contentType

        String serviceName = pathParts[pathPartsIdx++];
        RpcServiceHandler service = servicesLoader.getService(serviceName);
        if (service == null) {
            sendError(t, HttpURLConnection.HTTP_NOT_FOUND, RpcErrors.SERVICE_NOT_FOUND);
            return;
        }

        String procedureName = pathParts[pathPartsIdx++];
        List<RpcServiceHandler.RpcProcedureHandler> procedures = service.getProcedures(procedureName);
        if (procedures.size() == 0) {
            sendError(t, HttpURLConnection.HTTP_NOT_FOUND, RpcErrors.PROCEDURE_NOT_FOUND);
            return;
        }

        String method = t.getRequestMethod();
        JsonArray jsonArray = getRawParamsByMethod(method, br, pathParts, pathPartsIdx);
        if (jsonArray == null) {
            sendError(t, HttpURLConnection.HTTP_BAD_METHOD, RpcErrors.INVALID_REQUEST_METHOD);
            return;
        }

        boolean hasResponse;
        Object response;
        try {
            RpcServiceHandler.RpcProcedureHandler targetProcedure = parametersParser.findProcedureByArgs(jsonArray, procedures);

            List<Object> params = parametersParser.getParams(jsonArray, targetProcedure.getParameterTypes());
            response = targetProcedure.run(params);
            hasResponse = targetProcedure.hasResponse();
        }
        catch (IllegalArgumentException e) {
            sendError(t, HttpURLConnection.HTTP_BAD_REQUEST, RpcErrors.INVALID_ARGS_LIST);
            return;
        }
        catch (NoSuchMethodException e) {
            sendError(t, HttpURLConnection.HTTP_NOT_FOUND, RpcErrors.PROCEDURE_NOT_FOUND);
            return;
        }
        catch (Exception e) {
            sendError(t, HttpURLConnection.HTTP_INTERNAL_ERROR, e.toString());
            e.printStackTrace();
            return;
        }

        if (!hasResponse) {
            sendSuccessNotContent(t);
        }
        else {
            sendSuccess(t, response);
        }
    }

    private JsonArray getRawParamsByMethod(String method, BufferedReader br, String[] pathParts, int startIdx) {
        if (method.equals("POST")) {
            return JsonParser.parseReader(br).getAsJsonArray();
        }
        else if (method.equals("GET")) {
            return convertURIArgsToJsonArray(pathParts, startIdx);
        }
        else {
            return null;
        }
    }

    private JsonArray convertURIArgsToJsonArray(String[] args, int startIdx) {
        args = Arrays.copyOfRange(args, startIdx, args.length);
        List<Object> jsonArgs = Arrays.asList(args);

        JsonElement element = gson.toJsonTree(jsonArgs);
        return element.getAsJsonArray();
    }

    private void sendError(HttpExchange t, final int errorCode, String errorMessage) throws IOException {
        sendResponse(t, errorCode, errorMessage, "text/plain");
    }

    private void sendSuccess(HttpExchange t, Object response) throws IOException {
        String resultRaw = gson.toJson(response);
        sendResponse(t, HttpURLConnection.HTTP_OK, resultRaw, "application/json");
    }

    private void sendResponse(HttpExchange t, int code, String data, String contentType) throws IOException {
        t.getResponseHeaders().add("Content-Type", contentType);
        t.sendResponseHeaders(code, data.length());
        OutputStream os = t.getResponseBody();
        os.write(data.getBytes());
        os.close();
        t.close();
    }


    private void sendSuccessNotContent(HttpExchange t) throws IOException {
        t.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
        t.close();
    }
}
