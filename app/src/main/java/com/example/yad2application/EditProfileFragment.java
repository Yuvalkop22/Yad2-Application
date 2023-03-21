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
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.yad2application.Model.Product;
import com.example.yad2application.Model.User;
import com.example.yad2application.databinding.FragmentEditProfileBinding;
import com.example.yad2application.databinding.FragmentProductPageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class EditProfileFragment extends Fragment {

    FragmentEditProfileBinding binding;
    Boolean isAvatarSelected = false;
    ActivityResultLauncher<Void> cameraLauncher;
    ActivityResultLauncher<String> galleryLauncher;
    UserViewModel userViewModel;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

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
                if (result != null) {
                    binding.avatarImg.setImageURI(result);
                    isAvatarSelected = true;
                }
            }
        });

        getParentFragmentManager().setFragmentResultListener("EditUserDetails", this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        User user = (User) result.getSerializable("user");
                        if (user != null) {
                            String oldEmail = user.getEmail();
                            binding.email.setText(user.getEmail());
                            Picasso.get().load(user.getAvatarUrl()).into(binding.avatarImg);
                            if (isAvatarSelected) {
                                Bitmap bitmap = ((BitmapDrawable) binding.avatarImg.getDrawable()).getBitmap();
                                Model.instance().uploadImageUser(user.getEmail(), bitmap, url -> {
                                    if (url != null) {
                                        user.setAvatarUrl(String.valueOf(url));
                                    }
                                    binding.update.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String newEmail = binding.email.getText().toString();
                                            auth = FirebaseAuth.getInstance();
                                            Model.instance().updateUser(oldEmail, newEmail, user, (unused) -> {
                                                Toast.makeText(getContext(), "User Updated Successfully", Toast.LENGTH_LONG).show();
                                            });
                                        }
                                    });
                                });
                            } else {
                                binding.update.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String newEmail = binding.email.getText().toString();
                                        auth = FirebaseAuth.getInstance();
                                        Model.instance().updateUser(oldEmail, newEmail, user, (unused) -> {
                                            Toast.makeText(getContext(), "User Updated Successfully", Toast.LENGTH_LONG).show();
                                        });
                                    }
                                });
                            }

                    binding.cancellBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Navigation.findNavController(view).navigate(R.id.profileFragment);
                        }
                    });
                }
            }
        });
        binding.cameraButtonEditProfile.setOnClickListener(view1->{
            cameraLauncher.launch(null);
        });

        binding.galleryButtonEditPrfoile.setOnClickListener(view1->{
            galleryLauncher.launch("image/*");
        });


        FragmentActivity parentActivity = getActivity();
        parentActivity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.removeItem(android.R.id.home);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == android.R.id.home){
                    Navigation.findNavController(view).navigate(R.id.productsListFragment);
                }
                return false;
            }
        },getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }

}