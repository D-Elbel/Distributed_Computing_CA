package client;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.KeyStore;

public class SMPClientHelper {
   static final String endMessage = ".";
   private MyStreamSocket mySocket;
   private InetAddress serverHost;
   private int serverPort;

   SMPClientHelper(String hostName, String portNum) throws Exception {
      this.serverHost = InetAddress.getByName(hostName);
      this.serverPort = Integer.parseInt(portNum);

      String tsName = "src\\client\\clienttruststore.jks";
      char tsPass[] = "123456".toCharArray();
      KeyStore ts = KeyStore.getInstance("JKS");
      ts.load(new FileInputStream(tsName), tsPass);

      TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      tmf.init(ts);

      SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, tmf.getTrustManagers(), null);

      SSLSocketFactory sslSocketFactory = sc.getSocketFactory();

      SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverHost, serverPort);

      this.mySocket = new MyStreamSocket(sslSocket);
      System.out.println("Connection request made");
   }

   public String getEcho(String message) throws IOException, IOException {
      String echo = "";
      mySocket.sendMessage(message);
      // now receive the echo
      echo = mySocket.receiveMessage();
      return echo;
   }

   public void done() throws SocketException, IOException {
      mySocket.sendMessage(endMessage);
      mySocket.close();
   }
}