package com.example.yad2application.ProductModel;

import android.graphics.Bitmap;
import android.graphics.Movie;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductModel {
    private static final ProductModel _instance = new ProductModel();

    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = HandlerCompat.createAsync(Looper.getMainLooper());
    private ProductFirebaseModel firebaseModel = new ProductFirebaseModel();
    AppLocalDbRepository localDb = AppLocalDb.getAppDb();

    public static ProductModel instance(){
        return _instance;
    }
    private ProductModel(){

    }


    public interface Listener<T>{
        void onComplete(T data);
    }


    public enum LoadingState{
        LOADING,
        NOT_LOADING
    }
    final public MutableLiveData<LoadingState> EventStudentsListLoadingState = new MutableLiveData<LoadingState>(LoadingState.NOT_LOADING);

    private LiveData<List<Product>> productsList;
    public LiveData<List<Product>> getAllProducts() {
        if(productsList == null){
            productsList = localDb.productDao().getAll();
            refreshAllProducts();
        }
        return productsList;
    }
    public LiveData<List<Product>> getAllProductsOwner(String email) {
        if(productsList == null){
            productsList = localDb.productDao().getAllByOwnerEmail(email);
            refreshAllProducts();
        }
        return productsList;
    }

    public LiveData<List<Product>> getAllProductsCustomer(String email) {
        if(productsList == null){
            productsList = localDb.productDao().getAllAsCustomerEmail(email);
            refreshAllProducts();
        }
        return productsList;
    }

    public Product getProductByName(String name){
        Product product = localDb.productDao().getProductByName(name);
        return product;
    }

    public void refreshAllProducts(){
        EventStudentsListLoadingState.setValue(LoadingState.LOADING);
        // get local last update
        Long localLastUpdate = Product.getLocalLastUpdate();
        // get all updated recorde from firebase since local last update
        firebaseModel.getAllProductsSince(localLastUpdate,list->{
            executor.execute(()->{
                Log.d("TAG", " firebase return : " + list.size());
                Long time = localLastUpdate;
                for(Product prod:list){
                    // insert new records into ROOM
                    localDb.productDao().insertAll(prod);
                    if (time < prod.getLastUpdated()){
                        time = prod.getLastUpdated();
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // update local last update
                Product.setLocalLastUpdate(time);
                EventStudentsListLoadingState.postValue(LoadingState.NOT_LOADING);
            });
        });
    }

    public void addProduct(Product prod, Listener<Void> listener){
        firebaseModel.addProduct(prod,(Void)->{
            refreshAllProducts();
            listener.onComplete(null);
        });
    }


    public void uploadImage(String name, Bitmap bitmap, Listener<String> listener) {
        firebaseModel.uploadImage(name,bitmap,listener);
    }


    public FirebaseUser getCurrentUser(){
        return firebaseModel.getCurrentUser();
    }
}
