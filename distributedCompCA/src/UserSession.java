import java.util.ArrayList;

public class UserSession {

    String username;
    String password;
    //an arraylist of strings
    ArrayList<String> messages;

    public UserSession(String username, String password) {
        this.username = username;
    }
}
