package com.example.yad2application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.yad2application.Model.Model;
import com.example.yad2application.Model.Product;

import java.util.List;

public class ProductsListCustomerFragmentViewModel extends ViewModel {


    private LiveData<List<Product>> dataAsCustomer = Model.instance().getAllProductsCustomer(Model.instance().getCurrentUser().getEmail());


    LiveData<List<Product>> getDataAsCustomer(){
        return dataAsCustomer;
    }


}
