package Client;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String id;
    private String password;
    private String nickname;

    public User(String name, String id, String password, String email){
        this.name = name;
        this.id = id;
        this.password = password;
        this.nickname = email;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }

    public String getPassword(){
        return password;
    }

    public String getNickname(){
        return nickname;
    }
}
