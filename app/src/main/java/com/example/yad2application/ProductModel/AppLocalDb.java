package com.example.yad2application.ProductModel;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.yad2application.MyApplication;

@Database(entities = {Product.class}, version = 4)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract ProductDao productDao();
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