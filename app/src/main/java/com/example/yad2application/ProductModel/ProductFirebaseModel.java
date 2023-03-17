package com.example.yad2application.ProductModel;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.util.Listener;
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
    Product product;

    public FirebaseUser getCurrentUser(){
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        return firebaseUser;
    }

    public void getProductByName(String name, ProductModel.Listener<Product> callback) {
        db = FirebaseFirestore.getInstance();
        db.collection(Product.COLLECTION)
                .whereEqualTo(Product.NAME, name)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().size() > 0) {
                            DocumentSnapshot json = task.getResult().getDocuments().get(0);
                            Product product = Product.fromJson(json.getData());
                            callback.onComplete(product);
                        } else {
                            callback.onComplete(null);
                        }
                    }
                });
    }
    public void getAllProductsSince(Long since, ProductModel.Listener<List<Product>> callback){
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection(Product.COLLECTION);

        // Query 1: Get all documents where the "lastUpdated" field is greater than the provided timestamp
        Query query1 = collectionRef.whereGreaterThanOrEqualTo(Product.LAST_UPDATED, new Timestamp(since,0));

        // Query 2: Get all documents where the "owneremail" field is not equal to the current user's email
        Query query2 = collectionRef.whereNotEqualTo(Product.OWNEREMAIL,getCurrentUser().getEmail());


        // Merge the results of both queries
        Tasks.whenAllComplete(query1.get(), query2.get())
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> task) {
                        List<Product> list = new LinkedList<>();
                        if (task.isSuccessful()){
                            for (Task<?> subTask : task.getResult()) {
                                if (subTask.isSuccessful()) {
                                    QuerySnapshot querySnapshot = (QuerySnapshot) subTask.getResult();
                                    for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                        Product st = Product.fromJson(documentSnapshot.getData());
                                        list.add(st);
                                    }
                                }
                            }
                        }
                        callback.onComplete(list);
                    }
                });
    }

    public void getAllProductsOwnerSince(Long since, ProductModel.Listener<List<Product>> callback){
        db  = FirebaseFirestore.getInstance();
        db.collection(Product.COLLECTION)
                .whereGreaterThanOrEqualTo(Product.LAST_UPDATED, new Timestamp(since,0))
                .whereEqualTo(Product.OWNEREMAIL,getCurrentUser().getEmail())
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


    public void getAllProductsCustomerSince(Long since, ProductModel.Listener<List<Product>> callback){
        db  = FirebaseFirestore.getInstance();
        db.collection(Product.COLLECTION)
                .whereGreaterThanOrEqualTo(Product.LAST_UPDATED, new Timestamp(since,0))
                .whereEqualTo(Product.CUSTOMEREMAIL,getCurrentUser().getEmail())
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



    public void addProduct(Product pro, ProductModel.Listener<Void> listener) {
        db = FirebaseFirestore.getInstance();
        db.collection(Product.COLLECTION).document(pro.getName()).set(pro.toJson())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        listener.onComplete(null);
                    }
                });
    }

    public void order(String name, String newEmail, ProductModel.Listener<Void> listener) {
        db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection(Product.COLLECTION);

        productsRef.whereEqualTo(Product.NAME, name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WriteBatch batch = db.batch();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            batch.update(doc.getReference(), Product.CUSTOMEREMAIL, newEmail);
                        }
                        batch.commit()
                                .addOnCompleteListener(commitTask -> {
                                    if (commitTask.isSuccessful()) {
                                        listener.onComplete(null);
                                    } else {
                                        Log.e("TAG", "Error updating documents: ", commitTask.getException());
                                    }
                                });
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }




    public void deleteProduct(Product product, OnCompleteListener<Void> listener) {
        db = FirebaseFirestore.getInstance();
        db.collection(Product.COLLECTION).document(product.getName())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onComplete(null);
                            Log.d("TAG", "Product with ID " + product.getName() + " deleted successfully.");
                        } else {
                            Log.w("TAG", "Error deleting product with ID " + product.getName(), task.getException());
                        }
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
