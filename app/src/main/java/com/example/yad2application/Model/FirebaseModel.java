package com.example.yad2application.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
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
    public void getAllStudentsSince(Long since, Model.Listener<List<User>> callback){
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

    public void signInUser(User st, Model.Listener<Void> listener) {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (Patterns.EMAIL_ADDRESS.matcher(st.name).matches()) {
            // The email address is valid, create user in Firebase authentication
            auth.signInWithEmailAndPassword(st.name.toString(), st.id.toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // User created successfully
                        listener.onComplete(null);
                    } else {
                        // Failed to create user, print the exception with log
                        Exception exception = task.getException();
                        Log.e("TAG", "Error creating user", exception);
                    }
                }
            });
        } else {
            // The email address is invalid, show an error message to the user
            Log.e("TAG", "The email address is invalid");
        }

    }
        public void addStudent(User st, Model.Listener<Void> listener) {
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        Map<String,Object> user = new HashMap<>();
        user.put("email",st.name);
        user.put("password",st.id);
        db.collection("Users").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                        // Handle the error and provide feedback to the user
                    }
                });

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (Patterns.EMAIL_ADDRESS.matcher(st.name).matches()) {
            // The email address is valid, create user in Firebase authentication
            auth.createUserWithEmailAndPassword(st.name.toString(), st.id.toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // User created successfully
                        listener.onComplete(null);
                    } else {
                        // Failed to create user, print the exception with log
                        Exception exception = task.getException();
                        Log.e("TAG", "Error creating user", exception);
                    }
                }
            });
        } else {
            // The email address is invalid, show an error message to the user
            Log.e("TAG","The email address is invalid");
        }
    }

    public void uploadImage(String name, Bitmap bitmap, Model.Listener<String> listener){
        storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/" + name + ".jpg");
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
