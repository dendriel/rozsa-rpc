# Rozsa RPC
Simple java HTTP-based RPC library.

## Features

- Invoked by simple HTTP GET/POST requests;
- Simple annotations usage:
  - @RpcService mark classes to be used in RPC;
  - @RpcProcedure mark methods to be called as procedures;
- Pre-cache necessary reflection data at wrap-up; 
- Allow data structures (Array, List, Map, etc). 

## TODO

- Allow procedure overload;
- Add client API builder
  - Add Sync and Async method calls
  - Add reactive apis
- Remove the unnecessary dispatcher.
