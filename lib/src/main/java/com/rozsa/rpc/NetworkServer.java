package com.rozsa.rpc;

import java.io.IOException;

/**
 * Listens for RPC requests and dispatches them to the RequestHandler.
 */
public interface NetworkServer {
    /**
     * Starts receiving requests.
     */
    void start(RequestHandler handler) throws IOException;
}
