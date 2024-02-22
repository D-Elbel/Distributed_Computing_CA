import java.net.*;

public class EchoServer3 {
   public static void main(String[] args) {
      int serverPort = 7;    // default port
      String message;

      if (args.length == 1 )
         serverPort = Integer.parseInt(args[0]);       
      try {
          ServerSocket myConnectionSocket = new ServerSocket(serverPort);
          System.out.println("Echo server ready.");

          while (true) {


              System.out.println("Waiting for a connection.");
              MyStreamSocket myDataSocket = new MyStreamSocket(myConnectionSocket.accept( ));
              System.out.println("connection accepted");
              Thread theThread = new Thread(new EchoServerThread(myDataSocket));
              theThread.start();

            }
       }
	    catch (Exception ex) {
          ex.printStackTrace( );
	    }
   }


}
