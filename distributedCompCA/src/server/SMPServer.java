package server;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;

public class SMPServer {
    public static void main(String[] args) {
        int serverPort = 7;    // default port

        if (args.length == 1)
            serverPort = Integer.parseInt(args[0]);

        try {
            System.out.println(System.getProperty("user.dir"));
            String ksName = "serverkeystore.jks";
            char ksPass[] = "123456".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream("src/server/serverkeystore.jks"), ksPass);


            //Used IBM and Stackoverflow as reference for some of this code, links in document
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, ksPass);

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(kmf.getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sc.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(serverPort);

            System.out.println("SMP server ready.");

            System.out.println(sslServerSocket.getSSLParameters());


            while (true) {
                System.out.println("Waiting for a connection.");
                SSLSocket clientSocket = (SSLSocket) sslServerSocket.accept();
                System.out.println("Connection accepted from " + clientSocket.getInetAddress());

                MyStreamSocket myDataSocket = new MyStreamSocket(clientSocket);

                Thread thread = new Thread(new SMPServerThread(myDataSocket));
                thread.start();
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}