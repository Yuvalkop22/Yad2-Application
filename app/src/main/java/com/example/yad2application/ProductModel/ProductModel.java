package com.example.yad2application.ProductModel;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProductModel {
    private static final ProductModel _instance = new ProductModel();

    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = HandlerCompat.createAsync(Looper.getMainLooper());
    private ProductFirebaseModel firebaseModel = new ProductFirebaseModel();
    //AppLocalDbRepository localDb = AppLocalDb.getAppDb();

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


    private LiveData<List<Product>> productList;
    public LiveData<List<Product>> getAllProducts() {
        if(productList == null){
            //studentList = localDb.studentDao().getAll();
    //        refreshAllStudents();
        }
        return productList;
    }


    public void addProduct(Product prod, Listener<Void> listener){
        firebaseModel.addProduct(prod,(Void)->{
            //refreshAllStudents();
            listener.onComplete(null);
        });
    }



    public void uploadImage(String name, Bitmap bitmap, Listener<String> listener) {
        firebaseModel.uploadImage(name,bitmap,listener);
    }

}
