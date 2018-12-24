
package com.minhnhut.library.DataObj;

import com.google.firebase.database.IgnoreExtraProperties;
@IgnoreExtraProperties
public class User {
    public String id;
    public String username;
    public String email;
    public String password;
    public String avatar;
    public int level;

    public User() {}

    public User(String id, String username, String email, String password, String avatar,int level) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.level = level;
    }


}
