package com.example.yad2application.Model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.yad2application.MyApplication;
import com.example.yad2application.ProductModel.Product;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    public String email="";
    public String password = "";
    public String avatarUrl="";
    public Long lastUpdated;

    public User(){
    }

    public User(@NonNull String email, String password, String avatarUrl) {
        this.email = email;
        this.password = password;
        this.avatarUrl = avatarUrl;
    }

    static final String EMAIL = "email";
    static final String PASSWORD = "password";
    static final String AVATAR = "avatar";
    static final String COLLECTION = "users";
    static final String LAST_UPDATED = "lastUpdated";
    static final String LOCAL_LAST_UPDATED = "students_local_last_update";

    public static User fromJson(Map<String,Object> json){
        String email = (String)json.get(EMAIL);
        String password = (String) json.get(PASSWORD);
        String avatar = (String)json.get(AVATAR);

        User st = new User(email,password,avatar);
        try{
            Timestamp time = (Timestamp) json.get(LAST_UPDATED);
            st.setLastUpdated(time.getSeconds());
        }catch(Exception e){

        }
        return st;
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
        json.put(EMAIL, getEmail());
        json.put(PASSWORD,getPassword());
        json.put(AVATAR, getAvatarUrl());
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        return json;
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
}
