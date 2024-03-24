import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

public class EchoServer3 {
    public static void main(String[] args) {
        int serverPort = 7;    // default port

        if (args.length == 1)
            serverPort = Integer.parseInt(args[0]);

        try {
            String ksName = "C:\\Users\\Darragh\\Distributed_Computing_CA\\distributedCompCA\\src\\serverkeystore.jks";
            char ksPass[] = "123456".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(ksName), ksPass);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, ksPass);

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(kmf.getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sc.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(serverPort);

            System.out.println("Echo server ready.");

            while (true) {
                System.out.println("Waiting for a connection.");
                SSLSocket clientSocket = (SSLSocket) sslServerSocket.accept();
                System.out.println("Connection accepted from " + clientSocket.getInetAddress());

                MyStreamSocket myDataSocket = new MyStreamSocket(clientSocket);

                Thread thread = new Thread(new EchoServerThread(myDataSocket));
                thread.start();
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}