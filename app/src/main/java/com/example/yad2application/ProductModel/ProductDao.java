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

    @Query("select * from Product WHERE ownerEmail != :email AND customerEmail IS NULL")
    LiveData<List<Product>> getAll(String email);

    @Query("SELECT * FROM Product WHERE name = :name")
    Product getProductByName(String name);

    @Query("SELECT * FROM Product WHERE ownerEmail = :email")
    LiveData<List<Product>> getAllByOwnerEmail(String email);

    @Query("SELECT * FROM Product WHERE customerEmail = :email")
    LiveData<List<Product>> getAllAsCustomerEmail(String email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Product... products);

    @Query("UPDATE Product SET customerEmail = :newEmail WHERE customerEmail IS NULL AND name = :pName")
    void order(String pName, String newEmail);



    @Delete
    void delete(Product product);


}

