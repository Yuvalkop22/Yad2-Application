package com.example.yad2application.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

public class FirebaseModel {
    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseModel(){

    }

    public void getAllStudentsSince(Long since, Model.Listener<List<Student>> callback){
        db.collection(Student.COLLECTION)
                .whereGreaterThanOrEqualTo(Student.LAST_UPDATED, new Timestamp(since,0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Student> list = new LinkedList<>();
                if (task.isSuccessful()){
                    QuerySnapshot jsonsList = task.getResult();
                    for (DocumentSnapshot json: jsonsList){
                        Student st = Student.fromJson(json.getData());
                        list.add(st);
                    }
                }
                callback.onComplete(list);
            }
        });
    }

    public void signInUser(Student st, Model.Listener<Void> listener) {
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
        public void addStudent(Student st, Model.Listener<Void> listener) {
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
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
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
