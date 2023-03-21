package com.example.yad2application.Model;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Model {
    private static final Model _instance = new Model();

    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = HandlerCompat.createAsync(Looper.getMainLooper());
    private FirebaseModel firebaseModel = new FirebaseModel();
    AppLocalDbRepository localDb = AppLocalDb.getAppDb();
    private LiveData<User> user;

    public static Model instance(){
        return _instance;
    }
    private Model(){
    }

    public interface Listener<T>{
        void onComplete(T data);
    }



    public enum LoadingState{
        LOADING,
        NOT_LOADING
    }
    final public MutableLiveData<LoadingState> EventUsersListLoadingState = new MutableLiveData<LoadingState>(LoadingState.NOT_LOADING);
    final public MutableLiveData<LoadingState> EventProductsListLoadingState = new MutableLiveData<LoadingState>(LoadingState.NOT_LOADING);



    private LiveData<List<User>> usersList;
    public LiveData<List<User>> getAllUsers() {
        if(usersList == null){
            usersList = localDb.userDao().getAll();
            refreshAllUsers();
        }
        return usersList;
    }

    public void refreshAllUsers(){
        EventUsersListLoadingState.setValue(LoadingState.LOADING);
        // get local last update
        Long localLastUpdate = User.getLocalLastUpdate();
        // get all updated recorde from firebase since local last update
        firebaseModel.getAllUsersSince(localLastUpdate,list->{
            executor.execute(()->{
                Log.d("TAG", " firebase return : " + list.size());
                Long time = localLastUpdate;
                for(User user:list){
                    // insert new records into ROOM
                    localDb.userDao().insertAll(user);
                    if (time < user.getLastUpdated()){
                        time = user.getLastUpdated();
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // update local last update
                User.setLocalLastUpdate(time);
                EventUsersListLoadingState.postValue(LoadingState.NOT_LOADING);
            });
        });
    }

    public void addUser(User user, Listener<User> listener){
        firebaseModel.signUpUserFirebase(user.getEmail(),user.getPassword(),(FirebaseUser)->{
            if (FirebaseUser != null){
                firebaseModel.addUserToFirebase(user,(unused)->{
                    executor.execute(()->{
                        localDb.userDao().deleteAll();
                        localDb.userDao().insertAll(user);
                    });
                    listener.onComplete(user);
                });
            }else{
                listener.onComplete(null);
            }
        });
    }
    public LiveData<User> getUser(){
        if(user == null){
            user = localDb.userDao().getUser();
        }
        return user;
    }
    public void signInUser(String email,String password, Listener<User>listener){
        firebaseModel.signInUser(email,password, (FireBaseUser) -> {
            if (FireBaseUser != null)
            {
                firebaseModel.getUser(FireBaseUser.getEmail(), (User) -> {
                    Log.d("TAG", "user found in Model");
                    executor.execute(()->{
                        localDb.userDao().deleteAll();
                        localDb.userDao().insertAll(User);

                    });

                    listener.onComplete(User);
                });
            }
            else {
                listener.onComplete(null);
            }

        });

    }

    private LiveData<List<Product>> productsList;
    public LiveData<List<Product>> getAllProducts() {
        if(productsList == null){
            productsList = localDb.productDao().getAll();
            refreshAllProducts();
        }
        if (productsList != null){
            productsList = null;
            productsList =localDb.productDao().getAll();
            refreshAllProducts();
        }
        return productsList;
    }
    public LiveData<List<Product>> getAllProductsOwner(String email) {
        if(productsList == null){
            productsList = localDb.productDao().getAllByOwnerEmail(email);
            refreshAllProductsOwner();
        }
        if (productsList != null){
            productsList = null;
            productsList =localDb.productDao().getAllByOwnerEmail(email);
            refreshAllProductsOwner();
        }
        return productsList;
    }

    public LiveData<List<Product>> getAllProductsCustomer(String email) {
        if(productsList == null){
            productsList = localDb.productDao().getAllAsCustomerEmail(email);
            refreshAllProductsCustomer();
        }
        if (productsList != null){
            productsList = null;
            productsList =localDb.productDao().getAllAsCustomerEmail(email);
            refreshAllProductsCustomer();
        }
        return productsList;
    }


    public void refreshAllProductsOwner(){
        EventProductsListLoadingState.setValue(LoadingState.LOADING);
        // get local last update
        Long localLastUpdate = Product.getLocalLastUpdate();
        // get all updated recorde from firebase since local last update
        firebaseModel.getAllProductsOwnerSince(localLastUpdate,list->{
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
                EventProductsListLoadingState.postValue(LoadingState.NOT_LOADING);
            });
        });
    }

    public void refreshAllProductsCustomer(){
        //EventStudentsListLoadingState.setValue(LoadingState.LOADING);
        // get local last update
        Long localLastUpdate = Product.getLocalLastUpdate();
        // get all updated recorde from firebase since local last update
        firebaseModel.getAllProductsCustomerSince(localLastUpdate,list->{
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
                EventProductsListLoadingState.postValue(LoadingState.NOT_LOADING);
            });
        });
    }


    public void refreshAllProducts(){
        EventProductsListLoadingState.setValue(LoadingState.LOADING);
        // get local last update
        Long localLastUpdate = Product.getLocalLastUpdate();
        // get all updated recorde from firebase since local last update
        firebaseModel.getAllProductsSince(localLastUpdate,list->{
            executor.execute(()->{
                Log.d("TAG", " firebase return : " + list.size());
                Long time = localLastUpdate;
                for(Product st:list){
                    // insert new records into ROOM
                    localDb.productDao().insertAll(st);
                    if (time < st.getLastUpdated()){
                        time = st.getLastUpdated();
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // update local last update
                Product.setLocalLastUpdate(time);
                EventProductsListLoadingState.postValue(LoadingState.NOT_LOADING);
            });
        });
    }

    public void addProduct(Product prod, Listener<Void> listener){
        firebaseModel.addProduct(prod,(Void)->{
            refreshAllProducts();
            listener.onComplete(null);
        });
    }

    public void deleteProduct(Product product, Listener<Void> listener) {
        firebaseModel.deleteProduct(product, new Listener<Void>() {
            @Override
            public void onComplete(Void data) {
                executor.execute(()->{
                    localDb.productDao().delete(product);
                    listener.onComplete(null);
                });
            }
        });
    }

    public void updateProduct(String productId,String name, String price,String description,Model.Listener<Void> listener){
        firebaseModel.updateProduct(productId, name, price, description, new Listener<Void>() {
            @Override
            public void onComplete(Void data) {
                executor.execute(()->{
                    localDb.productDao().updateProduct(productId,name,price,description);
                    listener.onComplete(null);
                });
            }
        });
    }
    public void updateUserEmail(String oldEmail,String email,String password,Model.Listener<Boolean> listener){
        firebaseModel.editUserFirebase(email,password,(FirebaseUser)->{
            if (FirebaseUser != null) {
                firebaseModel.editEmailFromProducts(oldEmail, email, new Listener<Void>() {
                    @Override
                    public void onComplete(Void data) {
                        executor.execute(() -> {
                            localDb.productDao().updateProductAfterUserChangedEmail(oldEmail, email);
                        });
                        firebaseModel.editUserDocument(oldEmail, email, new Listener<Void>() {
                            @Override
                            public void onComplete(Void data) {
                                executor.execute(() -> {
                                    localDb.userDao().updateProductEmail(email);
                                });
                            }
                        });
                        listener.onComplete(true);
                    }
                });
            }else{
                listener.onComplete(false);
            }
        });
    }


        public void order(Product product, String newEmail, Listener<Void> listener) {
        firebaseModel.order(product,newEmail, new Listener<Void>() {
            @Override
            public void onComplete(Void data) {
                executor.execute(()->{
                    localDb.productDao().order(product.getProductId(),newEmail);
                    onComplete(data);
                });
            }
        });
    }

    public void signOut(){
        executor.execute(()->{
            localDb.userDao().deleteAll();
            user = null;
        });
    }

    public FirebaseUser getCurrentUser(){
        return firebaseModel.getCurrentUser();
    }




        public void uploadImageUser(String name, Bitmap bitmap, Listener<String> listener) {
        firebaseModel.uploadImageUser(name,bitmap,listener);
    }

    public void uploadImageProduct(String name, Bitmap bitmap, Listener<String> listener) {
        firebaseModel.uploadImageProduct(name,bitmap,listener);
    }

}
