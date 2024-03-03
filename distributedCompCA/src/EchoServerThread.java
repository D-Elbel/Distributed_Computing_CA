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
                     System.out.println("No error");
                     myDataSocket.sendMessage(getCommand(message));
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

        if(request.length() < 6){
            System.out.println("Invalid command length");
            return GlobalErrorMessages.INVALID_CMD_LENGTH;

        }
        else if(request.charAt(5) != ' '){
            System.out.println("Invalid command format");
            return GlobalErrorMessages.INVALID_CMD_FORMAT;
        }
        else if(!request.contains("LOGON") && !request.contains("MSGUP")
                && !request.contains("MSGDL") && !request.contains("LGOFF")){
            System.out.println(!request.contains("LOGON"));
            System.out.println("Invalid command type");
            return GlobalErrorMessages.INVALID_CMD_TYPE;
        }
        else{
            System.out.println("No error");
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

    public static String getCommand(String request){


       switch (request.substring(0, 5)){
           case "LOGON":
               return logon("username", "password");
           case "LGOFF":
               return logoff();
           case "MSGUP":
               return uploadMessage(request.substring(6, request.length()));
           case "MSGDL":
               return downloadMessages();
       }

       return "default";
    }

    public static String logon(String username, String password){
       user = new UserSession(username, password);
       return (username + " has logged on. Welcome! \n" +
               "Please Upload a message or Download messages, or Log off");
    }

    public static String logoff(){
       return "";
    }

    public static String uploadMessage(String message){
       user.messages.add(message);
       return "Message uploaded successfully";
    }

    public static String downloadMessages(){
       StringBuilder sb = new StringBuilder();

       //iterate through the messages and append them to the stringbuilder, use a regular for
         //loop to get the index of the message
         for(int i = 0; i < user.messages.size(); i++){
              sb.append(user.messages.get(i));
              sb.append(" ");
             System.out.println(user.messages.get(i));
            }

       return sb.toString();
    }
}
