package com.example.yad2application.Model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.yad2application.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
public class User implements Serializable {
    @PrimaryKey
    @NonNull
    public String userId="";
    public String email="";
    public String password = "";
    public String avatarUrl="";
    public Long lastUpdated;

    public User(){
    }

    public User(String email, String password, String avatarUrl) {
        this.email = email;
        this.password = password;
        this.avatarUrl = avatarUrl;
        this.userId = UUID.randomUUID().toString();
    }

    public User(@NonNull String userId, String email, String password, String avatarUrl) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.avatarUrl = avatarUrl;
    }
    static final String USERID = "userid";
    static final String EMAIL = "email";
    static final String PASSWORD = "password";
    static final String AVATAR = "avatar";
    static final String COLLECTION = "users";
    static final String LAST_UPDATED = "lastUpdated";
    static final String LOCAL_LAST_UPDATED = "students_local_last_update";

    public static User fromJson(Map<String,Object> json){
        String userid = (String)json.get(USERID);
        String email = (String)json.get(EMAIL);
        String password = (String) json.get(PASSWORD);
        String avatar = (String)json.get(AVATAR);
        User user = new User(userid,email,password,avatar);
        try{
            Timestamp time = (Timestamp) json.get(LAST_UPDATED);
            user.setLastUpdated(time.getSeconds());
        }catch(Exception e){

        }
        return user;
    }

    public static Long getLocalLastUpdate() {
        SharedPreferences sharedPref = MyApplication.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        return sharedPref.getLong(LOCAL_LAST_UPDATED, 0);
    }

    public static void setLocalLastUpdate(Long time) {
        SharedPreferences sharedPref = MyApplication.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(LOCAL_LAST_UPDATED,time);
        editor.commit();
    }

    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put(USERID,getUserId());
        json.put(EMAIL, getEmail());
        json.put(PASSWORD,getPassword());
        json.put(AVATAR, getAvatarUrl());
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        return json;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }


    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }


    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
