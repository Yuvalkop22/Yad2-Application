package com.example.yad2application.Model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.yad2application.MyApplication;
import com.example.yad2application.ProductModel.Product;
import com.example.yad2application.ProductModel.ProductDao;

@Database(entities = {User.class}, version = 21)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract UserDao userDao();
}
public class AppLocalDb{
    static public AppLocalDbRepository getAppDb(){
        return Room.databaseBuilder(MyApplication.getMyContext(),
                        AppLocalDbRepository.class,
                        "dbFileName.db")
                .fallbackToDestructiveMigration()
                .build();
    }
    private AppLocalDb(){}
}