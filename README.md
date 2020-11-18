# Habbo2020

This repository contains some work and information regarding the Habbo 2020 client. 

> This contains research done in one day so do not expect much, but it may get you set up fast.

## Client

You can find the client below. The `Metadata` file contains most important info, you can find the rest of the files required by looking at the `index.html`.

**Base URL:** https://images.habbo.com/habbo-webgl-clients/7_9dbaf1bed7ed1ca7105f027d02e79be5

> In the future you might need to replace `7_9dbaf1bed7ed1ca7105f027d02e79be5` with another string to get a more up-to-date client.

| Name | URL |
|-|-|
| Loader | /WebGL/habbo2020-global-prod/index.html |
| Metadata | /WebGL/habbo2020-global-prod/Build/habbo2020-global-prod.json |
| Asset data and Scenes | /WebGL/habbo2020-global-prod/Build/habbo2020-global-prod.data.unityweb |
| WebAssembly binary | /WebGL/habbo2020-global-prod/Build/habbo2020-global-prod.wasm.code.unityweb | 
| JavaScript runtime and plugins | /WebGL/habbo2020-global-prod/Build/habbo2020-global-prod.wasm.framework.unityweb |

## Client protocol

The client connects by default with a WebSocket to `wss://game-s2.habbo.com:30001/websocket`. You can change the IP it uses by placing the following code in your client `index.html`.

```html
<script type="text/javascript">
    (function(){
        var ws = window.WebSocket;

        window.WebSocket = function (a, b) {
            if (a == 'wss://game-s2.habbo.com:30001/websocket') {
                a = 'wss://localhost:30001/websocket';
            }

            return b ? new ws(a, b) : new ws(a);
        };

        window.WebSocket.prototype=ws.prototype; 
    }());
</script>
```

The connection to the server is using SSL twice. This is pretty annoying to deal with because there are not really TLS wrappers available that can deal with WebSocket sockets. I have provided a simple netty server which implements this protocol, although I don't know if it is correct because I have not yet been able to patch the certificate check.

When you connect to the Habbo server with an unmodified client, you can view in the chrome network tab that it sends `StartTLS`. Server responds with `OK` and then the client proceeds to start the TLS handshake. When you connect to the normal Habbo server, all goes well and you can not view plaintext messages after the handshake.

### Patching

However, when the client connects to your own server with your own certificate and private key, the client will not respond to your certificate. The client will need to be patched so it accepts any (or your) certificate.

One thing I found out is that there is a string `game-*.habbo.com` in the `global-metadata.dat` as string literal. This matches with the `Common Name` of the first certificate that the official Habbo server sends to the client. If you change this string in memory (i.e. by changing one byte), the client will show the same behaviour as when it was connecting to your own server. Attempting to patch this string to `localhost` and the string length to `9` showed the same behaviour so either I did it wrong or there is another check. Most likely there is some public key embedded in the client, as that is a pretty common SSL pinning method.

## WebAssembly Reverse Engineering

It is pretty annoying to reverse engineer WebAssembly at this time. The chrome debugger caps at  1 million lines of code which prevents you from debugging the client properly. 

You can use the tool `wasm2wat` from [WebAssembly/wabt](https://github.com/WebAssembly/wabt) to get a _readable_ `.wat` file from the client. You need to use this on the `WebAssembly binary` file from above. To recompile you need to use `wat2wasm`.

Tools:

- Browser extension for hacking WebAssembly games a la Cheat Engine  
  Make sure to recursively clone the repository instead of downloading the latest release  
  https://github.com/Qwokka/Cetus 
- JEB (Crashes)  
  https://www.pnfsoftware.com/
- idawasm (Outdated)  
  https://github.com/fireeye/idawasm

## Il2Cpp Reverse Engineering

The Habbo client is C# > Il2Cpp > WASM. In order to get all Il2Cpp metadata you need a `global-metadata.dat` file and the binary. The binary in the case of Habbo is listed above as `WebAssembly binary`. The `global-metadata.dat` must be extracted from the `Asset data and Scenes` file. I have provided a C# tool which can do this in the repository, it can unpack and repack `UnityWebData1.0` files.

You can get a metadata dump using Il2CppDumper https://github.com/Perfare/Il2CppDumper.

> At the time of writing this README, you will need to grab the latest binaries from the AppVeyor of Il2CppDumper. The release of v6.4.12 **does not work** with the Habbo client properly, the latest one from AppVeyor **does**.