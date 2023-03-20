package com.example.yad2application;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yad2application.Model.Model;
import com.example.yad2application.Model.User;
import com.example.yad2application.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment {
    FragmentSignUpBinding binding;
    ActivityResultLauncher<Void> cameraLauncher;
    ActivityResultLauncher<String> galleryLauncher;
    Boolean isAvatarSelected = false;
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity parentActivity = getActivity();
        setHasOptionsMenu(true);



        parentActivity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.removeItem(R.id.signUpFragment);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        },this, Lifecycle.State.RESUMED);

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
            @Override
            public void onActivityResult(Bitmap result) {
                if (result != null) {
                    binding.avatarImg.setImageBitmap(result);
                    isAvatarSelected = true;
                }
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null){
                    binding.avatarImg.setImageURI(result);
                    isAvatarSelected = true;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater,container,false);
        View view = binding.getRoot();


        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();
                User user = new User(email,password,"");
                if (isAvatarSelected) {
                    binding.avatarImg.setDrawingCacheEnabled(true);
                    binding.avatarImg.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) binding.avatarImg.getDrawable()).getBitmap();
                    Model.instance().uploadImageUser(email, bitmap, url -> {
                        if (url != null) {
                            user.setAvatarUrl(url);
                            Model.instance().addUser(user,(result)->{
                                if (result != null)
                                    Navigation.findNavController(view).navigate(R.id.signInFragment);
                                else {
                                    String pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
                                    if (!email.matches(pattern)){
                                        Toast.makeText(getContext(),"Make sure the email is in the correct format...",Toast.LENGTH_LONG).show();
                                    } else if (password.length() < 6){
                                        Toast.makeText(getContext(),"Make sure the password is longer than 6...",Toast.LENGTH_LONG).show();
                                    }else if (!email.matches(pattern) && password.length() <  6){
                                        Toast.makeText(getContext(),"Problem with email and password",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                }
                else{
                    Toast.makeText(getContext(),"Oops.. You need to upload a profile picture", Toast.LENGTH_LONG).show();
                }

            }
        });

        //binding.cancellBtn.setOnClickListener(view1 -> Navigation.findNavController(view1).popBackStack(R.id.studentsListFragment,false));

        binding.cameraButton.setOnClickListener(view1->{
            cameraLauncher.launch(null);
        });

        binding.galleryButton.setOnClickListener(view1->{
            galleryLauncher.launch("image/*");
        });
        return view;
    }
}