package com.example.yad2application;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;

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
import com.squareup.picasso.Picasso;

public class EditProfileFragment extends Fragment {

    FragmentEditProfileBinding binding;
    Boolean isAvatarSelected = false;
    ActivityResultLauncher<Void> cameraLauncher;
    ActivityResultLauncher<String> galleryLauncher;

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
        getParentFragmentManager().setFragmentResultListener("EditUserDetails", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                User user = (User) result.getSerializable("user");
                binding.email.setText(user.getEmail());
                Picasso.get().load(user.getAvatarUrl()).into(binding.avatarImg);

                String newEmail = binding.email.getText().toString();

                Model.instance().updateUserEmail(newEmail,(unused)->{
                    requireActivity().runOnUiThread(() -> {
                        Navigation.findNavController(view).navigate(R.id.firstFragment);
                    });
                });
            }
        });

        binding.cameraButton.setOnClickListener(view1->{
            cameraLauncher.launch(null);
        });

        binding.galleryButton.setOnClickListener(view1->{
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