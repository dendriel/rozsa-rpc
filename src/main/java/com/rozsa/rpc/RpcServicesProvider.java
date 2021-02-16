package com.rozsa.rpc;

/**
 * Provides services for RPC.
 */
public interface RpcServicesProvider {
    /**
     * Get a service by its name.
     * @param name target service's name.
     * @return the target service; null if not found.
     */
    RpcServiceHandler getServiceByName(String name);
}
