package com.example.yad2application;

import android.os.Bundle;

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

import com.example.yad2application.Model.Model;
import com.example.yad2application.Model.User;
import com.example.yad2application.databinding.FragmentSignInBinding;

public class SignInFragment extends Fragment {
    FragmentSignInBinding binding;

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
                menu.removeItem(R.id.logInFragment);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        },this, Lifecycle.State.RESUMED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater,container,false);
        View view = binding.getRoot();


        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.usernameSignIn.getText().toString();
                String stId = binding.passwordSignIn.getText().toString();
                Log.v("TAG","name = " + name + "," + "password - " + stId);
                User st = new User(stId.toString(),name.toString(),"",false);

                Model.instance().signInUser(st,(unused)->{
                    Navigation.findNavController(view).navigate(R.id.secondFragment);
                });


            }
        });

        binding.cancellBtnSignIn.setOnClickListener(view1 -> Navigation.findNavController(view).navigate(R.id.signUpFragment));


        return view;
    }
}