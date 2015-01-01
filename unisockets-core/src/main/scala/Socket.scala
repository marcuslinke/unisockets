package unisockets

import java.net.{
  Socket => JSocket,
  SocketAddress
}
import java.io.{ File, IOException }
import java.nio.channels.Channels
import jnr.unixsocket.{ UnixSocketAddress, UnixSocketChannel }

object Socket {
  def open(file: File): Socket =
    open(Addr(file))
  
  def open(addr: Addr): Socket =
    Socket(UnixSocketChannel.open(addr.addr))

  def open(): Socket =
    Socket(UnixSocketChannel.open())
}

case class Socket private[unisockets](
  private val chan: UnixSocketChannel,
  private val socketChannel: Option[SocketChannel] = None) extends JSocket {
  @volatile private[this] var closed = false
  @volatile private[this] var indown = false
  @volatile private[this] var outdown = false
  private[this] lazy val in = Channels.newInputStream(chan)
  private[this] lazy val out = Channels.newOutputStream(chan)

  override def bind(jaddr: SocketAddress) =
    Unsupported.bind

  override def close() = {
    chan.close()
    closed = true
  }

  override def connect(jaddr: SocketAddress) =
    connect(jaddr, 0)

  override def connect(jaddr: SocketAddress, timeout: Int) =
    jaddr match {
      case unix: Addr =>
        chan.connect(unix.addr) // timeout not supported
      case _ =>
        throw new IllegalArgumentException(
          s"address of type ${jaddr.getClass} are not supported. use a unisockets.Addr")
    }

  override def getChannel =
    socketChannel.orNull

  override def getInetAddress = null

  override def getInputStream =
    if (chan.isConnected) in else throw new IOException("not connected")

  //override def getKeepAlive = false

  //override def getLocalAddress = null

  //override def getLocalPort = 0

  override def getLocalSocketAddress =
    Option(chan.getLocalSocketAddress).map(Addr(_)).orNull

  //override def getOOBInline =
  //  false

  override def getOutputStream =
    if (chan.isConnected) out else throw new IOException("not connected")

  //override def getPort = 0

  //override def getReceiveBufferSize = null

  override def getRemoteSocketAddress: SocketAddress =
    Option(chan.getRemoteSocketAddress).map(Addr(_)).orNull

  //override def getReuseAddress = null

  //override def getSendBufferSize = null

  //override def getSoLinger = null

  //override def getSoTimeout = null

  //override def getTcpNoDelay = false

  //override def getTrafficClass = null

  override def isBound = false

  override def isClosed = closed

  override def isConnected = chan.isConnected

  override def isInputShutdown = indown

  override def isOutputShutdown = outdown

  //override def sendUrgentData(data: Int) {}

  //override def setKeepAlive(ka: Boolean) {
    //Native.setsockopt(chan.getFD, SocketLevel.SOL_SOCKET, Socket.SO_KEEPALIVE, on)
  //}

  //override def setOOBInline(in: Boolean) {}

  //override def setPerformancePreferences(connectionTime: Int, latency: Int, bandwidth: Int) {}

  //override def setReceiveBufferSize(size: Int) {}

  //override def setReuseAddress(on: Boolean) {}

  //override def setSendBufferSize(size: Int) {}

  //override def setSoLinger(on: Boolean, liger: Int) {}

  //override def setSoTimeout(to: Int) {}

  //override def setTcpNoDelay(on: Boolean) {}

  //override def setTrafficClass(tc: Int) {}

  override def shutdownInput() = {
    chan.shutdownInput
    indown = true
  }

  override def shutdownOutput() = {
    chan.shutdownOutput
    outdown = true
  }
}
