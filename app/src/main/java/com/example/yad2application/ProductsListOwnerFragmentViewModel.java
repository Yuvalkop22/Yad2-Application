package com.example.yad2application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.yad2application.Model.Model;
import com.example.yad2application.Model.Product;

import java.util.List;

public class ProductsListOwnerFragmentViewModel extends ViewModel {

    private LiveData<List<Product>> dataOwner = Model.instance().getAllProductsOwner(Model.instance().getCurrentUser().getEmail());


    LiveData<List<Product>> getDataOwner(){
        return dataOwner;
    }


}
