package com.example.yad2application.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
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

    public void getUser(String email, Model.Listener<User> listener) {
        db.collection(User.COLLECTION).whereEqualTo(User.EMAIL,email)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot jsonList = task.getResult();
                            for (DocumentSnapshot json : jsonList) {
                                User user = User.fromJson(json.getData());
                                Log.d("TAG", "User found");
                                listener.onComplete(user);
                            }
                        }
                    }
                });
    }

    public void getAllUsersSince(Long since, Model.Listener<List<User>> callback){
        db = FirebaseFirestore.getInstance();
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

    public void signInUser(String email,String password, Model.Listener<FirebaseUser> listener) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    listener.onComplete(user);
                }else{
                    listener.onComplete(null);
                }
            }
        });
    }

    public void signUpUserFirebase(String email,String password, Model.Listener<FirebaseUser> listener) {
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = auth.getCurrentUser();
                if (task.isSuccessful())
                    listener.onComplete(user);
                else
                    listener.onComplete(null);
            }
        });
    }
    public void addUserToFirebase(User user, Model.Listener<Void> listener){
        db = FirebaseFirestore.getInstance();
        db.collection(User.COLLECTION).document(user.getUserId()).set(user.toJson()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(null);
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
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        db.collection(Product.COLLECTION)
                .whereGreaterThanOrEqualTo(Product.LAST_UPDATED, new Timestamp(since,0))
                .whereEqualTo(Product.OWNEREMAIL,firebaseUser.getEmail())
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
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        db.collection(Product.COLLECTION)
                .whereGreaterThanOrEqualTo(Product.LAST_UPDATED, new Timestamp(since,0))
                .whereEqualTo(Product.CUSTOMEREMAIL,firebaseUser.getEmail())
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



    public void addProduct(Product product, Model.Listener<Void> listener) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        db.collection(User.COLLECTION).document(User.USERID)
                .collection("OwnerProducts").document(product.getProductId()).set(product.toJson())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        db.collection(Product.COLLECTION).document(product.getProductId()).set(product.toJson())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        listener.onComplete(null);
                                    }
                                });
                    }
                });

    }




    public FirebaseUser getCurrentUser(){
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        return firebaseUser;
    }

    public void updateProduct(String productId,String name, String price,String description,Model.Listener<Void> listener){
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        Map<String,Object> map = new HashMap<>();
        map.put(Product.NAME,name);
        map.put(Product.PRICE,price);
        map.put(Product.DESCRIPTION,description);
        map.put(Product.LAST_UPDATED, FieldValue.serverTimestamp());
        DocumentReference productReference = db.collection(Product.COLLECTION)
                .document(productId);
        productReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(null);
            }
        });
        DocumentReference documentReference1 = db.collection(User.COLLECTION).document(firebaseUser.getEmail())
                .collection("OwnerProducts").document(productId);
        documentReference1.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(null);
            }
        });
    }

    public void order(Product product, String newEmail,Model.Listener<Void> listener){
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        db.collection(User.COLLECTION).document(firebaseUser.getEmail())
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
                        db.collection(User.COLLECTION).document(firebaseUser.getEmail())
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

    public void editUserDocument(User user, Model.Listener<User> listener) {
        db = FirebaseFirestore.getInstance();
        db.collection(User.COLLECTION).document(user.getUserId()).set(user.toJson()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    listener.onComplete(user);
                }else{
                    listener.onComplete(null);
                }
            }
        });
    }
    public void editUserFirebase(String email, String password, Model.Listener<FirebaseUser> listener){
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Re-authenticate user
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Update email
                    firebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                listener.onComplete(firebaseUser);
                            }else{
                                listener.onComplete(null);
                            }
                        }
                    });
                } else {
                    listener.onComplete(null);
                }
            }
        });
    }

    public void editEmailFromProducts(String oldEmail,String newEmail,Model.Listener<Boolean> listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Product.COLLECTION)
                .whereEqualTo(Product.OWNEREMAIL,oldEmail)
                .whereEqualTo(Product.CUSTOMEREMAIL,oldEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            listener.onComplete(true);
                        }
                        listener.onComplete(false);
                    }
                });
    }

        public void deleteProduct(Product product, Model.Listener<Void> listener) {
        db = FirebaseFirestore.getInstance();
        db.collection(Product.COLLECTION).document(product.getProductId())
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
        DocumentReference documentReference = db.collection(User.COLLECTION).document(firebaseUser.getEmail())
                .collection("OwnerProducts").document(product.getProductId());
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(null);
            }
        });

        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/products/" + product.getName() + ".jpg");
        imagesRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(null);
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
