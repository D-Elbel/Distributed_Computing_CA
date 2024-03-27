package client;

import java.io.*;

/**
 * This module contains the presentaton logic of an Echo Client.
 * @author M. L. Liu
 */
public class EchoClient2 {
   static final String endMessage = ".";
   public static void main(String[] args) {
      InputStreamReader is = new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(is);
      try {
         System.out.println("Welcome to the Echo client.\n" +
            "What is the name of the server host?");
         String hostName = br.readLine();
         if (hostName.length() == 0) // if user did not enter a name
            hostName = "localhost";  //   use the default host name
         System.out.println("What is the port number of the server host?");
         String portNum = br.readLine();
         if (portNum.length() == 0)
            portNum = "7";          // default port number
         SMPClientHelper helper =
            new SMPClientHelper(hostName, portNum);
         boolean done = false;
         String message, echo;
         System.out.println("Welcome to SMP Client. Please LOGON before issuing any other commands");
         while (!done) {
            message = br.readLine( );
            if ((message.trim()).equals (endMessage)){
               done = true;
               helper.done( );
            }
            else {
               echo = helper.getEcho( message);
               System.out.println(echo);
            }
          }
      }
      catch (Exception ex) {
         ex.printStackTrace( );
      }
   }
}
