package com.example.yad2application.ProductModel;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("select * from Product")
    LiveData<List<com.example.yad2application.ProductModel.Product>> getAll();

    @Query("select * from product where name = :pName")
    com.example.yad2application.ProductModel.Product getStudentById(String pName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(com.example.yad2application.ProductModel.Product... products);

    @Delete
    void delete(com.example.yad2application.ProductModel.Product product);


}

