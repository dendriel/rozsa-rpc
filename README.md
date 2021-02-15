# Rozsa RPC
Simple java HTTP-based RPC library.

## Features

- Invoked by simple HTTP GET/POST requests;
- Simple annotations usage:
  - @RpcService mark classes to be used in RPC;
  - @RpcProcedure mark methods to be called as procedures;
- Simple initialization (setup just by setting target service's packages);
- Pre-cache necessary reflection data at wrap-up;
- Allow data structures (Array, List, Map, etc);
- Allow procedure overloading.


## GET/POST Requests

Use <b>GET</b> for lightweight requests. Procedures called by GET have their arguments (if any) encoded in the URI (as
path variables). For instance, the following procedure:
```Java
// invoked by using GET /serviceName/procedureName/param1/param2
@RpcProcedure
public List<Post> read(String input, int count);
```

May be invoked by using ``http://localhost/serviceName/read/dummyText/123/``. The downside is that it only supports
Primitive types as arguments.

Use <b>POST</b> when it's necessary to transfer larger data payloads. When using POST, the URI arguments won't be used
and the procedure arguments will be retrieved from the request body. For instance:
```Java
// invoked by using POST /serviceName/procedureName
@RpcProcedure
public void createAll(List<Post> post, Date createdAt); // test with a date.
```

May be invoked by using ``http://localhost/serviceName/createAll`` with the following request content:
```JSON
[
  // First argument - Array (List<T>)
    [
        
        {
            "text": "My first blog post",
            "author": "Vitor Rozsa",
            "stars": 5
        },
        {
            "text": "My second blog post",
            "author": "Rozsa RPC",
            "stars": 5
        }
    ],
  // Second argument - Primitive (Date)
    "2021-02-15T11:40:15.1234-03:00"
]
```

It is also possible to use POST (instead of GET) to invoke procedures that requires only primitive arguments. The
arguments will be retrieved from the request body.

## Procedure Overload

Procedures accept primitive (boxed and unboxed) and data-structure types. Overloads are allowed  with the following
conditions:

- Procedures with a different count of parameters;
- Procedures with the same count of parameters, but with differentiable data types.

Parameters parsing differentiate between <b>Primitives, Objects and Arrays</b> types. *Those may not correspond exactly
to OO primitives and arrays, but they are a "Json-based view".

- <b>Primitives</b>: Date, Double, Float, Long, Integer, Short, Character, Byte, Boolean, String and all their unboxed
  counterparts;
- <b>Arrays</b>: Array, List, Collection, Queue, Deque and Set;
- <b>Objects</b>: Maps and any other type that is not in the Primitives nor Arrays list.

When two or more procedures have the same signature and parameters count, the one who matches the received arguments
list will be selected. For instance:

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

The selector will translate the received args only to the types listed above. So, two procedures, say
`readBook(List<Integer> ids)` and `readBook(Collection<Integer> ids)` won't be distinguishable.

Also, null arguments are a permitted and will be matched with any of the available types (only if is still possible to
find the target procedure).


### Expected Date Format

```Json
"yyyy-MM-dd'T'HH:mm:ss.SSSZ"
```

For instance: ``2021-02-15T11:40:15.1234-03:00``

## TODO

- Add client API builder
  - Add Sync and Async method calls
  - Add reactive apis
- Add usage instructions with code examples;
- Decouple transport code to allow using other implementations.
