package com.example.yad2application.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProductDao {

//    @Query("select * from Product WHERE ownerEmail != :email AND customerEmail IS NULL")
//    LiveData<List<Product>> getAll(String email);


    @Query("select * from Product")
    LiveData<List<Product>> getAll();

    @Query("SELECT * FROM Product WHERE productId = :productId")
    Product getProductById(String productId);

    @Query("SELECT * FROM Product WHERE ownerEmail = :email")
    LiveData<List<Product>> getAllByOwnerEmail(String email);

    @Query("SELECT * FROM Product WHERE customerEmail = :email")
    LiveData<List<Product>> getAllAsCustomerEmail(String email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Product... products);

    @Query("UPDATE Product SET customerEmail = :newEmail WHERE productId = :productId")
    void order(String productId,String newEmail);

    @Query("UPDATE Product SET name = :name AND price = :price AND price = :price AND description = :description WHERE productId = :productId")
    void updateProduct(String productId,String name, String price,String description);


    @Query("UPDATE Product SET ownerEmail = :newEmail, customerEmail = :newEmail WHERE ownerEmail = :oldEmail OR customerEmail = :oldEmail")
    void updateProductAfterUserChangedEmail(String oldEmail, String newEmail);



    @Delete
    void delete(Product product);


}

