package com.example.yad2application.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;

import com.example.yad2application.Model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FirebaseModel {
    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseModel(){

    }

    public FirebaseUser getUser(){
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        return firebaseUser;
    }

    public void getAllUsersSince(Long since, Model.Listener<List<User>> callback){
        db.collection(User.COLLECTION)
                .whereGreaterThanOrEqualTo(User.LAST_UPDATED, new Timestamp(since,0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> list = new LinkedList<>();
                        if (task.isSuccessful()){
                            QuerySnapshot jsonsList = task.getResult();
                            for (DocumentSnapshot json: jsonsList){
                                User st = User.fromJson(json.getData());
                                list.add(st);
                            }
                        }
                        callback.onComplete(list);
                    }
                });
    }

    public void signInUser(User user, Model.Listener<Void> listener) {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        auth.signInWithEmailAndPassword(user.getEmail(),user.getPassword()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                listener.onComplete(null);
            }
        });
    }

    public void addUser(User user, Model.Listener<Void> listener) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                db.collection(User.COLLECTION).document(user.getEmail()).set(user.toJson())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                listener.onComplete(null);
                            }
                        });
            }
        });
    }

    public void uploadImageUser(String name, Bitmap bitmap, Model.Listener<String> listener){
        storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/users/" + name + ".jpg");
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

    public void getProductByName(String name, Model.Listener<Product> callback) {
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
    public void getAllProductsSince(Long since, Model.Listener<List<Product>> callback){
        db = FirebaseFirestore.getInstance();
        db.collection(Product.COLLECTION)
                .whereGreaterThanOrEqualTo(Product.LAST_UPDATED, new Timestamp(since,0))
//                .whereNotEqualTo(Product.OWNEREMAIL,getUser().getEmail())
//                .whereNotEqualTo(Product.CUSTOMEREMAIL,getUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Product> list = new LinkedList<>();
                        if (task.isSuccessful()){
                            QuerySnapshot jsonsList = task.getResult();
                            for (DocumentSnapshot json: jsonsList){
                                Product product = Product.fromJson(json.getData());
                                list.add(product);
                            }
                        }
                        callback.onComplete(list);
                    }
                });
    }


    public void getAllProductsOwnerSince(Long since, Model.Listener<List<Product>> callback){
        db  = FirebaseFirestore.getInstance();
        db.collection(Product.COLLECTION)
                .whereGreaterThanOrEqualTo(Product.LAST_UPDATED, new Timestamp(since,0))
                .whereEqualTo(Product.OWNEREMAIL,getUser().getEmail())
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


    public void getAllProductsCustomerSince(Long since, Model.Listener<List<Product>> callback){
        db  = FirebaseFirestore.getInstance();
        db.collection(Product.COLLECTION)
                .whereGreaterThanOrEqualTo(Product.LAST_UPDATED, new Timestamp(since,0))
                .whereEqualTo(Product.CUSTOMEREMAIL,getUser().getEmail())
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



    public void addProduct(Product pro, Model.Listener<Void> listener) {
        db = FirebaseFirestore.getInstance();
        db.collection(Product.COLLECTION).document(pro.getProductId()).set(pro.toJson())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        listener.onComplete(null);
                    }
                });
    }

//    public void order(String name, String newEmail, Model.Listener<Void> listener) {
//        db = FirebaseFirestore.getInstance();
//        CollectionReference productsRef = db.collection(Product.COLLECTION);
//
//        productsRef.whereEqualTo(Product.NAME, name)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        WriteBatch batch = db.batch();
//                        for (QueryDocumentSnapshot doc : task.getResult()) {
//                            batch.update(doc.getReference(), Product.CUSTOMEREMAIL, newEmail);
//                        }
//                        batch.commit()
//                                .addOnCompleteListener(commitTask -> {
//                                    if (commitTask.isSuccessful()) {
//                                        listener.onComplete(null);
//                                    } else {
//                                        Log.e("TAG", "Error updating documents: ", commitTask.getException());
//                                    }
//                                });
//                    } else {
//                        Log.e("TAG", "Error getting documents: ", task.getException());
//                    }
//                });
//    }

    public void order(Product product, String newEmail,Model.Listener<Void> listener){
        db = FirebaseFirestore.getInstance();
        db.collection(User.COLLECTION).document(Model.instance().getCurrentUser().getEmail())
                .collection("CustomerProducts").document(product.getProductId()).set(product.toJson())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Map<String,Object> map = new HashMap<>();
                        map.put(Product.CUSTOMEREMAIL,newEmail);
                        DocumentReference productReference = db.collection(Product.COLLECTION)
                                .document(product.getProductId());
                        productReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                listener.onComplete(null);
                            }
                        });
                        db.collection(User.COLLECTION).document(Model.instance().getCurrentUser().getEmail())
                                .collection("CustomerProducts").
                                document(product.getProductId()).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                listener.onComplete(null);
                            }
                        });
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




    public void uploadImageProduct(String name, Bitmap bitmap, Model.Listener<String> listener){
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
