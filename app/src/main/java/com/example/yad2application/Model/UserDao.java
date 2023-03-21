package com.example.yad2application.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("select * from User")
    LiveData<List<User>> getAll();

    @Query("SELECT * FROM User LIMIT 1")
    LiveData<User> getUser();

    @Query("select * from User where email = :email")
    User getUserByEmail(String email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(User... users);

    @Query("UPDATE User SET email=:email")
    void updateUserEmail(String email);

    @Query("UPDATE User SET avatarUrl=:avatarUrl")
    void updateUserAvatar(String avatarUrl);

    @Delete
    void delete(User user);

    @Query("DELETE FROM User")
    void deleteAll();


}

