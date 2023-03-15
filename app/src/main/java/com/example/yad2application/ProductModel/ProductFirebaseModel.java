package com.example.yad2application.ProductModel;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

public class ProductFirebaseModel {
    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    public void getAllProductsSince(Long since, ProductModel.Listener<List<Product>> callback){
        db  = FirebaseFirestore.getInstance();
        db.collection(Product.COLLECTION)
                .whereGreaterThanOrEqualTo(Product.LAST_UPDATED, new Timestamp(since,0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Product> list = new LinkedList<>();
                        if (task.isSuccessful()){
                            QuerySnapshot jsonsList = task.getResult();
                            for (DocumentSnapshot json: jsonsList){
                                Product st = Product.fromJson(json.getData());
                                list.add(st);
                            }
                        }
                        callback.onComplete(list);
                    }
                });
    }

//    public void getAllProductSearch(String search, ProductModel.Listener<List<Product>> callback){
//        db  = FirebaseFirestore.getInstance();
//        db.collection(Product.COLLECTION)
//                .whereEqualTo(Product.NAME, search)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        List<Product> list = new LinkedList<>();
//                        if (task.isSuccessful()){
//                            QuerySnapshot jsonsList = task.getResult();
//                            for (DocumentSnapshot json: jsonsList){
//                                Product st = Product.fromJson(json.getData());
//                                list.add(st);
//                            }
//                        }
//                        callback.onComplete(list);
//                    }
//                });
//    }

    public void addProduct(Product pro, ProductModel.Listener<Void> listener) {
//    db = FirebaseFirestore.getInstance();

        db = FirebaseFirestore.getInstance();
        db.collection(Product.COLLECTION).document(pro.getName()).set(pro.toJson())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        listener.onComplete(null);
                    }
                });

    }

    public void uploadImage(String name, Bitmap bitmap, ProductModel.Listener<String> listener){
        storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/products/" + name + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Start the upload task
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle the failure case
                Log.e("uploadImage", "Upload failed: " + exception.getMessage());
                listener.onComplete(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL for the uploaded image
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Handle the success case
                        Log.d("uploadImage", "Upload succeeded: " + uri.toString());
                        listener.onComplete(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle the failure case
                        Log.e("uploadImage", "Failed to get download URL: " + exception.getMessage());
                        listener.onComplete(null);
                    }
                });
            }
        });
    }
}
