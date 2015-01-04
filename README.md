# unisockets

> dressing up [unix domain sockets](http://en.wikipedia.org/wiki/Unix_domain_socket) in a tcp [socket](http://docs.oracle.com/javase/7/docs/api/java/nio/channels/SocketChannel.html) shirt and tie.

<p>
  <img height="175" src="https://rawgit.com/softprops/unisockets/master/us.svg"/>
</p>


## usage

_note_: This library requires at a minimum a java 7 jre, as the [SocketChannel](http://docs.oracle.com/javase/7/docs/api/java/nio/channels/SocketChannel.html) class changed to implement a new [NetworkChannel](http://docs.oracle.com/javase/7/docs/api/java/nio/channels/NetworkChannel.html) interface in java 7.

A unix domain socket is means of interprocess communication via data streamed through a local file descriptor.

unisockets, like tcp sockets, need to be addressable. unisockets defines an implementation of a `SocketAddress` for these file descriptors called an `Addr`.

```scala
import java.io.File
val addr = Addr(new File("/var/run/unix.sock"))
```

You can create both instances of nio SocketChannels

```scala
val channel = unisockets.SocketChannel.open(addr)
```

and old io Sockets with these Addrs

```scala
val socket = unisockets.Socket.open(addr)
```

You can also create disconnected instances of each calling `open` without arguments and calling `connect(addr)` at a deferred time. This library aims to stay close to familiar factory methods defined in their [std lib counterparts](http://docs.oracle.com/javase/7/docs/api/java/nio/channels/SocketChannel.html#open())

### netty

The `unisockets-netty` module provides a netty `NioSocketChannel` backed by a `unisockets.SocketChannel`, enabling you to
build netty clients for UNIX domain socket servers.

```scala 
import unisockets.ClientUdsSocketChannelFactory
val sockets = new ClientUdsSocketChannelFactory()
```

This nio socket channel factory share's many similarities with [NioClientSocketChannelFactories](http://netty.io/3.10/api/org/jboss/netty/channel/socket/nio/NioClientSocketChannelFactory.html)

Client's useing this interface should make sure they call `ClientUdsSocketChannelFactory#releaseExternalResources` to release any resources 
acquired during request processing.

note: the netty interface has only been tested with a netty client pipeline with version `3.9.6.Final` newer versions ( netty 4+ ) are not supported yet but support is planned to be added in the future.

Doug Tangren (softprops) 2014-2015
