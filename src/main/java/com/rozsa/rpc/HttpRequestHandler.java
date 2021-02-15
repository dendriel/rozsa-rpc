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

        gson = new Gson();
        parametersParser = new ProcedureParametersParser(gson);
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
        RpcServiceHandler service = servicesLoader.getService(serviceName);
        if (service == null) {
            sendError(t, HttpURLConnection.HTTP_NOT_FOUND, RpcErrors.SERVICE_NOT_FOUND);
            return;
        }

        String procedureName = pathParts[2];
        List<RpcServiceHandler.RpcProcedureHandler> procedures = service.getProcedures(procedureName);
        if (procedures.size() == 0) {
            sendError(t, HttpURLConnection.HTTP_NOT_FOUND, RpcErrors.PROCEDURE_NOT_FOUND);
            return;
        }

        boolean hasResponse;
        Object response;
        try {
            JsonArray jsonArray = JsonParser.parseReader(br).getAsJsonArray();
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
            sendError(t, HttpURLConnection.HTTP_INTERNAL_ERROR, e.getMessage());
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

    private void sendError(HttpExchange t, final int errorCode, final String errorMessage) throws IOException {
        sendResponse(t, errorCode, errorMessage, "text/plain");
    }

    private void sendSuccess(HttpExchange t, Object response) throws IOException {
        ResultDto result = new ResultDto(response);
        String resultRaw = gson.toJson(result);

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
        t.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, 0);;
        t.close();
    }
}
