package server;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/**
 * This module is to be used with a concurrent Echo server.
 * Its run method carries out the logic of a client session.
 * @author M. L. Liu
 */

class SMPServerThread implements Runnable {
   static final String endMessage = "LGOFF";
   MyStreamSocket myDataSocket;
   boolean isLoggedIn;
   UserSession user;

   SMPServerThread(MyStreamSocket myDataSocket) {
      this.myDataSocket = myDataSocket;
      this.isLoggedIn = false;
      this.user = null;
   }

    public void setUser(UserSession user) {
        this.user = user;
    }

    public void run( ) {
      boolean done = false;
      String message;

      SSLSession sslSession = ((SSLSocket) myDataSocket.getSocket()).getSession();
      System.out.println("Cipher suite used: " + sslSession.getCipherSuite() + " Protocol used: " + sslSession.getProtocol());

      try {
         while (!done) {
             message = myDataSocket.receiveMessage( );
             System.out.println("message received: "+ message);
             if (message.equals(endMessage)){
                 System.out.println("Session over.");
                myDataSocket.sendMessage(logoff());
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

        if(request.length() < 5){
            System.out.println("Invalid command length");
            return GlobalErrorMessages.E007_INVALID_LENGTH;

        }

        else if(!request.contains("LOGON") && !request.contains("MSGUP") && !request.contains("MSGDL")
                && !request.contains("LGOFF") && !request.contains("SPMSG")){
            return GlobalErrorMessages.INVALID_CMD_TYPE;
        }
        else{
            System.out.println("No error");
            return GlobalErrorMessages.NO_ERR;
        }
    }

    public static String getErrType(GlobalErrorMessages err){

        if(err == GlobalErrorMessages.E007_INVALID_LENGTH){
            return "ERR07 - INVALID COMMAND LENGTH";
        }
        if(err == GlobalErrorMessages.E008_INVALID_MSG_TYPE){
            return "ERR08 - INVALID COMMAND TYPE";
        }

       if(err == GlobalErrorMessages.E001_INVALID_AUTH){
           return "ERR01 - INVALID AUTH FORMAT";
       }

       if(err == GlobalErrorMessages.E003_INVALID_MSG_FORMAT) {
           return "ERR03 - INVALID MESSAGE FORMAT";
       }

        if(err == GlobalErrorMessages.E004_NO_MSG) {
            return "ERR04 - NO MESSAGE";
        }

        if(err == GlobalErrorMessages.E005_NO_MSGS) {
            return "ERR05 - NO MESSAGES STORED";
        }

        if(err == GlobalErrorMessages.E006_NO_MSGS_W_ID) {
            return "ERR06 - NO MESSAGES WITH PROVIDED ID";
        }

        return "UNIDENTIFIED ERROR";

    }

    public String getCommand(String request){


       switch (request.substring(0, 5)){
           case "LOGON":
               System.out.println(user);
               if (user != null){
                   return "USER ALREADY LOGGED IN";
               }else{
                   return logon("username", "password");
               }
           case "LGOFF":
               if (user == null){
                   return "USER NOT LOGGED IN";
               }
               else{
                     return logoff();
               }
           case "MSGUP":
               if (user == null){
                   return "USER NOT LOGGED IN";
               }
               else{
                   return uploadMessage(request.substring(6, request.length()));
               }
           case "MSGDL":
               if (user == null){
                   return "USER NOT LOGGED IN";
               }
               else{
                     return downloadMessages();
               }
           case "SPMSG":
               if (user == null){
                   return "USER NOT LOGGED IN";
               }
               else{
                   return downloadSpecificMessage(request.substring(6, request.length()));
               }
       }

       return "default";
    }

    public String logon(String username, String password){
       if (username.length() + password.length() + 7 > 519){
           return getErrType(GlobalErrorMessages.E001_INVALID_AUTH);
       }

       //Not implemented for simplicity, username/password validation would be done here

       user = new UserSession(username, password);
       return "USER AUTHENTICATED";
    }

    public  String logoff(){

       user = null;
       return "You have been logged out!";
    }

    public String uploadMessage(String message){

       if(message.length() < 1){
           return getErrType(GlobalErrorMessages.E004_NO_MSG);
       }

       if(message.length() + 6 > 1024){
           return getErrType(GlobalErrorMessages.E003_INVALID_MSG_FORMAT);
       }


       user.messages.add(message);
       return "Message uploaded successfully";
    }

    public String downloadMessages(){
       if(user.messages.isEmpty()){
           return getErrType(GlobalErrorMessages.E005_NO_MSGS);
       }

       StringBuilder sb = new StringBuilder();

       for(int i = 0; i < user.messages.size(); i++){
           sb.append("MSG-" + i + " ");
           sb.append(user.messages.get(i));
           sb.append(";");
           System.out.println(user.messages.get(i));
       }

       return sb.toString();
    }

    public String downloadSpecificMessage(String messageID){

       if(user.messages.isEmpty()){
                return getErrType(GlobalErrorMessages.E005_NO_MSGS);
       }

       if(Integer.parseInt(messageID.replace(" ", "")) > user.messages.size()){
           return getErrType(GlobalErrorMessages.E006_NO_MSGS_W_ID);
         }
        System.out.println(messageID);
        return user.messages.get(Integer.parseInt(messageID.replace(" ", "")));
    }
}
