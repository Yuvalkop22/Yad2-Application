package com.example.yad2application.ProductModel;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.yad2application.ProductModel.Product;
import java.util.List;

@Dao
public interface ProductDao {
    @Query("select * from Product")
    List<Product> getAll();

    @Query("select * from product where name = :pName")
    Product getStudentById(String pName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Product... products);

    @Delete
    void delete(Product product);


}

