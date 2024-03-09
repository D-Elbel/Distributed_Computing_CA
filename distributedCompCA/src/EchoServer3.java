import java.net.*;

public class EchoServer3 {
    public static void main(String[] args) {
        int serverPort = 7;    // default port

        if (args.length == 1)
            serverPort = Integer.parseInt(args[0]);

        try {
            ServerSocket myConnectionSocket = new ServerSocket(serverPort);
            System.out.println("Echo server ready.");

            while (true) {
                System.out.println("Waiting for a connection.");
                Socket clientSocket = myConnectionSocket.accept();
                System.out.println("Connection accepted from " + clientSocket.getInetAddress());

                // Create a new MyStreamSocket instance for each connection
                MyStreamSocket myDataSocket = new MyStreamSocket(clientSocket);

                Thread thread = new Thread(new EchoServerThread(myDataSocket));
                thread.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}