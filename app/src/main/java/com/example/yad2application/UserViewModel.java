package com.example.yad2application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.yad2application.Model.Model;
import com.example.yad2application.Model.Product;
import com.example.yad2application.Model.User;

import java.util.List;

public class UserViewModel extends ViewModel {

        private LiveData<User> user = Model.instance().getUser();

        public LiveData<User> getCurrentUser(){
            return user;
        }


}
