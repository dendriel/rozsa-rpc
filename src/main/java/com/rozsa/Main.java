package com.rozsa;

import com.rozsa.rpc.RpcServer;

public class Main {

    public static void main(String[] args) {

        RpcServer rpc = new RpcServer(8000);
        rpc.start("com.rozsa.services");

        System.out.println("Server is running!");
    }

//    // TODO:
//    /**
//     * Learn how to use reflection to find annotated classes in package and retrieve it annotated methods.
//     */
//
//    static class CalcHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange t) throws IOException {
//            InputStreamReader isr =  new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
//            BufferedReader br = new BufferedReader(isr);
//
//            Calculator calc = new Calculator();
//
//            URI uri = t.getRequestURI();
//            String path = uri.getPath();
//            String[] pathParts = path.split("/");
//
//            if (pathParts.length <= 2) {
//                t.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
//                t.close();
//                return;
//            }
//
//            String procedureName = pathParts[2];
//
//            Gson gson = new Gson();
//            ParamsDto procedureParams = gson.fromJson(br, ParamsDto.class);
//
//            int res;
//            switch (procedureName) {
//                case "sum":
//                    res = calc.sum(procedureParams.getNextInteger(), procedureParams.getNextInteger());
//                    break;
//                default:
//                    t.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
//                    t.close();
//                    return;
//            }
//
//            ResultDto result = new ResultDto();
//            result.setRes(res);
//            String resultRaw = gson.toJson(result);
//
//            t.getResponseHeaders().add("Content-Type", "application/json");
//            t.sendResponseHeaders(HttpURLConnection.HTTP_OK , resultRaw.length());
//            OutputStream os = t.getResponseBody();
//            os.write(resultRaw.getBytes());
//            os.close();
//            t.close();
//        }
//    }

//    static class GetHandler implements HttpHandler {
//        public void handle(HttpExchange t) throws IOException {
//
//            // add the required response header for a PDF file
//            Headers h = t.getResponseHeaders();
//            h.add("Content-Type", "application/pdf");
//
//            // a PDF (you provide your own!)
//            File file = new File ("c:/tmp/sample.pdf");
//            byte [] bytearray  = new byte [(int)file.length()];
//            FileInputStream fis = new FileInputStream(file);
//            BufferedInputStream bis = new BufferedInputStream(fis);
//            bis.read(bytearray, 0, bytearray.length);
//
//            // ok, we are ready to send the response.
//            t.sendResponseHeaders(200, file.length());
//            OutputStream os = t.getResponseBody();
//            os.write(bytearray,0,bytearray.length);
//            os.close();
//        }
//    }
}
