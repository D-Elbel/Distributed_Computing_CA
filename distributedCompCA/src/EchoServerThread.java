import java.io.*;
import java.util.ArrayList;

/**
 * This module is to be used with a concurrent Echo server.
 * Its run method carries out the logic of a client session.
 * @author M. L. Liu
 */

class EchoServerThread implements Runnable {
   static final String endMessage = ".";
   MyStreamSocket myDataSocket;
   boolean isLoggedIn;
   static UserSession user;

   EchoServerThread(MyStreamSocket myDataSocket) {
      this.myDataSocket = myDataSocket;
      this.isLoggedIn = false;
   }
 
   public void run( ) {
      boolean done = false;
      String message;
      try {
         while (!done) {
             message = myDataSocket.receiveMessage( );
/**/         System.out.println("message received: "+ message);
             if ((message.trim()).equals (endMessage)){

/**/            System.out.println("Session over.");
                myDataSocket.close( );
                done = true;
             }
             else {

                 if(parseReqType(message) == GlobalErrorMessages.NO_ERR){

                     myDataSocket.sendMessage(message);
                 }
                 else{
                     myDataSocket.sendMessage(getErrType(parseReqType(message)));
                 }

             }
          }
        }
        catch (Exception ex) {
           System.out.println("Exception caught in thread: " + ex);
        }
   }

    public static GlobalErrorMessages parseReqType(String request) {
        StringBuilder sb = new StringBuilder();

        if(request.length() < 6){
            return GlobalErrorMessages.INVALID_CMD_LENGTH;
        }
        else if(request.charAt(5) != ' '){
            return GlobalErrorMessages.INVALID_CMD_FORMAT;
        }
        else if(!request.contains("LOGON") || !request.contains("MSGUP")
                || !request.contains("MSGDL") || !request.contains("LGOFF")){
            return GlobalErrorMessages.INVALID_CMD_TYPE;
        }
        else{
            return GlobalErrorMessages.NO_ERR;
        }
    }

    public static String getErrType(GlobalErrorMessages err){
        if(err == GlobalErrorMessages.INVALID_CMD_LENGTH){
            return "ERR07 - COMMAND TOO SHORT";
        }
        else if(err == GlobalErrorMessages.INVALID_CMD_FORMAT){
            return "ERR08 - INVALID FORMAT";
        }
        else{
            return "UNIDENTIFIED ERROR";
        }
    }

    public static String logon(String username, String password){
       user = new UserSession(username, password);
       return "";
    }

    public static String logoff(){
       return "";
    }

    public static String uploadMessage(String message){
       user.messages.add(message);
       return "";
    }

    public static ArrayList<String> downloadMessages(){
         return user.messages;
    }
}
