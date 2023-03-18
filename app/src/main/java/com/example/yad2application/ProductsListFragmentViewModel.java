package com.example.yad2application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.yad2application.Model.Model;
import com.example.yad2application.Model.Product;

import java.util.List;

public class ProductsListFragmentViewModel extends ViewModel {

    private LiveData<List<Product>> data = Model.instance().getAllProducts();

    LiveData<List<Product>> getData(){
        return data;
    }

}
