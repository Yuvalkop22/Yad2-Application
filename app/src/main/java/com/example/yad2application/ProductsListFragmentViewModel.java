package com.example.yad2application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.yad2application.ProductModel.Product;
import com.example.yad2application.ProductModel.ProductModel;

import java.util.List;

public class ProductsListFragmentViewModel extends ViewModel {
    private LiveData<List<Product>> data = ProductModel.instance().getAllProducts();

    LiveData<List<Product>> getData(){
        return data;
    }
}
