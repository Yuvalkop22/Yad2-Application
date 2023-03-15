package com.example.yad2application.ProductModel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.yad2application.Model.Student;
import com.example.yad2application.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Product {
    @PrimaryKey
    @NonNull
    public String name="";
    public String avatarUrl="";
    public String category="";
    public String price="";
    public String description ="";
    public String ownerEmail = "";
    public String customerEmail = "";
    public Boolean cb=false;
    public Long lastUpdated;

    public Product(){
    }

    public Product(@NonNull String name, String avatarUrl, String category, String price, String description, String ownerEmail, String customerEmail, Boolean cb) {
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.category = category;
        this.price = price;
        this.description = description;
        this.ownerEmail = ownerEmail;
        this.customerEmail = customerEmail;
        this.cb = cb;
    }

    static final String NAME = "name";
    static final String CATEGORY = "category";
    static final String DESCRIPTION = "description";
    static final String PRICE = "price";
    static final String AVATAR = "avatar";
    static final String OWNEREMAIL = "owneremail";
    static final String CUSTOMEREMAIL = "customeremail";
    static final String CB = "cb";
    static final String COLLECTION = "Products";
    static final String LAST_UPDATED = "lastUpdated";
    static final String LOCAL_LAST_UPDATED = "products_local_last_update";

    public static Product fromJson(Map<String,Object> json){
        String name = (String)json.get(NAME);
        String category = (String)json.get(CATEGORY);
        String price= (String) json.get(PRICE);
        String description = (String) json.get(DESCRIPTION);
        String avatar = (String)json.get(AVATAR);
        Boolean cb = (Boolean) json.get(CB);
        String owneremail = (String)json.get(OWNEREMAIL);
        String customeremail = (String) json.get(CUSTOMEREMAIL);
        Product product = new Product(name,avatar,category,price,description,owneremail,customeremail,cb);
        try{
            Timestamp time = (Timestamp) json.get(LAST_UPDATED);
            product.setLastUpdated(time.getSeconds());
        }catch(Exception e){

        }
        return product;
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
        json.put(NAME, getName());
        json.put(CATEGORY,getCategory());
        json.put(PRICE,getPrice());
        json.put(DESCRIPTION,getDescription());
        json.put(AVATAR, getAvatarUrl());
        json.put(CB, getCb());
        json.put(OWNEREMAIL,getOwnerEmail());
        json.put(CUSTOMEREMAIL,getCustomerEmail());
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        return json;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    private String getPrice() {
        return this.price;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setCb(Boolean cb) {
        this.cb = cb;
    }

    public String getCategory() {
        return category;
    }


    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Boolean getCb() {
        return cb;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
