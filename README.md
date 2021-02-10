# Rozsa RPC
Simple java HTTP-based RPC library.

## Features

- Invoked by simple HTTP GET/POST requests;
- Simple annotations usage:
  - @RpcService mark classes to be used in RPC;
  - @RpcProcedure mark methods to be called as procedures;
- Pre-cache necessary reflection data at wrap-up;
- Allow data structures (Array, List, Map, etc);
- Procedure overloading


## Procedure Overload

Procedures accept primitive (boxed and unboxed) and data-structure types. Overloads are allowed  with the following conditions:

- Procedures with a different count of parameters;
- Procedures with the same count of parameters, but with differentiable data types.

Parameters parsing differentiate between <b>Primitives, Objects and Arrays</b> types. Those may not correspond exactly to OO primitives and arrays, but they are a "Json-based view".

- <b>Primitives</b>: Double, Float, Long, Integer, Short, Character, Byte, Boolean, String and all their unboxed related types;
- <b>Arrays</b>: List, Collection and Queue;
- <b>Objects</b>: Maps and any other type that is not in the Primitives nor Arrays list.

When two or more procedures have the same signature and parameters count, the one who matches the received arguments list will be selected. For instance:

```Java

// Matching args would be: [ PRIMITIVE ]
public Book getBooks(int count);

/ Matching args would be: [ ARRAY ]
public Book getBooks(List<Integer> ids);

// Matching args would be: [ PRIMITIVE, ARRAY ]
public Book getBooks(int count, List<Integer> filter);

// Matching args would be: [ ARRAY, PRIMITIVE ]
public Book addBooks(List<String> names, String author);

// Matching args would be: [ OBJECT, PRIMITIVE ]
public Book addBooks(Map<String, String> relation, int publishingDate);
```

The selector will translate the received args only to the types listed above. So, two procedures, say "readBook(List<Integer> ids)" and "readBook(Collection<Integer> ids)" won't be distinguishable.

Also, null arguments are a permitted and will be matched with any of the available types (only if is still possible to find the target procedure).

## TODO

- Add client API builder
  - Add Sync and Async method calls
  - Add reactive apis
- Review available Array types.
