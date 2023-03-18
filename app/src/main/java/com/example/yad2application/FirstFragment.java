package com.example.yad2application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import com.example.yad2application.Model.Joke;
import com.example.yad2application.Model.JokeModel;
import com.example.yad2application.databinding.FragmentFirstBinding;

import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        setHasOptionsMenu(true);
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.btnLoginFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.signInFragment);
            }
        });

        binding.btnSignupFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.signUpFragment);
            }
        });

        LiveData<List<Joke>> data = JokeModel.instance.getJoke(1);
        data.observe(getViewLifecycleOwner(), list->{
           String joke =  list.get(0).getJoke();
            binding.JokeTextView.setText(joke);
        });
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